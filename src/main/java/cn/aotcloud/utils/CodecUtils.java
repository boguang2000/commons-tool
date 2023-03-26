package cn.aotcloud.utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;

public class CodecUtils {

	protected static final Logger logger = LoggerFactory.getLogger(CodecUtils.class);
	
	public static String getMd5HexString(final InputStream data)  {
		try {
			return DigestUtils.md5Hex(data);
		} catch (IOException e) {
			logger.error("获取Md5编码时IOException异常");
		}
		return null;
	}
	
	public static String getSha1HexString(final InputStream data)  {
		try {
			return DigestUtils.sha1Hex(data);
		} catch (IOException e) {
			logger.error("获取Sha1编码时IOException异常");
		}
		return null;
	}
	
	public static ByteArrayInputStream stringToInputStream(String data) {
		if(StringUtils.isNotBlank(data)) {
			return new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8));
		} else {
			return null;
		}
	}
	
	public static HttpInputMessage createHttpInputMessage(HttpHeaders httpHeaders, String data) {
		byte[] bytes = data.getBytes(StandardCharsets.UTF_8);
		InputStream inputStream = new ByteArrayInputStream(bytes);
		return new HttpInputMessage() {
            @Override
            public HttpHeaders getHeaders() {
            	httpHeaders.setContentLength(bytes.length);
                return httpHeaders;
            }
            @Override
            public InputStream getBody() {
                return inputStream;
            }
        };
	}
}
