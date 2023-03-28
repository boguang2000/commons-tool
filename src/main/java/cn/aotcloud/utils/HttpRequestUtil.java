package cn.aotcloud.utils;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.WebUtils;

import com.google.common.collect.Maps;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class HttpRequestUtil {

	public static final String LOCALHOST = "localhost";

	public static final String LOCAL_127 = "127.0.0.1";

	public static final String LOCAL_IPV6 = "0:0:0:0:0:0:0:1";

	public static String getIPAddress(HttpServletRequest request, boolean xff) {
		if(xff) {
	    	String ipAddresses = getIPAddressFull(request);
	        if (StringUtils.isBlank(ipAddresses) || StringUtils.equalsAnyIgnoreCase(ipAddresses, "unknown", "null")) {
	        	ipAddresses = request.getRemoteAddr();
	        }
	        return StringUtils.equalsIgnoreCase(ipAddresses, "0:0:0:0:0:0:0:1") ? "127.0.0.1" : ipAddresses;
		} else {
			return request.getRemoteAddr();
		}
	}
    
    private static String getIPAddressFull(HttpServletRequest request) {
        //X-Forwarded-For：Squid 服务代理
        String ipAddresses = HttpServletUtil.getHeader(request, "X-Forwarded-For");
        if (ipAddresses == null || ipAddresses.length() == 0 || "unknown".equalsIgnoreCase(ipAddresses)) {
            //Proxy-Client-IP：apache 服务代理
            ipAddresses = HttpServletUtil.getHeader(request, "Proxy-Client-IP");
        }
        if (ipAddresses == null || ipAddresses.length() == 0 || "unknown".equalsIgnoreCase(ipAddresses)) {
            //WL-Proxy-Client-IP：weblogic 服务代理
            ipAddresses = HttpServletUtil.getHeader(request, "WL-Proxy-Client-IP");
        }
        if (ipAddresses == null || ipAddresses.length() == 0 || "unknown".equalsIgnoreCase(ipAddresses)) {
            //HTTP_CLIENT_IP：有些代理服务器
            ipAddresses = HttpServletUtil.getHeader(request, "HTTP_CLIENT_IP");
        }
        if (ipAddresses == null || ipAddresses.length() == 0 || "unknown".equalsIgnoreCase(ipAddresses)) {
            //X-Real-IP：nginx服务代理
            ipAddresses = HttpServletUtil.getHeader(request, "X-Real-IP");
        }
        return ipAddresses;
    }
    
	/**
	 * @param location
	 * @return
	 */
	public static boolean isLocalhostAddr(String location) {
		return LOCALHOST.equals(location) || LOCAL_127.equals(location) || LOCAL_IPV6.equals(location);
	}

	public static HttpServletRequest getHttpServletRequestFromThreadLocal() {
		// 升级到Springboot2.x
		if (RequestContextHolder.getRequestAttributes() == null) {
			return null;
		} else {
			return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		}
	}

	public static String getHeaderValue(HttpServletRequest request, String key, String defaultValue) {
		String value = getHeaderValue(request, key);
		if (value == null) {
			value = defaultValue;
		}
		return value;
	}

	public static String getHeaderValue(HttpServletRequest httpRequest, String key) {
		String value = HttpServletUtil.getHeader(httpRequest, key);
		if (org.apache.commons.lang3.StringUtils.isNotBlank(value)) {
			value = value.replaceAll("(\r\n|\r|\n|\n\r)", "");
		}
		return value;
	}

	public static void addHttpHeaderValue(HttpServletResponse response, String name, String value) {
		response.addHeader(name, value);
	}

	public static void addHttpCookieValue(HttpServletResponse response, Cookie cookie) {
		response.addCookie(cookie);
	}

	public static String getParameterValue(HttpServletRequest request, String key) {
		return StringEscapeUtils.escapeHtml4(WebUtils.findParameterValue(request, key));
	}

	public static String getParameterValue(HttpServletRequest request, String key, String defaultValue) {
		String value = getParameterValue(request, key);
		if (value == null) {
			value = defaultValue;
		}
		return value;
	}

	public static String getRequestURL(HttpServletRequest request) {
		return request.getRequestURI() + (request.getQueryString() != null ? "?" + request.getQueryString() : "");
	}

	public static String getContentType(HttpServletRequest request) {
		return request.getContentType();
	}

	public static String getRemoteHost(HttpServletRequest request) {
		return request.getRemoteHost();
	}

	public static void setContentType(HttpServletResponse response, String contentType) {
		response.setContentType(contentType);
	}

	public static void sendRedirect(HttpServletResponse response, String location) throws IOException {
		response.sendRedirect(location);
	}

	public static void setSessionAttribute(HttpSession session, String name, Object value) {
		if (session != null) {
			session.setAttribute(name, value);
		}
	}

	public static void setRequestAttribute(HttpServletRequest request, String name, Object value) {
		request.setAttribute(name, value);
	}

	public static Object getRequestAttribute(HttpServletRequest request, String name) {
		return request.getAttribute(name);
	}

	public static String getQueryString(HttpServletRequest request) {
		return request.getQueryString();
	}

	public static Map<String, String> getParameterMap(HttpServletRequest request) {
		Map<String, String> map = new HashMap<String, String>();
		Enumeration<String> parameterNames =  HttpServletUtil.getParameterNames(request);
		while(parameterNames !=null && parameterNames.hasMoreElements()) {
			String parameterName = parameterNames.nextElement();
			String parameterValue= HttpServletUtil.getParameter(request, parameterName);
			map.put(parameterName, parameterValue);
		}
		return map;
	}
	
	public static Map<String, String> getHeaderMap(HttpServletRequest request) {
		Map<String, String> map = new HashMap<String, String>();
		Enumeration<String> headerNames =  HttpServletUtil.getHeaderNames(request);
		while(headerNames !=null && headerNames.hasMoreElements()) {
			String headerName = headerNames.nextElement();
			String headerValue= HttpServletUtil.getHeader(request, headerName);
			map.put(headerName, headerValue);
		}
		return map;
	}
	
	public static void removeHttpSessionCookie(HttpServletRequest request, HttpServletResponse response, ServerProperties serverProperties) {
		org.springframework.boot.web.servlet.server.Session.Cookie cookie = serverProperties.getServlet().getSession().getCookie();
		String name = StringUtils.isEmpty(cookie.getName()) ? "JSESSIONID" : cookie.getName();
		Cookie sessionCookie = new Cookie(name, "");

		if (cookie.getDomain() != null) {
			sessionCookie.setDomain(cookie.getDomain());
		}
		sessionCookie.setPath(cookie.getPath());

		sessionCookie.setMaxAge(0);
		if (cookie.getSecure() != null) {
			sessionCookie.setSecure(cookie.getSecure());
		}
		if (cookie.getHttpOnly() != null) {
			sessionCookie.setHttpOnly(cookie.getHttpOnly());
		}

		response.addCookie(sessionCookie);
	}
	
	/**
	 * 获取请求体字符
	 * 
	 * @param request
	 * @return
	 */
	public static String getBodyString(HttpServletRequest request) {
		StringBuilder sb = new StringBuilder();
		BufferedReader reader = null;
		try (InputStream inputStream = request.getInputStream()) {
			reader = new BufferedReader(new InputStreamReader(inputStream, Charset.forName("UTF-8")));
			String line = "";
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}
		} catch (IOException e) {
			throw new RuntimeException("获取请求体异常");
		} finally {
			IOUtils.closeQuietly(reader);
		}
		return sb.toString();
	}
	
	public static boolean flashBackCheck(String param) {
		if(StringUtils.containsAnyIgnoreCase(param, "..","%","&")) {
			return false;
		}
		return true;
	}
	
	public static boolean safeCheck(String param) {
		if(StringUtils.containsAnyIgnoreCase(param, " or "," insert "," select "," delete "," update ","script>","alert(","..","%","&")) {
			return false;
		}
		return true;
	}
	
	public static boolean safeCheck(HttpServletRequest request) {
		Map<String, String> map = Maps.newHashMap();
		map.putAll(getParameterMap(request));
		map.putAll(getHeaderMap(request));
		for(Entry<String, String>  entry: map.entrySet()) {
			if(StringUtils.containsAnyIgnoreCase(entry.getValue(), " or "," insert "," select "," delete "," update ","script>","alert(","..","%","&")) {
				return false;
			}
		}
		return true;
	}
}
