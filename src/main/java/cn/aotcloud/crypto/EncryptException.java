package cn.aotcloud.crypto;

import cn.aotcloud.exception.BaseException;
import cn.aotcloud.exception.ErrorCode;

/**
 * 加密解密异常
 * 
 * @author xkxu
 */
public class EncryptException extends BaseException {

	private static final long serialVersionUID = -2238828901705315111L;
	
	public static final ErrorCode ENCRYPT_ERROR_CODE = new ErrorCode("appcenter-crypto-001", "加密失败。");
	
	public static final ErrorCode DECRYPT_ERROR_CODE = new ErrorCode("appcenter-crypto-002", "数据完整性被破坏。");

	public EncryptException() {
		super();
	}

	public EncryptException(ErrorCode errorCode) {
		super(errorCode);
	}

	public EncryptException(String message, String code) {
		super(message, code);
	}

	public EncryptException(String message, Throwable cause, String code) {
		super(message, cause, code);
	}

	public EncryptException(String message, Throwable cause) {
		super(message, cause);
	}

	public EncryptException(String code) {
		super(code);
	}

	public EncryptException(Throwable cause, ErrorCode errorCode) {
		super(cause, errorCode);
	}

	public EncryptException(Throwable cause, String code) {
		super(cause, code);
	}

	public EncryptException(Throwable cause) {
		super(cause);
	}
	
}
