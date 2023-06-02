package cn.aotcloud.crypto.sm;

import cn.aotcloud.crypto.EncryptException;

import cn.aotcloud.smcrypto.Sm3Utils;
import cn.aotcloud.smcrypto.exception.InvalidSourceDataException;

/**
 * SM3加密
 * 
 * @author xkxu
 */
public class SM3TextEncryptor extends SMTextEncryptor {

	private static final SM3TextEncryptor textEncryptor = new SM3TextEncryptor();

	public static SM3TextEncryptor getInstance() {
		return textEncryptor;
	}

	public SM3TextEncryptor() {
	}

	@Override
	public String encrypt(String text) {
		try {
			return Sm3Utils.encryptFromText(text);
		} catch (InvalidSourceDataException e) {
			throw new EncryptException(e, EncryptException.ENCRYPT_ERROR_CODE);
		}
	}

	@Override
	public String decrypt(String encryptedText) {
		throw new UnsupportedOperationException("SM3加密是不可逆的。");
	}

}
