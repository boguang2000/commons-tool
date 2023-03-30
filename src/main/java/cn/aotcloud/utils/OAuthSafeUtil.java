package cn.aotcloud.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

public class OAuthSafeUtil {

	public static String toString(final InputStream is, final String defaultCharset) throws IOException {
        if (is == null) {
            throw new IllegalArgumentException("InputStream may not be null");
        }

        String charset = defaultCharset;
        if (charset == null) {
            charset = "UTF-8";
        }
        Reader reader = new InputStreamReader(is, charset);
        StringBuilder sb = new StringBuilder();
        int l;
        try {
            char[] tmp = new char[4096];
            while ((l = reader.read(tmp)) != -1) {
                sb.append(tmp, 0, l);
            }
        } finally {
            reader.close();
        }
        return sb.toString();
    }
	
	public static Set<String> decodeScopes(String s) {
        Set<String> scopes = new HashSet<String>();
        if (!StringUtils.isEmpty(s)) {
            StringTokenizer tokenizer = new StringTokenizer(s, " ");

            while (tokenizer.hasMoreElements()) {
                scopes.add(tokenizer.nextToken());
            }
        }
        return scopes;

    }

    public static String encodeScopes(Set<String> s) {
        StringBuffer scopes = new StringBuffer();
        for (String scope : s) {
            scopes.append(scope).append(" ");
        }
        return scopes.toString().trim();

    }
    
    public static boolean hasContentType(String requestContentType, String requiredContentType) {
        if (StringUtils.isEmpty(requiredContentType) || StringUtils.isEmpty(requestContentType)) {
            return false;
        }
        StringTokenizer tokenizer = new StringTokenizer(requestContentType, ";");
        while (tokenizer.hasMoreTokens()) {
            if (requiredContentType.equals(tokenizer.nextToken())) {
                return true;
            }
        }

        return false;
    }

    
    public static String getCookieValue(HttpServletRequest request, String cookieName) {
    	Cookie[] cookies = request.getCookies();
    	if (cookies != null && cookies.length > 0) {
    		for (Cookie cookie : cookies) {
    			if (cookieName.equals(cookie.getName())) {
    				return cookie.getValue();
    			}
    		}
    	}
		return null;
    }
}
