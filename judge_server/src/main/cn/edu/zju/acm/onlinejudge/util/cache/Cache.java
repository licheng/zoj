package cn.edu.zju.acm.onlinejudge.util.cache;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class Cache {
	
	private static final long SLEEP_TIME = 1117; 
	private final Map entries = new HashMap();
	private final TreeSet lastAccessQueue = new TreeSet(new AccessTimeComparator());
	private final TreeSet evictionQueue = new TreeSet(new EvictionTimeComparator());
	private final long timeout;
	private final long capability;
	private final CacheCleaner cacheCleaner;
	
    
    public Set getKeys() {
        synchronized (entries) {
            return new HashSet(entries.keySet());
        }
    }
	public Cache(long timeout, long capability) {
		if (timeout < 1) {
			throw new IllegalArgumentException("time should be positive.");
		}
		if (capability < 0) {
			throw new IllegalArgumentException("capability should be positive.");
		}
		this.timeout = timeout;
		this.capability = capability;
		cacheCleaner = new CacheCleaner();
		cacheCleaner.start();		
	}
	public Object get(Object key) {
		if (key == null) {
			throw new IllegalArgumentException("key is null");
		}
		synchronized (entries) {
			CacheEntry entry = (CacheEntry) entries.get(key);
			if (entry == null) {
				return null;
			}
			CacheKey cacheKey = entry.getKey();
			lastAccessQueue.remove(cacheKey);
			cacheKey.setLastAccessTime(System.currentTimeMillis());
			lastAccessQueue.add(cacheKey);
			return entry.getEntry();
		}		
	}
	
	public void put(Object key, Object value) {
		put(key, value, timeout);
	}	
	
	public void put(Object key, Object value, long timeout) {
		if (key == null) {
			throw new IllegalArgumentException("key is null");
		}
		if (capability == 0) {
			return;
		}
		synchronized (entries) {
			CacheEntry entry = (CacheEntry) entries.get(key);
			CacheKey cacheKey = null;
			long now = System.currentTimeMillis();
			if (entry == null) {				
				cacheKey = new CacheKey(key, now + timeout, now);
				if (entries.size() >= capability) {		
					CacheKey next = (CacheKey) lastAccessQueue.first();
					if (!key.equals(next.getKey())) {
						removeKey(next);
					}									
				}
			} else {
				cacheKey = entry.getKey();
				lastAccessQueue.remove(cacheKey);
				evictionQueue.remove(cacheKey);
				cacheKey.setLastAccessTime(now);		
				cacheKey.setEvictionTime(now + timeout);
			}
			lastAccessQueue.add(cacheKey);
			evictionQueue.add(cacheKey);
			entries.put(key, new CacheEntry(value, cacheKey));				
		}		
	}	
	
	private void removeKey(CacheKey key) {
		lastAccessQueue.remove(key);
		evictionQueue.remove(key);
		entries.remove(key.getKey());
	}
	
	public Object remove(Object key) {
		if (key == null) {
			throw new IllegalArgumentException("key is null");
		}
		synchronized (entries) {
			CacheEntry entry = (CacheEntry) entries.get(key);
			if (entry == null) {
				return null;
			} else {
				removeKey(entry.getKey());
			}
			return entry.getEntry();			
		}
	}
	public boolean contains(Object obj) {
		if (obj == null) {
			throw new IllegalArgumentException("key is null");
		}		
		CacheKey key = new CacheKey(obj);
		synchronized (entries) {
			return entries.containsKey(key);			
		}
	}	

	
	public void finalize() {
		cacheCleaner.quit();
	}
	


	private class AccessTimeComparator implements Comparator {
		public int compare(Object o1, Object o2) {
			CacheKey key1 = (CacheKey) o1;
			CacheKey key2 = (CacheKey) o2;
			
			if (key1.getLastAccessTime() < key2.getLastAccessTime()) {
				return -1; 
			} else if (key1.getLastAccessTime() == key2.getLastAccessTime()) {
				return key1.hashcode() - key2.hashcode();			
			} else {
				return 1;
			}		 						 	
		}
	}
	
	private class EvictionTimeComparator implements Comparator {
		public int compare(Object o1, Object o2) {
			CacheKey key1 = (CacheKey) o1;
			CacheKey key2 = (CacheKey) o2;
			
			if (key1.getEvictionTime() < key2.getEvictionTime()) {
				return -1; 
			} else if (key1.getLastAccessTime() == key2.getLastAccessTime()) {
				return key1.hashcode() - key2.hashcode();			
			} else {
				return 1;
			}		 		
		}
	}
	
	private class CacheCleaner extends Thread {
				
		private boolean quitFlag = false;
		public CacheCleaner() {
			
		}
		public void run() {
			while (!quitFlag) {
									
				see("cleaner - before");
				long now = System.currentTimeMillis();
				synchronized (entries) {														
					for (Iterator it = evictionQueue.iterator(); it.hasNext();) {
						CacheKey key = (CacheKey) it.next();
						if (key.getEvictionTime() < now) {
							see("cleaning - " + key.getKey());
							it.remove();
							evictionQueue.remove(key);
							entries.remove(key.getKey());						
						} else {							
							break;
						}
					}							
				}
				see("cleaner - after");
				try {
					Thread.sleep(SLEEP_TIME);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		public void quit() {
			quitFlag = true;
		}
		
		

	}
	
	private long start = System.currentTimeMillis();
	public void see(String message) {
		if (true) return;
		System.out.println(message + " " + (System.currentTimeMillis() - start));
		synchronized (entries) {
			for (Iterator it = entries.keySet().iterator(); it.hasNext();) {
				Object key = it.next();
				CacheEntry value = (CacheEntry) entries.get(key);
				System.out.print(key + "(" + (value.getKey().getEvictionTime() -start)+ "," + (value.getKey().getLastAccessTime()-start)+ ") - ");
				System.out.println(value.getEntry());						
			}
		}
		System.out.println("-------------");
	}
	
	
	public static void main(String[] a) throws Exception {
		Cache c = new Cache(2000, 3);
		Thread.sleep(500);
		c.put("A", "a");
		
		c.put("B", "b");
		c.put("C", "c", 3000);
		Thread.sleep(100);
		System.out.println(c.get("A"));	
		c.see("**");
		c.put("D", "d", 1000);
					
		Thread.sleep(5000);
		
		c.finalize();
		
	}
}
