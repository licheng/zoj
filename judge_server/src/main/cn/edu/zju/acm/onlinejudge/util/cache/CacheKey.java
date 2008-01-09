package cn.edu.zju.acm.onlinejudge.util.cache;

class CacheKey {
	private long evictionTime;
	private long lastAccessTime;
	private final Object key;
	
	public CacheKey(Object key, long evictionTime, long lastAccessTime) {
		if (key == null) {
			throw new IllegalArgumentException("key is null");
		}
		this.key = key;
		this.evictionTime = evictionTime;
		this.lastAccessTime = lastAccessTime;
	}
	public CacheKey(Object key) {
		this(key, 0, 0);	
	}
	public Object getKey() {
		return key;
	}
	public long getEvictionTime() {
		return evictionTime;
	}
	public void setEvictionTime(long evictionTime) {
		this.evictionTime = evictionTime;
	}
	public long getLastAccessTime() {
		return lastAccessTime;
	}
	public void setLastAccessTime(long lastAccessTime) {
		this.lastAccessTime = lastAccessTime;
	}
	
	public boolean equals(Object obj) {
		if (!(obj instanceof CacheKey)) {
			return false;
		}
		return this.key.equals(((CacheKey) obj).key);		
	}
	
	public int hashcode() {
		return key.hashCode();
	}
}
