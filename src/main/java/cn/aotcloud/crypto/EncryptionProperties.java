package cn.aotcloud.crypto;

import cn.aotcloud.crypto.pcode.BCryptPcodeEncoder;
import cn.aotcloud.crypto.pcode.Pbkdf2PcodeEncoder;
import cn.aotcloud.crypto.pcode.PcodeEncoder;
import cn.aotcloud.crypto.pcode.SCryptPcodeEncoder;
import cn.aotcloud.crypto.pcode.StandardPcodeEncoder;
import cn.aotcloud.crypto.sm.SMImplMode;
import cn.aotcloud.crypto.sm.SmKeyLoader;

import cn.aotcloud.crypto.sm.delegate.SMCryptoFactory;
import cn.aotcloud.smcrypto.Sm2Utils;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.Resource;
import org.springframework.security.rsa.crypto.RsaAlgorithm;
import org.springframework.util.ClassUtils;

/**
 * @author xkxu
 */
@ConfigurationProperties(prefix="encrypt")
public class EncryptionProperties {
	
	/**
	 * SM2非对称加密默认公钥
	 */
	private static final String DEFAULT_SM2_PUBLIC_KEY = "040ECAE82EA6BDC5FF6C90E6D8700F4F9BD44224E8F7F7576FA9B17CE823C18DD5C1D4F9CBF4EE23B3D3347C589036EE13E9497607D0F6C7B5CC655D0D250EDCE8";
	
	/**
	 * SM2非对称加密默认私钥
	 */
	private static final String DEFAULT_SM2_PRIVATE_KEY = "7E94B2BE308639054B1714A758C27B6955CA702A70D906D603EA145AC0A2F079";
	
	/**
	 * SM4对称加密默认密钥，16进制格式
	 * JeF8U9wHFOMfs003
	 */
	private static final String DEFAULT_SM4_KEY = "4A65463855397748464F4D6673303033";
	
	private Boolean enabled = false;
	
	/**
	 * 安全模式，国密私钥和对称加密密钥分两部分保存，一部分保存到配置文件中，一部分保存到数据库中
	 */
	private boolean safeMode = false;
	
	private EncoderType encoderType = new EncoderType();

	/**
	 * A symmetric key. As a stronger alternative consider using a keystore.
	 */
	private String symmetricKey;

	/**
	 * Flag to say that a process should fail if there is an encryption or decryption
	 * error.
	 */
	private boolean failOnError = true;
	
	private TextCryptoType text = TextCryptoType.none;

	/**
	 * The key store properties for locating a key in a Java Key Store (a file in a format
	 * defined and understood by the JVM).
	 */
	private KeyStore keyStore = new KeyStore();
	
	/**
	 * Rsa algorithm properties when using asymmetric encryption.
	 */
	private Rsa rsa;

	{
		if (ClassUtils.isPresent("org.springframework.security.rsa.crypto.RsaAlgorithm", null)) {
			this.rsa = new Rsa();
		}
	}
	
	private String sm4Type = "ECB";

	private String sm4Key = DEFAULT_SM4_KEY;

	private String sm4KeyPrefix = "4A65463855397748464F4D66";

	/**
	 * CBC偏移量
	 */
	private String sm4CbcIv = "2092603DC7C046DCA20B61E086161C68";

	/**
	 * CBC偏移量前缀
	 */
	private String sm4CbcIvPrefix = "2092603DC7C046DCA20B61E0";

	private Sm2Key sm2Key;
	
	private boolean hasReBuildSmKey = false;
	
	private SMImplMode smImplMode = SMImplMode.java;
	
	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	public Rsa getRsa() {
		return this.rsa;
	}

	public boolean isFailOnError() {
		return this.failOnError;
	}

	public void setFailOnError(boolean failOnError) {
		this.failOnError = failOnError;
	}

	public String getKey() {
		return this.symmetricKey;
	}

	public void setKey(String key) {
		this.symmetricKey = key;
	}

	public KeyStore getKeyStore() {
		return this.keyStore;
	}

	public void setKeyStore(KeyStore keyStore) {
		this.keyStore = keyStore;
	}
	
	public EncoderType getEncoderType() {
		return encoderType;
	}

	public void setEncoderType(EncoderType encoderType) {
		this.encoderType = encoderType;
	}

	public TextCryptoType getText() {
		return text;
	}

	public void setText(TextCryptoType text) {
		this.text = text;
	}
	
	public String getSm4Key() {
		return sm4Key;
	}

	public void setSm4Key(String sm4Key) {
		this.sm4Key = sm4Key;
	}
	
	public String getSymmetricKey() {
		return symmetricKey;
	}

	public void setSymmetricKey(String symmetricKey) {
		this.symmetricKey = symmetricKey;
	}

	public String getSm4KeyPrefix() {
		return sm4KeyPrefix;
	}

	public void setSm4KeyPrefix(String sm4KeyPrefix) {
		this.sm4KeyPrefix = sm4KeyPrefix;
	}

	public String getSm4CbcIvPrefix() {
		return sm4CbcIvPrefix;
	}

	public void setSm4CbcIvPrefix(String sm4CbcIvPrefix) {
		this.sm4CbcIvPrefix = sm4CbcIvPrefix;
	}

	public String getSm4Type() {
		return sm4Type;
	}

	public void setSm4Type(String sm4Type) {
		this.sm4Type = sm4Type;
	}

	public String getSm4CbcIv() {
		return sm4CbcIv;
	}

	public void setSm4CbcIv(String sm4CbcIv) {
		this.sm4CbcIv = sm4CbcIv;
	}

	public Sm2Key getSm2Key() {
		if (sm2Key == null) {
			sm2Key = new Sm2Key();
			sm2Key.setPrvKeyHex(DEFAULT_SM2_PRIVATE_KEY);
			sm2Key.setPubKeyHex(DEFAULT_SM2_PUBLIC_KEY);
		}
		return sm2Key;
	}

	public void setSm2Key(Sm2Key sm2Key) {
		this.sm2Key = sm2Key;
	}
	
	public boolean isSafeMode() {
		return safeMode;
	}

	public void setSafeMode(boolean safeMode) {
		this.safeMode = safeMode;
	}
	
	public SMImplMode getSmImplMode() {
		return smImplMode;
	}

	public void setSmImplMode(SMImplMode smImplMode) {
		this.smImplMode = smImplMode;
	}

	@Deprecated
	public Sm2Utils createSm2Util() {
		Sm2Utils sm2Utils = new Sm2Utils();
		return sm2Utils;
	}
	
	public void rebuildSmKey(SmKeyLoader keyLoader) {
		if (!hasReBuildSmKey) {
			String privateKey = keyLoader.getSm2PrivateKey(getSm2Key().getPrvKeyHexPrefix());
			String key = keyLoader.getSm4Key(getSm4KeyPrefix());
			String sm4CbcIv = keyLoader.getSm4CbcIv(getSm4CbcIvPrefix());
			getSm2Key().setPrvKeyHex(privateKey);
			setSm4Key(key);
			setSm4CbcIv(sm4CbcIv);
			hasReBuildSmKey = true;
		}
	}
	
	public static class KeyStore {
		
		/**
		 * Location of the key store file, e.g. classpath:/keystore.jks.
		 */
		private Resource location;

		/**
		 * authkey that locks the keystore.
		 */
		//private String authkey;

		/**
		 * Alias for a key in the store.
		 */
		private String alias;

		/**
		 * Secret protecting the key (defaults to the same as the authkey).
		 */
		//private String secret;

		public String getAlias() {
			return this.alias;
		}

		public void setAlias(String alias) {
			this.alias = alias;
		}

		public Resource getLocation() {
			return this.location;
		}

		public void setLocation(Resource location) {
			this.location = location;
		}

		//public String getAuthkey() {
		//	return authkey;
		//}

		//public void setAuthkey(String authkey) {
		//	this.authkey = authkey;
		//}

		//public String getSecret() {
		//	return this.secret == null ? this.authkey : this.secret;
		//}

		//public void setSecret(String secret) {
		//	this.secret = secret;
		//}
	}

	public static class Rsa {

		/**
		 * The RSA algorithm to use (DEFAULT or OEAP). Once it is set do not change it (or
		 * existing ciphers will not a decryptable).
		 */
		private RsaAlgorithm algorithm = RsaAlgorithm.DEFAULT;

		/**
		 * Flag to indicate that "strong" AES encryption should be used internally. If
		 * true then the GCM algorithm is applied to the AES encrypted bytes. Default is
		 * false (in which case "standard" CBC is used instead). Once it is set do not
		 * change it (or existing ciphers will not a decryptable).
		 */
		private boolean strong = false;

		/**
		 * Salt for the random secret used to encrypt cipher text. Once it is set do not
		 * change it (or existing ciphers will not a decryptable).
		 */
		private String salt = "deadbeef";

		public RsaAlgorithm getAlgorithm() {
			return this.algorithm;
		}

		public void setAlgorithm(RsaAlgorithm algorithm) {
			this.algorithm = algorithm;
		}

		public boolean isStrong() {
			return this.strong;
		}

		public void setStrong(boolean strong) {
			this.strong = strong;
		}

		public String getSalt() {
			return this.salt;
		}

		public void setSalt(String salt) {
			this.salt = salt;
		}
	}
	
	public PcodeEncoder createPcodeEncoder() {
		if (getEncoderType().getType().equals("bcrypt")) {
			return new BCryptPcodeEncoder(getEncoderType().getBcrypt().getStrength());
			
		} else if (getEncoderType().getType().equals("scrypt")) {
			return new SCryptPcodeEncoder(
					getEncoderType().getScrypt().getCpuCost(), 
					getEncoderType().getScrypt().getMemoryCost(), 
					getEncoderType().getScrypt().getParallelization(), 
					getEncoderType().getScrypt().getKeyLength(), 
					getEncoderType().getScrypt().getSaltLength());
			
		} else if (getEncoderType().getType().equals("pbkdf2")) {
			return new Pbkdf2PcodeEncoder(
					getEncoderType().getPbkdf2().getSecret(), 
					getEncoderType().getPbkdf2().getIterations(), 
					getEncoderType().getPbkdf2().getHashWidth());
			
		} else if (getEncoderType().getType().equals("sm3")) {
			
			return SMCryptoFactory.createSM3PcodeEncoder(getSmImplMode());
		}
		return new StandardPcodeEncoder(getEncoderType().getStandard().getSecret());
	}
	
	public static class EncoderType {
		
		private String type = "sm3";
		
		private StandardAuthkey standard = new StandardAuthkey();
		
		private BCryptAuthkey bcrypt = new BCryptAuthkey();
		
		private SCryptAuthkey scrypt = new SCryptAuthkey();
		
		private Pbkdf2Authkey pbkdf2 = new Pbkdf2Authkey();
		
		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public StandardAuthkey getStandard() {
			return standard;
		}

		public void setStandard(StandardAuthkey standard) {
			this.standard = standard;
		}

		public BCryptAuthkey getBcrypt() {
			return bcrypt;
		}

		public void setBcrypt(BCryptAuthkey bcrypt) {
			this.bcrypt = bcrypt;
		}

		public Pbkdf2Authkey getPbkdf2() {
			return pbkdf2;
		}

		public void setPbkdf2(Pbkdf2Authkey pbkdf2) {
			this.pbkdf2 = pbkdf2;
		}

		public SCryptAuthkey getScrypt() {
			return scrypt;
		}

		public void setScrypt(SCryptAuthkey scrypt) {
			this.scrypt = scrypt;
		}

	}
	
	public static class StandardAuthkey {
		
		private String algorithm = "SHA-256";
		private String secret = "secret";
		
		public String getAlgorithm() {
			return algorithm;
		}
		public void setAlgorithm(String algorithm) {
			this.algorithm = algorithm;
		}
		
		public String getSecret() {
			return secret;
		}
		public void setSecret(String secret) {
			this.secret = secret;
		}
	}
	
	public static class BCryptAuthkey {
		
		private int strength;

		public int getStrength() {
			return strength;
		}

		public void setStrength(int strength) {
			this.strength = strength;
		}
	}
	
	public static class SCryptAuthkey {
		
		private int cpuCost = 16384;

		private int memoryCost = 8;

		private int parallelization = 1;

		private int keyLength = 32;
		
		private int saltLength = 64;

		public int getCpuCost() {
			return cpuCost;
		}

		public void setCpuCost(int cpuCost) {
			this.cpuCost = cpuCost;
		}

		public int getMemoryCost() {
			return memoryCost;
		}

		public void setMemoryCost(int memoryCost) {
			this.memoryCost = memoryCost;
		}

		public int getParallelization() {
			return parallelization;
		}

		public void setParallelization(int parallelization) {
			this.parallelization = parallelization;
		}

		public int getKeyLength() {
			return keyLength;
		}

		public void setKeyLength(int keyLength) {
			this.keyLength = keyLength;
		}

		public int getSaltLength() {
			return saltLength;
		}

		public void setSaltLength(int saltLength) {
			this.saltLength = saltLength;
		}
	}
	
	public static class Pbkdf2Authkey {
		
		private String secret;
		private int hashWidth;
		private int iterations;
		
		public String getSecret() {
			return secret;
		}
		public void setSecret(String secret) {
			this.secret = secret;
		}
		
		public int getHashWidth() {
			return hashWidth;
		}
		public void setHashWidth(int hashWidth) {
			this.hashWidth = hashWidth;
		}
		
		public int getIterations() {
			return iterations;
		}
		public void setIterations(int iterations) {
			this.iterations = iterations;
		}
	}
	
	public static class Sm2Key {
		
		private String pubKeyHex;
		private String prvKeyHex;
		
		private String prvKeyHexPrefix;
		
		public String getPubKeyHex() {
			return pubKeyHex;
		}
		public void setPubKeyHex(String pubKeyHex) {
			this.pubKeyHex = pubKeyHex;
		}
		
		public String getPrvKeyHex() {
			return prvKeyHex;
		}
		public void setPrvKeyHex(String prvKeyHex) {
			this.prvKeyHex = prvKeyHex;
		}
		public String getPrvKeyHexPrefix() {
			return prvKeyHexPrefix;
		}
		public void setPrvKeyHexPrefix(String prvKeyHexPrefix) {
			this.prvKeyHexPrefix = prvKeyHexPrefix;
		}
		
	}
}
