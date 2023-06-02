package cn.aotcloud.crypto.sm.jni;

import cn.aotcloud.crypto.EncryptException;
import cn.aotcloud.crypto.sm.SM3TextEncryptor;

import cn.aotcloud.smcrypto.Sm3Utils;
import cn.aotcloud.smcrypto.exception.InvalidSourceDataException;

/**
 * @author xkxu
 */
public class SM3NativeTextEncryptor extends SM3TextEncryptor {

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
