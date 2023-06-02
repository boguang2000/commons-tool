package cn.aotcloud.crypto.sm.jni;

import cn.aotcloud.crypto.EncryptionProperties;
import cn.aotcloud.crypto.sm.SM2ToSM3TextEncryptor;
import cn.aotcloud.crypto.sm.SMTextEncryptor;

/**
 * @author xkxu
 */
public class SM2ToSM3NativeTextEncryptor extends SM2ToSM3TextEncryptor {
	
	private SMTextEncryptor sm2NativeTextEncryptor;
	private SM3NativeTextEncryptor sm3NativeTextEncryptor = new SM3NativeTextEncryptor();

	public SM2ToSM3NativeTextEncryptor(EncryptionProperties encryptionProperties, boolean toSM3) {
		super(encryptionProperties, toSM3);
		sm2NativeTextEncryptor = new SM2NativeTextEncryptor(encryptionProperties);
	}

	public SM2ToSM3NativeTextEncryptor(EncryptionProperties encryptionProperties) {
		super(encryptionProperties);
		sm2NativeTextEncryptor = new SM2NativeTextEncryptor(encryptionProperties);
	}

	public SM2ToSM3NativeTextEncryptor(String pubKeyHex, String prvKeyHex, boolean toSM3) {
		super(pubKeyHex, prvKeyHex, toSM3);
		sm2NativeTextEncryptor = new SM2NativeTextEncryptor(pubKeyHex, prvKeyHex);
	}

	public SM2ToSM3NativeTextEncryptor(String pubKeyHex, String prvKeyHex) {
		super(pubKeyHex, prvKeyHex);
		sm2NativeTextEncryptor = new SM2NativeTextEncryptor(pubKeyHex, prvKeyHex);
	}
	
	@Override
	public String encrypt(String text) {
		return sm2NativeTextEncryptor.encrypt(encryptInternal(text));
	}
	
	@Override
	protected String sm3Encrypt(String text) {
		return sm3NativeTextEncryptor.encrypt(text);
	}
	
	@Override
	public String decryptWithSM2(String encryptedText) {
		return sm2NativeTextEncryptor.decrypt(encryptedText);
	}
}
