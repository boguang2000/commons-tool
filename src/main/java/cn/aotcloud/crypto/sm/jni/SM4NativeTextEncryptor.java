package cn.aotcloud.crypto.sm.jni;

import cn.aotcloud.crypto.EncryptException;
import cn.aotcloud.crypto.sm.SM4TextEncryptor;

import cn.aotcloud.smcrypto.Sm4Utils;
import cn.aotcloud.smcrypto.exception.InvalidCryptoDataException;
import cn.aotcloud.smcrypto.exception.InvalidKeyException;
import cn.aotcloud.smcrypto.exception.InvalidSourceDataException;

/**
 * @author xkxu
 */
public class SM4NativeTextEncryptor extends SM4TextEncryptor {

	public SM4NativeTextEncryptor(String key) {
		super(key);
	}

	public SM4NativeTextEncryptor(String key, String iv, String type) {
		super(key, iv, type);
	}

	/**
	 * @param key
	 * @param hex	KEY是否是16进制格式
	 * @version 1.7.0
	 */
	public SM4NativeTextEncryptor(String key, boolean hex) {
		super(key, hex);
	}

	/**
	 * @param key
	 * @param hex	KEY是否是16进制格式
	 * @param iv
	 * @param type
	 * @version 1.7.0
	 */
	public SM4NativeTextEncryptor(String key, boolean hex, String iv, String type) {
		super(key, hex, iv, type);
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
