package cn.aotcloud.crypto.sm;

import cn.aotcloud.crypto.EncryptException;
import cn.aotcloud.crypto.EncryptionProperties;
import cn.aotcloud.crypto.rsa.RsaPublicCode;
import cn.aotcloud.crypto.rsa.RsaPublicCodeHolder;
import cn.aotcloud.logger.LoggerHandle;

import cn.aotcloud.smcrypto.Sm2Utils;
import cn.aotcloud.smcrypto.exception.InvalidCryptoDataException;
import cn.aotcloud.smcrypto.exception.InvalidKeyException;
import cn.aotcloud.smcrypto.exception.InvalidSourceDataException;

/**
 * SM2非对称加密解密
 * 
 * @author xkxu
 */
public class SM2TextEncryptor extends SMTextEncryptor implements RsaPublicCodeHolder {
	
	protected LoggerHandle logger = new LoggerHandle(getClass());
	
	private final String pubKeyHex;
	private final String prvKeyHex;
	
	/**
	 * 该构造方法已过期，请修改
	 * 
	 * @param sm2Utils	
	 * @param encryptionProperties
	 */
	@Deprecated
	public SM2TextEncryptor(Sm2Utils sm2Utils, EncryptionProperties encryptionProperties) {
		this(sm2Utils, encryptionProperties.getSm2Key().getPubKeyHex(), encryptionProperties.getSm2Key().getPrvKeyHex());
	}

	/**
	 * 该构造方法已过期，请修改
	 * 
	 * @param sm2Utils
	 * @param pubKeyHex
	 * @param prvKeyHex
	 */
	@Deprecated
	public SM2TextEncryptor(Sm2Utils sm2Utils, String pubKeyHex, String prvKeyHex) {
		this.pubKeyHex = pubKeyHex;
		this.prvKeyHex = prvKeyHex;
	}

	public SM2TextEncryptor(EncryptionProperties encryptionProperties) {
		this(encryptionProperties.getSm2Key().getPubKeyHex(), encryptionProperties.getSm2Key().getPrvKeyHex());
	}

	public SM2TextEncryptor(String pubKeyHex, String prvKeyHex) {
		this.pubKeyHex = pubKeyHex;
		this.prvKeyHex = prvKeyHex;
	}

	@Override
	public String encrypt(String text) {
		try {
			return getSm2Utils().encryptFromText(pubKeyHex, text);
		} catch (InvalidKeyException | InvalidSourceDataException e) {
			logger.error("SM2 加密失败。", e);
			throw new EncryptException(e, EncryptException.ENCRYPT_ERROR_CODE);
		}
	}

	@Override
	public String decrypt(String encryptedText) {
		try {
			return getSm2Utils().decryptToText(prvKeyHex, encryptedText);
		} catch (InvalidKeyException | InvalidCryptoDataException e) {
			logger.error("SM2 解密失败。", e);
			throw new EncryptException(e, EncryptException.DECRYPT_ERROR_CODE);
		} 
	}
	
	@Override
	public RsaPublicCode getRsaPublicCode() {
		RsaPublicCode rsaPublicKey = new RsaPublicCode();
		rsaPublicKey.setExponent(pubKeyHex);
		return rsaPublicKey;
	}

	public Sm2Utils getSm2Utils() {
		return createSm2Util();
	}
	
	public Sm2Utils createSm2Util() {
		Sm2Utils sm2Utils = new Sm2Utils();
		return sm2Utils;
	}

	public String getPubKeyHex() {
		return pubKeyHex;
	}

	public String getPrvKeyHex() {
		return prvKeyHex;
	}
}
