/*
 * Copyright 2007 Zhang, Zheng <oldbig@gmail.com>
 * 
 * This file is part of ZOJ.
 * 
 * ZOJ is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either revision 3 of the License, or (at your option) any later revision.
 * 
 * ZOJ is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with ZOJ. if not, see
 * <http://www.gnu.org/licenses/>.
 */

package cn.edu.zju.acm.onlinejudge.util.cache;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class Cache<T> {

    private static final long SLEEP_TIME = 1117;
    private final Map<Object, CacheEntry<T>> entries = new HashMap<Object, CacheEntry<T>>();
    private final TreeSet<CacheKey> lastAccessQueue = new TreeSet<CacheKey>(new AccessTimeComparator());
    private final TreeSet<CacheKey> evictionQueue = new TreeSet<CacheKey>(new EvictionTimeComparator());
    private final long timeout;
    private final long capability;
    private final CacheCleaner cacheCleaner;

    public Set<Object> getKeys() {
        synchronized (entries) {
            return new HashSet<Object>(entries.keySet());
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

    public T get(Object key) {
        if (key == null) {
            throw new IllegalArgumentException("key is null");
        }
        synchronized (entries) {
            CacheEntry<T> entry = entries.get(key);
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

    public void put(Object key, T value) {
        put(key, value, timeout);
    }

    public void put(Object key, T value, long timeout) {
        if (key == null) {
            throw new IllegalArgumentException("key is null");
        }
        if (capability == 0) {
            return;
        }
        synchronized (entries) {
            CacheEntry<T> entry = entries.get(key);
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
            entries.put(key, new CacheEntry<T>(value, cacheKey));
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
            CacheEntry<T> entry = entries.get(key);
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

    private class AccessTimeComparator implements Comparator<CacheKey> {
        public int compare(CacheKey key1, CacheKey key2) {
            if (key1.getLastAccessTime() < key2.getLastAccessTime()) {
                return -1;
            } else if (key1.getLastAccessTime() == key2.getLastAccessTime()) {
                return key1.hashcode() - key2.hashcode();
            } else {
                return 1;
            }
        }
    }

    private class EvictionTimeComparator implements Comparator<CacheKey> {
        public int compare(CacheKey key1, CacheKey key2) {
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
                    for (Iterator<CacheKey> it = evictionQueue.iterator(); it.hasNext();) {
                        CacheKey key = it.next();
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
        if (true)
            return;
        System.out.println(message + " " + (System.currentTimeMillis() - start));
        synchronized (entries) {
            for (Iterator<Object> it = entries.keySet().iterator(); it.hasNext();) {
                Object key = it.next();
                CacheEntry<T> value = entries.get(key);
                System.out.print(key + "(" + (value.getKey().getEvictionTime() - start) + "," +
                        (value.getKey().getLastAccessTime() - start) + ") - ");
                System.out.println(value.getEntry());
            }
        }
        System.out.println("-------------");
    }
}
