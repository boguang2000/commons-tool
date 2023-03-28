package cn.aotcloud.logger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.NamedThreadLocal;

public class LoggerHandle {
	
	protected static final NamedThreadLocal<String> USERINFO_THREAD_LOCAL = new NamedThreadLocal<String>("LoggerHandle UserInfo Thread Local");
	
	protected Logger logger = null;
	
	public LoggerHandle(Class<?> clazz) {
		this.logger =  LoggerFactory.getLogger(clazz);
	}
	
	public LoggerHandle(String name) {
		this.logger =  LoggerFactory.getLogger(name);
	}
	
	public void setUserInfo(String userInfo) {
		LoggerHandle.USERINFO_THREAD_LOCAL.set(userInfo);
	}
	
	public String getUserInfo(boolean tab, boolean conf) {
		return null;
	}
	
	public String getUserInfo(boolean tab) {
		return getUserInfo(tab, true);
	}
	
	public boolean isDebugEnabled() {
		return this.logger.isDebugEnabled();
	}
	
	public void debug(String msg) {
		this.logger.debug(getUserInfo(true) + msg);
	}
	
	public void debug(String format, Object... arguments) {
		this.logger.debug(getUserInfo(true) + format, arguments);
	}
	
	public void info(String msg) {
		this.logger.info(getUserInfo(true) + msg);
	}
	
	public void info(boolean conf, String msg) {
		this.logger.info(getUserInfo(true, conf) + msg);
	}
	
	public void info(String format, Object... arguments) {
		this.logger.info(getUserInfo(true) + format, arguments);
	}
	
	public void info(boolean conf, String format, Object... arguments) {
		this.logger.info(getUserInfo(true, conf) + format, arguments);
	}
	
    public void warn(String msg) {
    	this.logger.warn(getUserInfo(true) + msg);
    }
    
    public void warn(Throwable t) {
    	this.logger.warn(getUserInfo(true) + t.getMessage());
    }
    
    public void warn(String format, Object... arguments) {
    	this.logger.warn(getUserInfo(true) + format, arguments);
    }
    
    public void error(String msg) {
    	this.logger.error(getUserInfo(true) + msg);
    }

    public void error(String format, Object... arguments) {
    	this.logger.error(getUserInfo(true) + format, arguments);
    }
    
    public void error(Throwable t) {
    	this.logger.error(getUserInfo(true) + t.getMessage());
    }
    
    public void error(String msg, Throwable t) {
    	this.logger.error(getUserInfo(true) + msg, t);
    }
    
    public void jobInfo(String format, Object... arguments) {
		this.logger.info(format, arguments);
	}
	
    public void jobError(String format, Object... arguments) {
		this.logger.error(format, arguments);
	}
    
    public void taskInfo(String userInfo, String msg) {
		this.logger.info(userInfo + msg);
	}
    
	public void taskInfo(String userInfo, String format, Object... arguments) {
		this.logger.info(userInfo + format, arguments);
	}
	
	public void taskError(String userInfo, String msg) {
    	this.logger.error(userInfo + msg);
    }

    public void taskError(String userInfo, String format, Object... arguments) {
    	this.logger.error(userInfo + format, arguments);
    }
    
    public void taskdebug(String userInfo, String msg) {
		this.logger.debug(userInfo + msg);
	}
    
	public void taskdebug(String userInfo, String format, Object... arguments) {
		this.logger.debug(userInfo + format, arguments);
	}
}
