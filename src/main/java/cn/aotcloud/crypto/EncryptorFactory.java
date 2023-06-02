package cn.aotcloud.crypto;

import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.security.rsa.crypto.RsaSecretEncryptor;

/**
 * @author xkxu
 */
public class EncryptorFactory {

	private String salt = "deadbeef";
	
	public EncryptorFactory() {
	}

	public EncryptorFactory(String salt) {
		this.salt = salt;
	}

	public TextEncryptor create(String data) {

		TextEncryptor encryptor;
		if (data.contains("RSA PRIVATE KEY")) {

			try {
				encryptor = new RsaSecretEncryptor(data);
			}
			catch (IllegalArgumentException e) {
				throw new KeyFormatException();
			}
		}
		else if (data.startsWith("ssh-rsa") || data.contains("RSA PUBLIC KEY")) {
			throw new KeyFormatException();
		}
		else {
			encryptor = Encryptors.text(data, salt);
		}
		return encryptor;
	}
}
