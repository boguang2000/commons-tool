package cn.aotcloud.crypto;

/**
 * 支持的文本字符串加密类型
 * 
 * @author xkxu
 */
public enum TextCryptoType {

	/**
	 * 未加密，直接返回输入的文本
	 */
	none, 
	
	/**
	 * RSA非对称加密，通过配置参数这是证书
	 */
	rsa, 
	
	/**
	 * 扩展RSA非对称加密，主要是为了配合前端JS加密传输，使用程序自动生成的密钥对
	 */
	ex_rsa, 
	
	/**
	 * 国密非对称加密
	 */
	sm2, 
	
	/**
	 * 国密对称加密
	 */
	sm4, 
	
	/**
	 * 国密非对称加密，主要用于用户密码提交，解密后得到采用SM3加密后的文本
	 */
	sm2tosm3, 
	
	/**
	 * 国密非对称加密，主要用于用户密码提交，解密后得到明文
	 */
	sm2toplain;
}
