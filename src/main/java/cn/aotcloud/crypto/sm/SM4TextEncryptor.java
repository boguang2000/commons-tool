package cn.aotcloud.crypto.sm;

import cn.aotcloud.crypto.EncryptException;

import cn.aotcloud.smcrypto.Sm4Utils;
import cn.aotcloud.smcrypto.exception.InvalidCryptoDataException;
import cn.aotcloud.smcrypto.exception.InvalidKeyException;
import cn.aotcloud.smcrypto.exception.InvalidSourceDataException;
import cn.aotcloud.smcrypto.util.ByteUtils;

import org.springframework.util.Assert;

/**
 * SM4对称加密解密
 * 
 * @author xkxu
 */
public class SM4TextEncryptor extends SMTextEncryptor {

	public static final String SM4_ECB = "ECB";

	public static final String SM4_CBC = "CBC";

	/**
	 * 密钥，16进制格式
	 */
	protected String key;

	/**
	 * CBC 偏移量
	 */
	protected String iv;

	/**
	 * ECB 和 CBC
	 */
	protected String type = "ECB";

	public SM4TextEncryptor(String key) {
		this(key, false);
	}

	/**
	 * @param key
	 * @param hex	KEY是否是16进制格式
	 * @version 1.7.0
	 */
	public SM4TextEncryptor(String key, boolean hex) {
		Assert.notNull(key, "SM4密钥不能为空");
		if (hex) {
			this.key = key;
			if (key.length() < 32) {
				throw new IllegalArgumentException("SM4密钥Hex必须不能小于32位");
			}
		} else {
			if (key.length() < 16) {
				throw new IllegalArgumentException("SM4密钥必须不能小于16位");
			}
			this.key = ByteUtils.stringToHex(key);
		}
	}

	public SM4TextEncryptor(String key, String iv, String type) {
		this(key);
		this.iv = iv;
		this.type = type;
	}

	/**
	 * @param key
	 * @param hex	KEY是否是16进制格式
	 * @param iv
	 * @param type
	 * @version 1.7.0
	 */
	public SM4TextEncryptor(String key, boolean hex, String iv, String type) {
		this(key, hex);
		this.iv = iv;
		this.type = type;
	}

	@Override
	public String encrypt(String text) {
		try {
			if (SM4_ECB.equalsIgnoreCase(type)) {
				return Sm4Utils.ECB.encryptFromText(text, key);
			} else {
				return Sm4Utils.CBC.encryptFromText(text, key, iv);
			}
		} catch (InvalidKeyException | InvalidSourceDataException e) {
			throw new EncryptException(e, EncryptException.ENCRYPT_ERROR_CODE);
		}
	}

	@Override
	public String decrypt(String encryptedText) {
		try {
			if (SM4_ECB.equalsIgnoreCase(type)) {
				return Sm4Utils.ECB.decryptToText(encryptedText, key);
			} else {
				return Sm4Utils.CBC.decryptToText(encryptedText, key, iv);
			}
		} catch (InvalidCryptoDataException | InvalidKeyException e) {
			throw new EncryptException(e, EncryptException.DECRYPT_ERROR_CODE);
		}
	}

}
