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
        return this.key;
    }

    public long getEvictionTime() {
        return this.evictionTime;
    }

    public void setEvictionTime(long evictionTime) {
        this.evictionTime = evictionTime;
    }

    public long getLastAccessTime() {
        return this.lastAccessTime;
    }

    public void setLastAccessTime(long lastAccessTime) {
        this.lastAccessTime = lastAccessTime;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof CacheKey)) {
            return false;
        }
        return this.key.equals(((CacheKey) obj).key);
    }

    public int hashcode() {
        return this.key.hashCode();
    }
}
