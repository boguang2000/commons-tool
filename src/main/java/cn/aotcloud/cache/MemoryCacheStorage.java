package cn.aotcloud.cache;

import java.util.Set;
import java.util.concurrent.TimeUnit;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

public class MemoryCacheStorage<T> implements CacheStorage<T> {

	private final Cache<String, T> cache; 
	
	private String prefix;
	
	private final long expiryTime;
	
	public MemoryCacheStorage(String prefix, long expiryTime) {
		super();
		this.prefix = prefix + ":";
		this.expiryTime = expiryTime;
		this.cache = buildCache(this.expiryTime);
	}

	@Override
	public void addCache(String key, T value) {
		cache.put(prefix + key, value);
	}

	@Override
	public T getCache(String key) {
		return cache.getIfPresent(prefix + key);
	}
	
	@Override
	public void deleteCache(String key) {
		cache.invalidate(prefix + key);
	}

	@Override
	public Set<String> keys() {
		return cache.asMap().keySet();
	}
	
	private Cache<String, T> buildCache(long cacheTime) {
		if(cacheTime < 0 ) {
			return CacheBuilder.newBuilder().expireAfterWrite(24 * 365 * 10, TimeUnit.HOURS).build();
		} else {
			return CacheBuilder.newBuilder().expireAfterWrite(cacheTime, TimeUnit.SECONDS).build();
		}
	}
}
