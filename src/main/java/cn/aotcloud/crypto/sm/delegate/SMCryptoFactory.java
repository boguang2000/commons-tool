package cn.aotcloud.crypto.sm.delegate;

import cn.aotcloud.crypto.EncryptionProperties;
import cn.aotcloud.crypto.TextCryptoType;
import cn.aotcloud.crypto.pcode.PcodeEncoder;
import cn.aotcloud.crypto.sm.SM2TextEncryptor;
import cn.aotcloud.crypto.sm.SM2ToSM3TextEncryptor;
import cn.aotcloud.crypto.sm.SM3PcodeEncoder;
import cn.aotcloud.crypto.sm.SM3TextEncryptor;
import cn.aotcloud.crypto.sm.SM4TextEncryptor;
import cn.aotcloud.crypto.sm.SMImplMode;
import cn.aotcloud.crypto.sm.SMTextEncryptor;
import cn.aotcloud.crypto.sm.jni.SM2NativeTextEncryptor;
import cn.aotcloud.crypto.sm.jni.SM2ToSM3NativeTextEncryptor;
import cn.aotcloud.crypto.sm.jni.SM4NativeTextEncryptor;
import cn.aotcloud.crypto.sm.jni.SMNativeLicenseInit;
import org.springframework.security.crypto.encrypt.TextEncryptor;

/**
 * @author xkxu
 */
public class SMCryptoFactory {
	
	static {
		SMNativeLicenseInit.setSMNativeLicenseProperties();
	}
	
	public static TextEncryptor createTextEncryptor(EncryptionProperties encryptionProperties) {
		
		if (encryptionProperties.getText() == TextCryptoType.sm2) {
			return SMCryptoFactory.createSM2TextEncryptor(encryptionProperties, encryptionProperties.getSmImplMode());
		}
		
		if (encryptionProperties.getText() == TextCryptoType.sm4) {
			return SMCryptoFactory.createSM4TextEncryptor(encryptionProperties.getSm4Key(),
					encryptionProperties.getSm4CbcIv(), encryptionProperties.getSm4Type(), encryptionProperties.getSmImplMode());
		}
		
		if (encryptionProperties.getText() == TextCryptoType.sm2tosm3) {
			return SMCryptoFactory.createSM2ToSM3TextEncryptor(encryptionProperties, encryptionProperties.getSmImplMode());
		}
		
		if (encryptionProperties.getText() == TextCryptoType.sm2toplain) {
			return SMCryptoFactory.createSM2ToSM3TextEncryptor(encryptionProperties, false, encryptionProperties.getSmImplMode());
		}
		return null;
	}

	public static SMTextEncryptor createSM2TextEncryptor(String pubKeyHex, String prvKeyHex, SMImplMode implMode) {
		if (implMode == SMImplMode.c) {
			return new SM2NativeTextEncryptor(pubKeyHex, prvKeyHex);
		} else {
			return new SM2TextEncryptor(pubKeyHex, prvKeyHex);
		}
	}
	
	public static SMTextEncryptor createSM2TextEncryptor(EncryptionProperties encryptionProperties, SMImplMode implMode) {
		if (implMode == SMImplMode.c) {
			return new SM2NativeTextEncryptor(encryptionProperties);
		} else {
			return new SM2TextEncryptor(encryptionProperties);
		}
	}
	
	public static SMTextEncryptor createSM4TextEncryptor(EncryptionProperties encryptionProperties) {
		return createSM4TextEncryptor(encryptionProperties.getSm4Key(),
				encryptionProperties.getSm4CbcIv(),
				encryptionProperties.getSm4Type(),
				encryptionProperties.getSmImplMode());
	}
	
	public static SMTextEncryptor createSM4TextEncryptor(String key, String iv, String type, SMImplMode implMode) {
		if (implMode == SMImplMode.c) {
			return new SM4NativeTextEncryptor(key, true);
		} else {
			return new SM4TextEncryptor(key, true, iv, type);
		}
	}

	public static SMTextEncryptor createSM4TextEncryptor(String key, SMImplMode implMode) {
		if (implMode == SMImplMode.c) {
			return new SM4NativeTextEncryptor(key, true);
		} else {
			return new SM4TextEncryptor(key, true);
		}
	}

	public static SMTextEncryptor createSM3TextEncryptor(SMImplMode implMode) {
		if (implMode == SMImplMode.c) {
			return new SM3TextEncryptor();
		} else {
			return new SM3TextEncryptor();
		}
	}
	
	public static PcodeEncoder createSM3PcodeEncoder(SMImplMode implMode) {
		if (implMode == SMImplMode.c) {
			return new SM3PcodeEncoder();
		} else {
			return new SM3PcodeEncoder();
		}
	}
	
	public static SMTextEncryptor createSM2ToSM3TextEncryptor(
			EncryptionProperties encryptionProperties, SMImplMode implMode) {
		return createSM2ToSM3TextEncryptor(encryptionProperties, true, implMode);
	}
	
	public static SMTextEncryptor createSM2ToSM3TextEncryptor(
			EncryptionProperties encryptionProperties, boolean toSM3, SMImplMode implMode) {
		if (implMode == SMImplMode.c) {
			return new SM2ToSM3NativeTextEncryptor(encryptionProperties, toSM3);
		} else {
			return new SM2ToSM3TextEncryptor(encryptionProperties, toSM3);
		}
	}
	
	public static SMTextEncryptor createSM2ToSM3TextEncryptor(
			String pubKeyHex, String prvKeyHex, SMImplMode implMode) {
		return createSM2ToSM3TextEncryptor(pubKeyHex, prvKeyHex, true, implMode);
	}
	
	public static SMTextEncryptor createSM2ToSM3TextEncryptor(
			String pubKeyHex, String prvKeyHex, boolean toSM3, SMImplMode implMode) {
		if (implMode == SMImplMode.c) {
			return new SM2ToSM3NativeTextEncryptor(pubKeyHex, prvKeyHex, toSM3);
		} else {
			return new SM2ToSM3TextEncryptor(pubKeyHex, prvKeyHex, toSM3);
		}
	}
}
