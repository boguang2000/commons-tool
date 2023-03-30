package cn.aotcloud.cache;

import java.util.Set;

/**
 * 
 * @author bgu
 *
 */
public interface CacheStorage<T> {

	/**
	 * 
	 * @param data
	 */
	void addCache(String key, T value);

	/**
	 * 
	 * @param token
	 * @return
	 */
	T getCache(String key);
	

	/**
	 * 
	 * @param token
	 */
	void deleteCache(String key);

	Set<String> keys();
	
}
