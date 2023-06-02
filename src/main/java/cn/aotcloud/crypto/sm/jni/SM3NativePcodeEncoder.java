package cn.aotcloud.crypto.sm.jni;

import cn.aotcloud.crypto.sm.SM3PcodeEncoder;
import cn.aotcloud.crypto.sm.SMTextEncryptor;

/**
 * @author xkxu
 */
public class SM3NativePcodeEncoder extends SM3PcodeEncoder {
	
	private SMTextEncryptor textEncryptor = new SM3NativeTextEncryptor();

	@Override
	public String encode(CharSequence rawPcode) {
		if (rawPcode == null) {
			return null;
		}
		return textEncryptor.encrypt(rawPcode.toString());
	}
}
