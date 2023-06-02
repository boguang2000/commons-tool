package cn.aotcloud.crypto.pcode;

import org.springframework.security.crypto.scrypt.SCryptPasswordEncoder;

/**
 * @author xkxu
 */
public class SCryptPcodeEncoder extends PcodeEncoderWrapper {

	public SCryptPcodeEncoder() {
		super(new SCryptPasswordEncoder());
	}
	
	public SCryptPcodeEncoder(int cpuCost, int memoryCost, int parallelization, int keyLength, int saltLength) {
		super(new SCryptPasswordEncoder(cpuCost, memoryCost, parallelization, keyLength, saltLength));
	}

}
