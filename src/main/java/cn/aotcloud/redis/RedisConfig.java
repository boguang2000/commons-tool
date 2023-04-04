package cn.aotcloud.redis;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties.Lettuce;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties.Pool;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration.LettucePoolingClientConfigurationBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.aotcloud.prop.RedisSafeProperties;
import cn.aotcloud.smcrypto.Sm4Utils;
import cn.aotcloud.smcrypto.exception.InvalidCryptoDataException;
import cn.aotcloud.smcrypto.exception.InvalidKeyException;

/**
 * bgu
 */
public class RedisConfig {
	
	protected Logger logger = LoggerFactory.getLogger(getClass());

    public LettuceConnectionFactory lettuceConnectionFactory(RedisSafeProperties redisSafeProperties, RedisProperties redisProperties) {
		String pw = getPassword(redisSafeProperties, redisProperties);
		GenericObjectPoolConfig<Object> genericObjectPoolConfig = null;
		LettucePoolingClientConfiguration clientConfig = null;
		Lettuce lettuce = redisProperties.getLettuce();
		if(lettuce != null) {
			Pool lettucePool = redisProperties.getLettuce().getPool();
			if(lettucePool != null) {
				genericObjectPoolConfig = new GenericObjectPoolConfig<Object>();
		        genericObjectPoolConfig.setMaxIdle(lettucePool.getMaxIdle());
		        genericObjectPoolConfig.setMinIdle(lettucePool.getMinIdle());
		        genericObjectPoolConfig.setMaxTotal(lettucePool.getMaxActive());
		        if(lettucePool.getMaxWait() != null) {
		        	genericObjectPoolConfig.setMaxWaitMillis(lettucePool.getMaxWait().getSeconds() * 1000);
		        }
		        if(lettucePool.getTimeBetweenEvictionRuns() != null) {
		        	genericObjectPoolConfig.setTimeBetweenEvictionRunsMillis(lettucePool.getTimeBetweenEvictionRuns().getSeconds() * 1000);
		        }
			}
			
			LettucePoolingClientConfigurationBuilder lettucePoolingClientConfigurationBuilder = LettucePoolingClientConfiguration.builder();
			if(redisProperties.getTimeout() != null) {
				lettucePoolingClientConfigurationBuilder.commandTimeout(redisProperties.getTimeout());
			}
			if(lettuce.getShutdownTimeout() != null) {
				lettucePoolingClientConfigurationBuilder.shutdownTimeout(lettuce.getShutdownTimeout());
			}
			if(genericObjectPoolConfig != null) {
				lettucePoolingClientConfigurationBuilder.poolConfig(genericObjectPoolConfig);
			}
			clientConfig = lettucePoolingClientConfigurationBuilder.build();
		}
			
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
        redisStandaloneConfiguration.setDatabase(redisProperties.getDatabase());
        redisStandaloneConfiguration.setHostName(redisProperties.getHost());
        redisStandaloneConfiguration.setPort(redisProperties.getPort());
        if(StringUtils.isNotBlank(pw)) {
        	redisStandaloneConfiguration.setPassword(RedisPassword.of(pw));
		}
        
        if(clientConfig != null) {
        	//factory.setShareNativeConnection(true);
            //factory.setValidateConnection(false);
        	return new LettuceConnectionFactory(redisStandaloneConfiguration, clientConfig);
        } else {
        	return new LettuceConnectionFactory(redisStandaloneConfiguration);
        }
    }
	
	public String getPassword(RedisSafeProperties redisSafeProperties, RedisProperties redisProperties) {
		String pd = redisProperties.getPassword();
		String pw = redisSafeProperties.getPw();
		if(StringUtils.isNotBlank(pw)) {
			if(StringUtils.startsWith(pw, "enc(")) {
				try {
					pw = StringUtils.substringBetween(pw, "enc(", ")");
					pw = Sm4Utils.CBC.decryptToText(pw, "5261C80B313B514C1A83699E904014A0", "0785E4AD00F457A8370057765B3C155D");
					logger.info("Redis密码解密后装载成功");
				} catch (InvalidCryptoDataException e) {
					logger.error("Redis密码解密失败：{}", e.getMessage());
				} catch (InvalidKeyException e) {
					logger.error("Redis密码解密失败：{}", e.getMessage());
				}
			} else {
				logger.info("Redis密码明文装载成功");
			}
			return pw;
		} else if(StringUtils.isNotBlank(pd)) {
			logger.info("Redis密码默认装载成功");
			return pd;
		} else {
			logger.info("Redis无密码装载成功");
			return null;
		}
	}
}