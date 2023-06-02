package cn.aotcloud.crypto.pcode;

import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;

/**
 * @author xkxu
 */
public class Pbkdf2PcodeEncoder extends PcodeEncoderWrapper {

	public Pbkdf2PcodeEncoder() {
		super(new Pbkdf2PasswordEncoder());
	}
	
	public Pbkdf2PcodeEncoder(CharSequence secret) {
		super(new Pbkdf2PasswordEncoder(secret));
	}
	
	public Pbkdf2PcodeEncoder(CharSequence secret, int iterations, int hashWidth) {
		super(new Pbkdf2PasswordEncoder(secret, iterations, hashWidth));
	}
}
