package cn.aotcloud.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.catalina.connector.ClientAbortException;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.util.MultiValueMap;

import cn.aotcloud.openapi.filter.BodyReaderHttpServletRequestWrapper;
import eu.bitwalker.useragentutils.Browser;

public class HttpServletUtil {

	private static final Logger logger = LoggerFactory.getLogger(HttpServletUtil.class);
	
	public static String getIP(HttpServletRequest request) {
		return request.getRemoteAddr();
	}
	
	public static String getCharacterEncoding(HttpServletRequest request) {
		return request.getCharacterEncoding();
	}
	
	public static String getContentType(HttpServletRequest request) {
		return request.getContentType();
	}
	
	public static Enumeration<String> getParameterNames(HttpServletRequest request) {
		return request.getParameterNames();
	}
	
	public static Enumeration<String> getHeaderNames(HttpServletRequest request) {
		return request.getHeaderNames();
	}
	
	public static Enumeration<String> getHeaders(HttpServletRequest request, String name) {
		return request.getHeaders(name);
	}
	
	public static String getHeader(HttpServletRequest request, String name) {
		return request.getHeader(name);
	}
	
	public static String getQueryString(HttpServletRequest request) {
		return request.getQueryString();
	}
	
	public static String getParameter(HttpServletRequest request, String name) {
		return request.getParameter(name);
	}
	
	public static String[] getParameterValues(HttpServletRequest request, String name) {
		return request.getParameterValues(name);
	}
	
	public static Object getAttribute(HttpServletRequest request, String name) {
		return request.getAttribute(name);
	}
	
	public static String getBodyString(BodyReaderHttpServletRequestWrapper bodyRequest) {
		return bodyRequest.getBodyString();
	}
	
	public static void setContentType(HttpServletResponse response, String type) {
		response.setContentType(type);
	}
	
	public static void setContentLength(HttpServletResponse response, int len) {
		response.setContentLength(len);
	}
	
	public static void setCharacterEncoding(HttpServletResponse response, String charset) {
		response.setCharacterEncoding(charset);
	}
	
	public static void addHeader(HttpServletResponse response, String name, String value) {
		response.addHeader(name, value);
	}
	
	public static void addHeader(HttpRequest request, String headerName, String headerValue) {
		request.getHeaders().add(headerName, headerValue);
	}
	
	public static void setHeader(HttpHeaders httpHeaders, String headerName, String headerValue) {
		if(httpHeaders != null) {
			httpHeaders.set(headerName, headerValue);
		}
	}
	
	public static void putAllHeader(HttpHeaders httpHeaders, HttpHeaders httpHeaders_) {
		if(httpHeaders != null) {
			httpHeaders.putAll(httpHeaders_);
		}
	}
	
	public static void setHeader(HttpServletResponse response, String name, String value) {
		response.setHeader(name, value);
	}
	
	public static void sendError(HttpServletResponse response, int sc) throws IOException {
		response.sendError(sc);
	}
	
	public static void setDateHeader(HttpServletResponse response, String name, long value) {
		response.setDateHeader(name, value);
	}
	
	public static void setStatus(HttpServletResponse response, int sc) {
		response.setStatus(sc);
	}
	
	public static Map<String, String[]> transferQueryParams(MultiValueMap<String, String> queryParams) {
    	Map<String, String[]> parameterMap = new HashMap<>();
    	if (queryParams != null) {
            queryParams.keySet().forEach(key -> parameterMap.put(key,
                    new String[]{String.valueOf(queryParams.get(key))
                            .replace("[", "")
                            .replace("]", "")}));
        }
    	
    	return parameterMap;
    }
	
	public static PrintWriter getPrintWriter(HttpServletResponse response) throws IOException {
		return response.getWriter();
	}
	
	public static void print(HttpServletResponse response, String content) {
		try {
			response.getWriter().print(content);
		} catch (IOException e) {
			logger.warn("response.getWriter()发生时IOException异常!");
		}
	}
	
	public static void responseError(HttpServletResponse response, Map<Integer, String> errorMap, Integer errorCode) {
		HttpServletUtil.setCharacterEncoding(response, "UTF-8");
		HttpServletUtil.setContentType(response, "text/html; charset=UTF-8");
		HttpServletUtil.setHeader(response, "Strict-Transport-Security", "max-age=31536000; includeSubDomains;preload");
		try {
			String error = errorMap.get(errorCode);
			PrintWriter printWriter = response.getWriter();
			printWriter.print(error == null ? "未知错误" : error);
		} catch (IOException e) {
			logger.error("PrintWriter发生IO异常");
		}
	}
	
	/**
	 * 支持断点续传的驱动下载
	 */
	@SuppressWarnings("deprecation")
	public static void downloadFileRanges(
			HttpServletRequest request, 
			HttpServletResponse response, 
			Long fileSize, 
			String fileName,
			String contentType,
			InputStream in) throws IOException {

		HttpServletUtil.setContentType(response, contentType);
		
		String userAgentString = HttpServletUtil.getHeader(request, "User-Agent");
		//判断是否safari浏览器
		if(StringUtils.isNotBlank(userAgentString) && Browser.SAFARI.isInUserAgentString(userAgentString)) {
			HttpServletUtil.addHeader(response, "Content-Disposition","attachment; filename*=UTF-8''" + URLEncoder.encode(fileName,"UTF-8"));
		}else {
			HttpServletUtil.addHeader(response, "Content-Disposition","attachment; filename=" + URLEncoder.encode(fileName, "UTF-8"));
		}
		HttpServletUtil.setHeader(response, "Accept-Ranges", "bytes");
		
		long pos = 0, last = fileSize - 1, sum = 0;// pos开始读取位置; last最后读取位置; sum记录总共已经读取了多少字节
		long rangLength = last - pos + 1;// 总共需要读取的字节
		String requestRange = HttpServletUtil.getHeader(request, "Range");
		String safeRequestRange = requestRange;
		HttpServletUtil.setStatus(response, HttpServletResponse.SC_PARTIAL_CONTENT);
		try {
			String numRang = safeRequestRange.replaceAll("bytes=", "");
			String[] strRange = numRang.split("-");
			if (strRange.length == 2) {
				pos = Long.parseLong(strRange[0].trim());
				last = Long.parseLong(strRange[1].trim());
			} else {
				pos = Long.parseLong(numRang.replaceAll("-", "").trim());
			}
		} catch (Exception e) {
			logger.warn("Range标头不是数字!");
			pos = 0;
		}
		
		String contentRange = new StringBuffer("bytes ").append(pos).append("-").append(last).append("/").append(fileSize).toString();
		HttpServletUtil.setHeader(response, "Content-Range", contentRange);
		HttpServletUtil.addHeader(response, "Content-Length", String.valueOf(rangLength));
		
		InputStream inputStream = null;
	    OutputStream bufferOut = null;
		try {
			// 跳过已经下载的部分，进行后续下载
	        bufferOut = new BufferedOutputStream(response.getOutputStream());
	        inputStream = new BufferedInputStream(in);
	        inputStream.skip(pos);
	        byte[] buffer = new byte[1024];
	        long length = 0;
	        while (sum < rangLength) {
	            length = inputStream.read(buffer, 0, ((rangLength - sum) <= buffer.length ? ((int) (rangLength - sum)) : buffer.length));
	            sum = sum + length;
	            if(length < Integer.MAX_VALUE) {
	            	bufferOut.write(buffer, 0, (int)length);
	            } else {
	            	throw new IOException("文件过大无法下载");
	            }
	        }
		} catch (Exception e) {
			if (e instanceof ClientAbortException) {
	            // 浏览器点击取消
	            logger.info("用户取消下载!");
	        } else {
	        	logger.error("下载文件失败...");
	            throw new IOException(e);
	        }
		} finally {
			IOUtils.closeQuietly(bufferOut);
			IOUtils.closeQuietly(inputStream);
			IOUtils.closeQuietly(in);
		}

	}
}
