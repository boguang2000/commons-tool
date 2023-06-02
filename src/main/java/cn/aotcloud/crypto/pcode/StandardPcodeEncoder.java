package cn.aotcloud.crypto.pcode;

import org.springframework.security.crypto.password.StandardPasswordEncoder;

/**
 * @author xkxu
 */
@SuppressWarnings("deprecation")
public class StandardPcodeEncoder extends PcodeEncoderWrapper {

	public StandardPcodeEncoder() {
		super(new StandardPasswordEncoder());
	}
	
	public StandardPcodeEncoder(CharSequence secret) {
		super(new StandardPasswordEncoder(secret));
	}
}
