package com.kylantraynor.cache;

import java.util.ArrayList;
import java.util.Iterator;

public class Cache<K, T> {
	private long timeToLive;
    private LRUCache<K, CacheObject> cacheMap;
 
    protected class CacheObject {
        public long lastAccessed = System.currentTimeMillis();
        public T value;
        public K key;
 
        protected CacheObject(T value, K key) {
            this.value = value;
            this.key = key;
        }
    }
 
    public Cache(long timeToLive, final long timerInterval, int maxItems) {
        this.timeToLive = timeToLive * 1000;
 
        cacheMap = new LRUCache<K, CacheObject>(maxItems);
 
        if (timeToLive > 0 && timerInterval > 0) {
 
            Thread t = new Thread(new Runnable() {
                public void run() {
                    while (true) {
                        try {
                            Thread.sleep(timerInterval * 1000);
                        } catch (InterruptedException ex) {
                        }
                        cleanup();
                    }
                }
            });
 
            t.setDaemon(true);
            t.start();
        }
    }
 
    public T put(K key, T value) {
        synchronized (cacheMap) {
            CacheObject o = cacheMap.put(key, new CacheObject(value, key));
            T oldValue = null;
            if(o != null){
            	oldValue = o.value;
            }
            return oldValue;
        }
    }
 
    public T get(K key) {
        synchronized (cacheMap) {
            CacheObject c = (CacheObject) cacheMap.get(key);
 
            if (c == null)
                return null;
            else if(c.value != null) {
                c.lastAccessed = System.currentTimeMillis();
                return c.value;
            } else {
            	return null;
            }
        }
    }
 
    public void remove(K key) {
        synchronized (cacheMap) {
            cacheMap.remove(key);
        }
    }
 
    public int size() {
        synchronized (cacheMap) {
            return cacheMap.size();
        }
    }
 
    public void cleanup() {
 
        long now = System.currentTimeMillis();
        ArrayList<K> deleteKey = null;
 
        synchronized (cacheMap) {
            Iterator<K> itr = cacheMap.keySet().iterator();
 
            deleteKey = new ArrayList<K>((cacheMap.size() / 2) + 1);
            K key = null;
            CacheObject c = null;
 
            while (itr.hasNext()) {
                key = (K) itr.next();
                c = (CacheObject) cacheMap.get(key);
 
                if (c != null && (now > (timeToLive + c.lastAccessed))) {
                    deleteKey.add(key);
                }
            }
        }
 
        for (K key : deleteKey) {
            synchronized (cacheMap) {
                cacheMap.remove(key);
            }
 
            Thread.yield();
        }
    }
}
