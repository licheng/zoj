package cn.edu.zju.acm.onlinejudge.util.cache;

class CacheEntry {
	private final Object entry;
	private final CacheKey key;
	public CacheEntry(Object entry, CacheKey key) {
		this.entry = entry;
		this.key = key;		
	}
	public Object getEntry() {
		return entry;
	}
	
	public CacheKey getKey() {
		return key;
	}
}
