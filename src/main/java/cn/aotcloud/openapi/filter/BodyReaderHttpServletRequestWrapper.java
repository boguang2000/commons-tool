package cn.aotcloud.openapi.filter;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Arrays;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BodyReaderHttpServletRequestWrapper extends HttpServletRequestWrapper {
	
	protected Logger logger = LoggerFactory.getLogger(getClass());
	
	private byte[] buffer = null;

	public BodyReaderHttpServletRequestWrapper(HttpServletRequest request) throws IOException {
		super(request);
		copyInputStream();
	}
	
	public void setBuffer(byte[] buffer) {
		this.buffer = buffer != null ? Arrays.copyOf(buffer, buffer.length) : null;
	}
	
	@Override
    public ServletInputStream getInputStream() throws IOException {
		if(this.buffer != null) {
			return new BodyReaderBufferInputStream(this.buffer);
		} else {
			return null;
		}
    }
	
	@Override
	public BufferedReader getReader() throws IOException {
		return new BufferedReader(new InputStreamReader(this.getInputStream(), this.readCharacterEncoding(this)));
	}
	
	public String getBodyString() {
		String bodyString = null;
		try {
			bodyString = new String(this.buffer, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			logger.error("buffer转字符串发生UnsupportedEncodingException");
		}
		return bodyString;
	}
	
	/**
	 * 备份流
	 * @throws IOException
	 */
	public void copyInputStream() throws IOException {
		InputStream is = null;
		try {
			is = super.getInputStream();
			if(is != null) {
				this.buffer = IOUtils.toByteArray(is);
			}
		} finally {
			if(is != null) {
				try {
					is.close();
				} catch (IOException e) {
					logger.error("InputStream关闭异常");
				}
			}
		}
	}
	
	public Charset readCharacterEncoding(HttpServletRequest request) {
		Charset charset = null;
		String characterEncoding = request.getCharacterEncoding();
		if (StringUtils.isNotBlank(characterEncoding) && !StringUtils.equalsIgnoreCase(characterEncoding, "null")) {
			charset = Charset.forName(characterEncoding);
		} else {
			charset = Charset.defaultCharset();
		}

		return charset;
	}
	
	private class BodyReaderBufferInputStream extends ServletInputStream {

		private ByteArrayInputStream inputStream;

		public BodyReaderBufferInputStream(byte[] buffer) {
			this.inputStream = (buffer == null ? null : new ByteArrayInputStream(buffer));
		}

		@Override
		public int available() throws IOException {
			return inputStream.available();
		}

		@Override
		public int read() throws IOException {
			return inputStream.read();
		}

		@Override
		public int read(byte[] b, int off, int len) throws IOException {
			return inputStream.read(b, off, len);
		}

		@Override
		public boolean isFinished() {
			return false;
		}

		@Override
		public boolean isReady() {
			return false;
		}

		@Override
		public void setReadListener(ReadListener listener) {
			
		}
	}
}
