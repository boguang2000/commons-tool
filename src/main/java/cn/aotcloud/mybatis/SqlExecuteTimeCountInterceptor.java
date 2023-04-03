package cn.aotcloud.mybatis;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.session.SessionProperties;
import org.springframework.boot.autoconfigure.session.StoreType;
import org.springframework.data.redis.connection.RedisConnectionFactory;

import com.google.common.collect.Maps;

import cn.aotcloud.cache.CacheStorage;
import cn.aotcloud.cache.MemoryCacheStorage;
import cn.aotcloud.cache.NoneCacheStorage;
import cn.aotcloud.cache.RedisCacheStorage;

import java.lang.reflect.Method;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
 
public class SqlExecuteTimeCountInterceptor implements Interceptor {

	protected static Logger logger = LoggerFactory.getLogger(SqlExecuteTimeCountInterceptor.class);
	
	private Map<String, String> debugClassName = Maps.newHashMap();
	
	private final static String DEFAULT_CONSTANTS = "ac:constants:debug";
	
	private static CacheStorage<String> cacheStorage;
	
	public SqlExecuteTimeCountInterceptor(SessionProperties sessionProperties, RedisConnectionFactory connectionFactory, boolean isEnableDebug) {
		if (sessionProperties != null && sessionProperties.getStoreType() == StoreType.REDIS) {
			cacheStorage = new RedisCacheStorage<String>(connectionFactory, DEFAULT_CONSTANTS, -1);
		} else if (sessionProperties != null && sessionProperties.getStoreType() == StoreType.NONE) { // 升级到SpringBoot2.X HASH_MAP --> NONE
			cacheStorage = new MemoryCacheStorage<String>(DEFAULT_CONSTANTS, -1);
		} else {
			cacheStorage = new NoneCacheStorage<String>();
		}
		setDebugEnabled(isEnableDebug);
	}
	
	public void setDebugClassName(Map<String, String> debugClassName) {
		this.debugClassName = debugClassName;
	}
	
	public static void setDebugEnabled(boolean debugEnabled) {
		cacheStorage.addCache("debugEnabled", String.valueOf(debugEnabled));
	}
	
	public static boolean getDebugEnabled() {
		String debugEnabled = cacheStorage.getCache("debugEnabled");
		return StringUtils.equalsIgnoreCase(debugEnabled, "true");
	}
	
	@Override
	public Object intercept(Invocation invocation) throws Throwable {
		return invocation.proceed();
	}
   
    public Object intercept(Invocation invocation, Method saveQueueMethod) throws Throwable {
    	// 获取xml中的一个select/update/insert/delete节点，是一条SQL语句
        MappedStatement mappedStatement = (MappedStatement)invocation.getArgs()[0];
        // 获取到节点的id, 即sql语句的id
        String sqlId = mappedStatement.getId();
        String className = StringUtils.substringBeforeLast(sqlId, ".");
        if(getDebugEnabled() && this.debugClassName.containsKey(className)) {
	    	long startTime = System.currentTimeMillis();
	    	String sql = null;
	        try {
	            Object parameter = null;
	            // 获取参数，if语句成立，表示sql语句有参数，参数格式是map形式
	            if (invocation.getArgs().length > 1) {
	                parameter = invocation.getArgs()[1];
	            }
	            // BoundSql就是封装myBatis最终产生的sql类
	            BoundSql boundSql = mappedStatement.getBoundSql(parameter);
	            // 获取节点的配置
	            Configuration configuration = mappedStatement.getConfiguration();
	            // 获取到最终的sql语句
	            sql = this.getSql(configuration, boundSql);
	            // 执行完上面的任务后，不改变原有的sql执行过程
	            return invocation.proceed();
	        } finally {
	            long timeCount = System.currentTimeMillis() - startTime;
	            if(saveQueueMethod != null) {
		            Map<String, String> map = new HashMap<String, String>();
		            map.put("debugType", "数据库调试");
		            map.put("debugModel", this.debugClassName.get(className));
		            map.put("debugContent", sql);
		            map.put("debugTime", String.valueOf(timeCount));
		            map.put("debugResult", "成功");
		            saveQueueMethod.invoke(null, map);
	            }
	            logger.info( "执行SQL语句：" + sql + " 耗时："+timeCount+"ms");
	        }
        } else {
        	return invocation.proceed();
        }
    }
 
    // 封装了一下sql语句，使得结果返回完整xml路径下的sql语句节点id + sql语句
    private String getSql(Configuration configuration, BoundSql boundSql) {
        String sql = this.showSql(configuration, boundSql);
        StringBuilder str = new StringBuilder(512);
        str.append("调试执行SQL语句");
        str.append(" : ");
        str.append(sql);
        return str.toString();
    }
 
    // 如果参数是String，则添加单引号， 如果是日期，则转换为时间格式器并加单引号； 对参数是null和不是null的情况作了处理
    private String getParameterValue(Object obj) {
        String value = null;
        if (obj instanceof String) {
            value = "'" + obj.toString() + "'";
        } else if (obj instanceof Date) {
            DateFormat formatter = DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.DEFAULT, Locale.CHINA);
            value = "'" + formatter.format(new Date()) + "'";
        } else {
            if (obj != null) {
                value = obj.toString();
            } else {
                value = "";
            }
        }
        return value;
    }
 
    // 进行？的替换
    private String showSql(Configuration configuration, BoundSql boundSql) {
        // 获取参数
        Object parameterObject = boundSql.getParameterObject();
        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
        // sql语句中多个空格都用一个空格代替
        String sql = boundSql.getSql().replaceAll("[\\s]+", " ");
        if (CollectionUtils.isNotEmpty(parameterMappings) && parameterObject != null) {
            // 获取类型处理器注册器，类型处理器的功能是进行java类型和数据库类型的转换
            TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
            // 如果根据parameterObject.getClass(）可以找到对应的类型，则替换
            if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {
                sql = sql.replaceFirst("\\?", Matcher.quoteReplacement(getParameterValue(parameterObject)));
            } else {
                // MetaObject主要是封装了originalObject对象，提供了get和set的方法用于获取和设置originalObject的属性值,主要支持对JavaBean、Collection、Map三种类型对象的操作
                MetaObject metaObject = configuration.newMetaObject(parameterObject);
                for (ParameterMapping parameterMapping : parameterMappings) {
                    String propertyName = parameterMapping.getProperty();
                    if (metaObject.hasGetter(propertyName)) {
                        Object obj = metaObject.getValue(propertyName);
                        sql = sql.replaceFirst("\\?", Matcher.quoteReplacement(getParameterValue(obj)));
                    } else if (boundSql.hasAdditionalParameter(propertyName)) {
                        // 该分支是动态sql
                        Object obj = boundSql.getAdditionalParameter(propertyName);
                        sql = sql.replaceFirst("\\?", Matcher.quoteReplacement(getParameterValue(obj)));
                    } else {
                        // 打印出缺失，提醒该参数缺失并防止错位
                        sql = sql.replaceFirst("\\?", "缺失");
                    }
                }
            }
        }
        return sql;
    }
 
    @Override
    public Object plugin(Object target) {
        return Interceptor.super.plugin(target);
    }
 
    @Override
    public void setProperties(Properties properties) {
        Interceptor.super.setProperties(properties);
    }
    
}