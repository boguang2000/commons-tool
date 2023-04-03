package cn.aotcloud.prop;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;

import cn.aotcloud.smcrypto.Sm4Utils;
import cn.aotcloud.smcrypto.exception.InvalidCryptoDataException;
import cn.aotcloud.smcrypto.exception.InvalidKeyException;

@ConfigurationProperties(prefix = "spring.datasource.c3p0")
public class C3P0Properties {
	protected Logger logger = LoggerFactory.getLogger(getClass());
	private String driverClass;
	private String jdbcUrl;
	private String user;
	private String password;
	private String dz;
	private String un;
	private String pw;
	private int initialPoolSize = 3;
	private int maxPoolSize = 15;
	private int minPoolSize = 3;
	private int maxIdleTime = 0;
	private int maxIdleTimeExcessConnections = 0;
	private int maxStatements = 0;
	private int maxStatementsPerConnection = 0;
	private int maxConnectionAge = 0;
	private int maxAdministrativeTaskTime = 0;
	private int numHelperThreads = 3;
	private int idleConnectionTestPeriod = 0;
	private int acquireIncrement = 3;
	private int acquireRetryAttempts = 30;
	private int acquireRetryDelay = 1000;
	private boolean autoCommitOnClose = false;
	private String automaticTestTable;
	private boolean breakAfterAcquireFailure = false;
	private int checkoutTimeout = 0;
	private String connectionCustomizerClassName;
	private String connectionTesterClassName = "com.mchange.v2.c3p0.impl.DefaultConnectionTester";
	private String contextClassLoaderSource = "caller";
	private String dataSourceName = "dataSource";
	private boolean debugUnreturnedConnectionStackTraces = false;
	private String factoryClassLocation;
	private boolean forceIgnoreUnresolvedTransactions = false;
	private boolean forceSynchronousCheckins = false;
	private boolean forceUseNamedDriverClass = false;
	private String overrideDefaultUser;
	private String overrideDefaultPassword;
	private String preferredTestQuery;
	private boolean privilegeSpawnedThreads = false;
	private int propertyCycle = 0;
	private int statementCacheNumDeferredCloseThreads = 0;
	private boolean testConnectionOnCheckin = false;
	private boolean testConnectionOnCheckout = false;
	private int unreturnedConnectionTimeout = 0;
	private Map<Object, Object> extensions = new HashMap<Object, Object>();
	private boolean usesTraditionalReflectiveProxies = false;
	public String getDriverClass() {
		return driverClass;
	}
	public void setDriverClass(String driverClass) {
		this.driverClass = driverClass;
	}
	public String getJdbcUrl() {
		if(StringUtils.isNotBlank(this.jdbcUrl)) {
			logger.info("数据库地址默认装载成功");
		} else if(StringUtils.startsWith(this.getDz(), "enc(")) {
			this.jdbcUrl = StringUtils.substringBetween(this.getDz(), "enc(", ")");
			try {
				this.jdbcUrl = Sm4Utils.CBC.decryptToText(this.jdbcUrl, "5261C80B313B514C1A83699E904014A0", "0785E4AD00F457A8370057765B3C155D");
				logger.info("数据库地址解密后装载成功");
			} catch (InvalidCryptoDataException | InvalidKeyException e) {
				logger.error("数据库地址解密失败：{}", e.getMessage());
			}
		} else {
			this.jdbcUrl = this.getDz();
			logger.info("数据库地址明文装载成功");
		}
		
		return this.jdbcUrl;
	}
	public void setJdbcUrl(String jdbcUrl) {
		this.jdbcUrl = jdbcUrl;
	}
	public String getUser() {
		if(StringUtils.isNotBlank(this.user)) {
			logger.info("数据库用户名默认装载成功");
		} else if(StringUtils.startsWith(this.getUn(), "enc(")) {
			this.user = StringUtils.substringBetween(this.getUn(), "enc(", ")");
			try {
				this.user = Sm4Utils.CBC.decryptToText(this.user, "5261C80B313B514C1A83699E904014A0", "0785E4AD00F457A8370057765B3C155D");
				logger.info("数据库用户名解密后装载成功");
			} catch (InvalidCryptoDataException | InvalidKeyException e) {
				logger.error("数据库用户名解密失败：{}", e.getMessage());
			}
		} else {
			this.user = this.getUn();
			logger.info("数据库用户名明文装载成功");
		}
		
		return this.user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getPassword() {
		if(StringUtils.isNotBlank(this.password)) {
			logger.info("数据库密码默认装载成功");
		} else if(StringUtils.startsWith(this.getPw(), "enc(")) {
			this.password = StringUtils.substringBetween(this.getPw(), "enc(", ")");
			try {
				this.password = Sm4Utils.CBC.decryptToText(this.password, "5261C80B313B514C1A83699E904014A0", "0785E4AD00F457A8370057765B3C155D");
				logger.info("数据库密码解密后装载成功");
			} catch (InvalidCryptoDataException | InvalidKeyException e) {
				logger.error("数据库密码解密失败：{}", e.getMessage());
			}
		} else {
			this.password = this.getPw();
			logger.info("数据库密码明文装载成功");
		}
		
		return this.password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getDz() {
		return dz;
	}
	public void setDz(String dz) {
		this.dz = dz;
	}
	public String getUn() {
		return un;
	}
	public void setUn(String un) {
		this.un = un;
	}
	public String getPw() {
		return pw;
	}
	public void setPw(String pw) {
		this.pw = pw;
	}
	public int getInitialPoolSize() {
		return initialPoolSize;
	}
	public void setInitialPoolSize(int initialPoolSize) {
		this.initialPoolSize = initialPoolSize;
	}
	public int getMaxPoolSize() {
		return maxPoolSize;
	}
	public void setMaxPoolSize(int maxPoolSize) {
		this.maxPoolSize = maxPoolSize;
	}
	public int getMinPoolSize() {
		return minPoolSize;
	}
	public void setMinPoolSize(int minPoolSize) {
		this.minPoolSize = minPoolSize;
	}
	public int getMaxIdleTime() {
		return maxIdleTime;
	}
	public void setMaxIdleTime(int maxIdleTime) {
		this.maxIdleTime = maxIdleTime;
	}
	public int getMaxIdleTimeExcessConnections() {
		return maxIdleTimeExcessConnections;
	}
	public void setMaxIdleTimeExcessConnections(int maxIdleTimeExcessConnections) {
		this.maxIdleTimeExcessConnections = maxIdleTimeExcessConnections;
	}
	public int getMaxStatements() {
		return maxStatements;
	}
	public void setMaxStatements(int maxStatements) {
		this.maxStatements = maxStatements;
	}
	public int getMaxStatementsPerConnection() {
		return maxStatementsPerConnection;
	}
	public void setMaxStatementsPerConnection(int maxStatementsPerConnection) {
		this.maxStatementsPerConnection = maxStatementsPerConnection;
	}
	public int getMaxConnectionAge() {
		return maxConnectionAge;
	}
	public void setMaxConnectionAge(int maxConnectionAge) {
		this.maxConnectionAge = maxConnectionAge;
	}
	public int getMaxAdministrativeTaskTime() {
		return maxAdministrativeTaskTime;
	}
	public void setMaxAdministrativeTaskTime(int maxAdministrativeTaskTime) {
		this.maxAdministrativeTaskTime = maxAdministrativeTaskTime;
	}
	public int getNumHelperThreads() {
		return numHelperThreads;
	}
	public void setNumHelperThreads(int numHelperThreads) {
		this.numHelperThreads = numHelperThreads;
	}
	public int getIdleConnectionTestPeriod() {
		return idleConnectionTestPeriod;
	}
	public void setIdleConnectionTestPeriod(int idleConnectionTestPeriod) {
		this.idleConnectionTestPeriod = idleConnectionTestPeriod;
	}
	public int getAcquireIncrement() {
		return acquireIncrement;
	}
	public void setAcquireIncrement(int acquireIncrement) {
		this.acquireIncrement = acquireIncrement;
	}
	public int getAcquireRetryAttempts() {
		return acquireRetryAttempts;
	}
	public void setAcquireRetryAttempts(int acquireRetryAttempts) {
		this.acquireRetryAttempts = acquireRetryAttempts;
	}
	public int getAcquireRetryDelay() {
		return acquireRetryDelay;
	}
	public void setAcquireRetryDelay(int acquireRetryDelay) {
		this.acquireRetryDelay = acquireRetryDelay;
	}
	public boolean isAutoCommitOnClose() {
		return autoCommitOnClose;
	}
	public void setAutoCommitOnClose(boolean autoCommitOnClose) {
		this.autoCommitOnClose = autoCommitOnClose;
	}
	public String getAutomaticTestTable() {
		return automaticTestTable;
	}
	public void setAutomaticTestTable(String automaticTestTable) {
		this.automaticTestTable = automaticTestTable;
	}
	public boolean isBreakAfterAcquireFailure() {
		return breakAfterAcquireFailure;
	}
	public void setBreakAfterAcquireFailure(boolean breakAfterAcquireFailure) {
		this.breakAfterAcquireFailure = breakAfterAcquireFailure;
	}
	public int getCheckoutTimeout() {
		return checkoutTimeout;
	}
	public void setCheckoutTimeout(int checkoutTimeout) {
		this.checkoutTimeout = checkoutTimeout;
	}
	public String getConnectionCustomizerClassName() {
		return connectionCustomizerClassName;
	}
	public void setConnectionCustomizerClassName(String connectionCustomizerClassName) {
		this.connectionCustomizerClassName = connectionCustomizerClassName;
	}
	public String getConnectionTesterClassName() {
		return connectionTesterClassName;
	}
	public void setConnectionTesterClassName(String connectionTesterClassName) {
		this.connectionTesterClassName = connectionTesterClassName;
	}
	public String getContextClassLoaderSource() {
		return contextClassLoaderSource;
	}
	public void setContextClassLoaderSource(String contextClassLoaderSource) {
		this.contextClassLoaderSource = contextClassLoaderSource;
	}
	public String getDataSourceName() {
		return dataSourceName;
	}
	public void setDataSourceName(String dataSourceName) {
		this.dataSourceName = dataSourceName;
	}
	public boolean isDebugUnreturnedConnectionStackTraces() {
		return debugUnreturnedConnectionStackTraces;
	}
	public void setDebugUnreturnedConnectionStackTraces(boolean debugUnreturnedConnectionStackTraces) {
		this.debugUnreturnedConnectionStackTraces = debugUnreturnedConnectionStackTraces;
	}
	public String getFactoryClassLocation() {
		return factoryClassLocation;
	}
	public void setFactoryClassLocation(String factoryClassLocation) {
		this.factoryClassLocation = factoryClassLocation;
	}
	public boolean isForceIgnoreUnresolvedTransactions() {
		return forceIgnoreUnresolvedTransactions;
	}
	public void setForceIgnoreUnresolvedTransactions(boolean forceIgnoreUnresolvedTransactions) {
		this.forceIgnoreUnresolvedTransactions = forceIgnoreUnresolvedTransactions;
	}
	public boolean isForceSynchronousCheckins() {
		return forceSynchronousCheckins;
	}
	public void setForceSynchronousCheckins(boolean forceSynchronousCheckins) {
		this.forceSynchronousCheckins = forceSynchronousCheckins;
	}
	public boolean isForceUseNamedDriverClass() {
		return forceUseNamedDriverClass;
	}
	public void setForceUseNamedDriverClass(boolean forceUseNamedDriverClass) {
		this.forceUseNamedDriverClass = forceUseNamedDriverClass;
	}
	public String getOverrideDefaultUser() {
		return overrideDefaultUser;
	}
	public void setOverrideDefaultUser(String overrideDefaultUser) {
		this.overrideDefaultUser = overrideDefaultUser;
	}
	public String getOverrideDefaultPassword() {
		return overrideDefaultPassword;
	}
	public void setOverrideDefaultPassword(String overrideDefaultPassword) {
		this.overrideDefaultPassword = overrideDefaultPassword;
	}
	public String getPreferredTestQuery() {
		return preferredTestQuery;
	}
	public void setPreferredTestQuery(String preferredTestQuery) {
		this.preferredTestQuery = preferredTestQuery;
	}
	public boolean isPrivilegeSpawnedThreads() {
		return privilegeSpawnedThreads;
	}
	public void setPrivilegeSpawnedThreads(boolean privilegeSpawnedThreads) {
		this.privilegeSpawnedThreads = privilegeSpawnedThreads;
	}
	public int getPropertyCycle() {
		return propertyCycle;
	}
	public void setPropertyCycle(int propertyCycle) {
		this.propertyCycle = propertyCycle;
	}
	public int getStatementCacheNumDeferredCloseThreads() {
		return statementCacheNumDeferredCloseThreads;
	}
	public void setStatementCacheNumDeferredCloseThreads(int statementCacheNumDeferredCloseThreads) {
		this.statementCacheNumDeferredCloseThreads = statementCacheNumDeferredCloseThreads;
	}
	public boolean isTestConnectionOnCheckin() {
		return testConnectionOnCheckin;
	}
	public void setTestConnectionOnCheckin(boolean testConnectionOnCheckin) {
		this.testConnectionOnCheckin = testConnectionOnCheckin;
	}
	public boolean isTestConnectionOnCheckout() {
		return testConnectionOnCheckout;
	}
	public void setTestConnectionOnCheckout(boolean testConnectionOnCheckout) {
		this.testConnectionOnCheckout = testConnectionOnCheckout;
	}
	public int getUnreturnedConnectionTimeout() {
		return unreturnedConnectionTimeout;
	}
	public void setUnreturnedConnectionTimeout(int unreturnedConnectionTimeout) {
		this.unreturnedConnectionTimeout = unreturnedConnectionTimeout;
	}
	public Map<Object, Object> getExtensions() {
		return extensions;
	}
	public void setExtensions(Map<Object, Object> extensions) {
		this.extensions = extensions;
	}
	public boolean isUsesTraditionalReflectiveProxies() {
		return usesTraditionalReflectiveProxies;
	}
	public void setUsesTraditionalReflectiveProxies(boolean usesTraditionalReflectiveProxies) {
		this.usesTraditionalReflectiveProxies = usesTraditionalReflectiveProxies;
	}
}
