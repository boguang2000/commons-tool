package cn.aotcloud.crypto.pcode;

import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * @author xkxu
 */
public class PcodeEncoderWrapper implements PcodeEncoder {
	
	private final PasswordEncoder pcodeEncoder;
	
	public PcodeEncoderWrapper(PasswordEncoder pcodeEncoder) {
		this.pcodeEncoder = pcodeEncoder;
	}

	@Override
	public String encode(CharSequence rawCode) {
		return pcodeEncoder.encode(rawCode);
	}

	@Override
	public boolean matches(CharSequence rawCode, String encodedCode) {
		return pcodeEncoder.matches(rawCode, encodedCode);
	}

	public PasswordEncoder getPcodeEncoder() {
		return pcodeEncoder;
	}
}
