package cn.aotcloud.crypto.sm.jni;

import cn.aotcloud.crypto.EncryptException;
import cn.aotcloud.crypto.EncryptionProperties;
import cn.aotcloud.crypto.sm.SM2TextEncryptor;
import cn.aotcloud.logger.LoggerHandle;

import cn.aotcloud.smcrypto.Sm2Utils;
import cn.aotcloud.smcrypto.exception.InvalidCryptoDataException;
import cn.aotcloud.smcrypto.exception.InvalidKeyException;
import cn.aotcloud.smcrypto.exception.InvalidSourceDataException;

/**
 * @author xkxu
 */
public class SM2NativeTextEncryptor extends SM2TextEncryptor {
	
	protected LoggerHandle logger = new LoggerHandle(getClass());
	
	public SM2NativeTextEncryptor(EncryptionProperties encryptionProperties) {
		this(encryptionProperties.getSm2Key().getPubKeyHex(), encryptionProperties.getSm2Key().getPrvKeyHex());
	}

	public SM2NativeTextEncryptor(String pubKeyHex, String prvKeyHex) {
		super(pubKeyHex, prvKeyHex);
	}

	@Override
	public String encrypt(String text) {
		try {
			return getNativeSm2Utils().encryptFromText(getPubKeyHex(), text);
		} catch (InvalidKeyException | InvalidSourceDataException e) {
			logger.error("SM2 加密失败。", e);
			throw new EncryptException(e, EncryptException.ENCRYPT_ERROR_CODE);
		}
	}

	@Override
	public String decrypt(String encryptedText) {
		try {
			return getNativeSm2Utils().decryptToText(getPrvKeyHex(), encryptedText);
		} catch (InvalidKeyException | InvalidCryptoDataException e) {
			logger.error("SM2 解密失败。", e);
			throw new EncryptException(e, EncryptException.DECRYPT_ERROR_CODE);
		} 
	}

	public Sm2Utils getNativeSm2Utils() {
		return createNativeSm2Util();
	}
	
	public Sm2Utils createNativeSm2Util() {
		Sm2Utils sm2Utils = new Sm2Utils();
		return sm2Utils;
	}

}
