package cn.aotcloud.crypto.sm;

import cn.aotcloud.crypto.pcode.PcodeEncoder;

/**
 * 国密SM3不可逆加密
 *
 * @author xkxu
 */
public class SM3PcodeEncoder implements PcodeEncoder {

	private static final SM3PcodeEncoder pcodeEncoder = new SM3PcodeEncoder();

	public static SM3PcodeEncoder getInstance() {
		return pcodeEncoder;
	}
	
	private SMTextEncryptor textEncryptor = new SM3TextEncryptor();
	
	public SM3PcodeEncoder() {
	}

	@Override
	public String encode(CharSequence rawPcode) {
		if (rawPcode == null) {
			return null;
		}
		return textEncryptor.encrypt(rawPcode.toString());
	}

	@Override
	public boolean matches(CharSequence rawPcode, String encodedPcode) {
		// 都是进过sm3算法后的密文
		if (encodedPcode.equalsIgnoreCase((String) rawPcode)) {
			return true;
		}
		String inputPassword = encode(rawPcode);
		return inputPassword.equalsIgnoreCase(encodedPcode);
	}

}
