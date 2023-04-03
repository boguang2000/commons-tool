package cn.aotcloud.utils;

import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

public class RedisUtil {

	public static <K,V> RedisTemplate<K, V> redisSessionTemplate(RedisConnectionFactory factory, Class<?> clz) {
		RedisTemplate<K, V> template = new RedisTemplate<>();
		// 配置连接工厂
		template.setConnectionFactory(factory);
		//JdkSerializationRedisSerializer jdkRedisSerializer = new JdkSerializationRedisSerializer();
		RedisSerializer<String> keySerializer = new StringRedisSerializer();
		RedisSerializer<Object> valueSerializer = new JdkSerializationRedisSerializer(clz.getClassLoader());
		// 值采用json序列化
		template.setValueSerializer(valueSerializer);
		//使用StringRedisSerializer来序列化和反序列化redis的key值
		template.setKeySerializer(keySerializer);
		// 设置hash key 和value序列化模式
		template.setHashKeySerializer(keySerializer);
		template.setHashValueSerializer(valueSerializer);
		template.afterPropertiesSet();
		return template;
    }
	
}
