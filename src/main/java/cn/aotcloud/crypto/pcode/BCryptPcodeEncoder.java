package cn.aotcloud.crypto.pcode;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.security.SecureRandom;

/**
 * @author xkxu
 */
public class BCryptPcodeEncoder extends PcodeEncoderWrapper {

	public BCryptPcodeEncoder() {
		super(new BCryptPasswordEncoder());
	}
	
	public BCryptPcodeEncoder(int strength) {
		super(new BCryptPasswordEncoder(strength));
	}
	
	public BCryptPcodeEncoder(int strength, SecureRandom random) {
		super(new BCryptPasswordEncoder(strength, random));
	}
}
