package cn.aotcloud.cache;

import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.util.Assert;

public class RedisCacheStorage<T> implements CacheStorage<T> {

	private final RedisOperations<String, T> redisOperations;
	
	private String prefix;
	
	private final Long expiryTime;
	
	public RedisCacheStorage(RedisConnectionFactory connectionFactory, String prefix, long expiryTime) {
		super();
		this.redisOperations = createDefaultTemplate(connectionFactory);
		this.prefix = prefix + ":";
		this.expiryTime = expiryTime;
	}
	
	private RedisTemplate<String, T> createDefaultTemplate(RedisConnectionFactory connectionFactory) {
		Assert.notNull(connectionFactory, "connectionFactory cannot be null");
		RedisTemplate<String, T> template = new RedisTemplate<String, T>();
		template.setKeySerializer(new StringRedisSerializer());
		template.setHashKeySerializer(new StringRedisSerializer());
		template.setConnectionFactory(connectionFactory);
		template.afterPropertiesSet();
		return template;
	}

	@Override
	public void addCache(String key, T value) {
		if(expiryTime < 0) {
			redisOperations.opsForValue().set(this.prefix + key, value, 24 * 365 * 10, TimeUnit.HOURS);
		} else {
			redisOperations.opsForValue().set(this.prefix + key, value, expiryTime, TimeUnit.SECONDS);
		}
	}

	@Override
	public T getCache(String key) {
		return redisOperations.opsForValue().get(this.prefix +key);
	}
	
	@Override
	public void deleteCache(String key) {
		redisOperations.delete(this.prefix + key);
	}
	
	@Override
	public Set<String> keys() {
		return redisOperations.keys(this.prefix + "*");
	}
}
