package cn.aotcloud.prop;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

import cn.aotcloud.smcrypto.Sm4Utils;
import cn.aotcloud.smcrypto.exception.InvalidCryptoDataException;
import cn.aotcloud.smcrypto.exception.InvalidKeyException;

@ConfigurationProperties(prefix = "spring.datasource")
public class HikariProperties extends DataSourceProperties {
	
	protected Logger logger = LoggerFactory.getLogger(getClass());
	
	private String dz;
	
	private String un;
	
	private String pw;

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
	
	@Override
	public String getUrl() {
		String url = super.getUrl();
		if(StringUtils.isNotBlank(super.getUrl())) {
			logger.info("数据库地址默认装载成功");
		} else if(StringUtils.startsWith(this.getDz(), "enc(")) {
			url = StringUtils.substringBetween(this.getDz(), "enc(", ")");
			try {
				url = Sm4Utils.CBC.decryptToText(url, "5261C80B313B514C1A83699E904014A0", "0785E4AD00F457A8370057765B3C155D");
				logger.info("数据库地址解密后装载成功");
			} catch (InvalidCryptoDataException | InvalidKeyException e) {
				logger.error("数据库地址解密失败：{}", e.getMessage());
			}
		} else {
			url = this.getDz();
			logger.info("数据库地址明文装载成功");
		}
		
		return url;
	}
	
	@Override
	public String getUsername() {
		String username = super.getUsername();
		if(StringUtils.isNotBlank(super.getUsername())) {
			logger.info("数据库用户名默认装载成功");
		} else if(StringUtils.startsWith(this.getUn(), "enc(")) {
			username = StringUtils.substringBetween(this.getUn(), "enc(", ")");
			try {
				username = Sm4Utils.CBC.decryptToText(username, "5261C80B313B514C1A83699E904014A0", "0785E4AD00F457A8370057765B3C155D");
				logger.info("数据库用户名解密后装载成功");
			} catch (InvalidCryptoDataException | InvalidKeyException e) {
				logger.error("数据库用户名解密失败：{}", e.getMessage());
			}
		} else {
			username = this.getUn();
			logger.info("数据库用户名明文装载成功");
		}
		
		return username;
	}
	
	@Override
	public String getPassword() {
		String password = super.getPassword();
		if(StringUtils.isNotBlank(super.getPassword())) {
			logger.info("数据库密码默认装载成功");
		} else if(StringUtils.startsWith(this.getPw(), "enc(")) {
			password = StringUtils.substringBetween(this.getPw(), "enc(", ")");
			try {
				password = Sm4Utils.CBC.decryptToText(password, "5261C80B313B514C1A83699E904014A0", "0785E4AD00F457A8370057765B3C155D");
				logger.info("数据库密码解密后装载成功");
			} catch (InvalidCryptoDataException | InvalidKeyException e) {
				logger.error("数据库密码解密失败：{}", e.getMessage());
			}
		} else {
			password = this.getPw();
			logger.info("数据库密码明文装载成功");
		}
		
		return password;
	}
	
//	public static void main(String[] args) throws InvalidSourceDataException, InvalidKeyException {
//	System.out.println(Sm4Utils.CBC.encryptFromText("appcenter", "5261C80B313B514C1A83699E904014A0", "0785E4AD00F457A8370057765B3C155D"));
//	System.out.println(Sm4Utils.CBC.encryptFromText("root", "5261C80B313B514C1A83699E904014A0", "0785E4AD00F457A8370057765B3C155D"));
//	System.out.println(Sm4Utils.CBC.encryptFromText("Sjftys@9162igw", "5261C80B313B514C1A83699E904014A0", "0785E4AD00F457A8370057765B3C155D"));
//	System.out.println(Sm4Utils.CBC.encryptFromText("jdbc:mysql://27.76.33.206:13306/appcenter?useLegacyDatetimeCode=false&serverTimezone=Asia/Shanghai&useunicode=true&characterEncoding=utf-8", "5261C80B313B514C1A83699E904014A0", "0785E4AD00F457A8370057765B3C155D"));
//}
}
