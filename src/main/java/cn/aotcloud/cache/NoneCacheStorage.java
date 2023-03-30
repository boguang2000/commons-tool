package cn.aotcloud.cache;

import java.util.Set;

/**
 * 
 * @author bgu
 *
 */
public class NoneCacheStorage<T> implements CacheStorage<T> {

	@Override
	public void addCache(String key, T value) {
	}

	@Override
	public T getCache(String key) {
		return null;
	}

	@Override
	public void deleteCache(String key) {
	}
	
	@Override
	public Set<String> keys() {
		return null;
	}
}
