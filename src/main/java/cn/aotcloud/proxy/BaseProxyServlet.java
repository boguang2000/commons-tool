package cn.aotcloud.proxy;

import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.config.SocketConfig;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicHttpEntityEnclosingRequest;
import org.apache.http.message.BasicHttpRequest;
import org.apache.http.message.HeaderGroup;
import org.apache.http.util.EntityUtils;

import ch.qos.logback.classic.Logger;
import cn.aotcloud.utils.HttpServletUtil;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpCookie;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.BitSet;
import java.util.Enumeration;
import java.util.Formatter;

public class BaseProxyServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	public static final String LOG_ENABLE = "log";

	public static final String P_FORWARDEDFOR = "forwardip";

	public static final String P_PRESERVEHOST = "preserveHost";

	public static final String P_PRESERVECOOKIES = "preserveCookies";

	public static final String P_HANDLEREDIRECTS = "http.protocol.handle-redirects";

	public static final String P_CONNECTTIMEOUT = "http.socket.timeout";

	public static final String P_READTIMEOUT = "http.read.timeout";

	public static final String P_CONNECTIONREQUESTTIMEOUT = "http.connectionrequest.timeout";

	public static final String P_MAXCONNECTIONS = "http.maxConnections";

	public static final String P_USESYSTEMPROPERTIES = "useSystemProperties";

	public static final String P_HANDLECOMPRESSION = "handleCompression";

	public static final String P_TARGET_URI = "targetUri";

	protected static final String ATTR_TARGET_URI = BaseProxyServlet.class.getSimpleName() + ".targetUri";
	
	protected static final String ATTR_TARGET_HOST = BaseProxyServlet.class.getSimpleName() + ".targetHost";

	protected boolean doLog = false;
	protected boolean doForwardIP = true;
	protected boolean doSendUrlFragment = true;
	protected boolean doPreserveHost = false;
	protected boolean doPreserveCookies = false;
	protected boolean doHandleRedirects = false;
	protected boolean useSystemProperties = true;
	protected boolean doHandleCompression = false;
	protected int connectTimeout = -1;
	protected int readTimeout = -1;
	protected int connectionRequestTimeout = -1;
	protected int maxConnections = -1;

	protected String targetUri;
	protected URI targetUriObj;
	protected HttpHost targetHost;

	private HttpClient proxyClient;

	protected static final HeaderGroup hopByHopHeaders;
	
	static {
		hopByHopHeaders = new HeaderGroup();
		String[] headers = new String[] { 
				"Connection", "Keep-Alive", "Proxy-Authenticate", "Proxy-Authorization", "TE",
				"Trailers", "Transfer-Encoding", "Upgrade" };
		for (String header : headers) {
			hopByHopHeaders.addHeader(new BasicHeader(header, null));
		}
	}
	
	protected String getTargetUri(HttpServletRequest servletRequest) {
		return (String) HttpServletUtil.getAttribute(servletRequest, ATTR_TARGET_URI);
	}

	protected HttpHost getTargetHost(HttpServletRequest servletRequest) {
		return (HttpHost) HttpServletUtil.getAttribute(servletRequest, ATTR_TARGET_HOST);
	}

	protected String getConfigParam(String key) {
		return getServletConfig().getInitParameter(key);
	}

	@Override
	public void init() throws ServletException {
		String doLogStr = getConfigParam(LOG_ENABLE);
		if (doLogStr != null) {
			this.doLog = Boolean.parseBoolean(doLogStr);
		}

		String doForwardIPString = getConfigParam(P_FORWARDEDFOR);
		if (doForwardIPString != null) {
			this.doForwardIP = Boolean.parseBoolean(doForwardIPString);
		}

		String preserveHostString = getConfigParam(P_PRESERVEHOST);
		if (preserveHostString != null) {
			this.doPreserveHost = Boolean.parseBoolean(preserveHostString);
		}

		String preserveCookiesString = getConfigParam(P_PRESERVECOOKIES);
		if (preserveCookiesString != null) {
			this.doPreserveCookies = Boolean.parseBoolean(preserveCookiesString);
		}

		String handleRedirectsString = getConfigParam(P_HANDLEREDIRECTS);
		if (handleRedirectsString != null) {
			this.doHandleRedirects = Boolean.parseBoolean(handleRedirectsString);
		}

		String connectTimeoutString = getConfigParam(P_CONNECTTIMEOUT);
		if (connectTimeoutString != null) {
			this.connectTimeout = Integer.parseInt(connectTimeoutString);
		}

		String readTimeoutString = getConfigParam(P_READTIMEOUT);
		if (readTimeoutString != null) {
			this.readTimeout = Integer.parseInt(readTimeoutString);
		}

		String connectionRequestTimeout = getConfigParam(P_CONNECTIONREQUESTTIMEOUT);
		if (connectionRequestTimeout != null) {
			this.connectionRequestTimeout = Integer.parseInt(connectionRequestTimeout);
		}

		String maxConnections = getConfigParam(P_MAXCONNECTIONS);
		if (maxConnections != null) {
			this.maxConnections = Integer.parseInt(maxConnections);
		}

		String useSystemPropertiesString = getConfigParam(P_USESYSTEMPROPERTIES);
		if (useSystemPropertiesString != null) {
			this.useSystemProperties = Boolean.parseBoolean(useSystemPropertiesString);
		}

		String doHandleCompression = getConfigParam(P_HANDLECOMPRESSION);
		if (doHandleCompression != null) {
			this.doHandleCompression = Boolean.parseBoolean(doHandleCompression);
		}
		proxyClient = createHttpClient();
	}

	protected RequestConfig buildRequestConfig() {
		return RequestConfig.custom().setRedirectsEnabled(doHandleRedirects).setCookieSpec(CookieSpecs.IGNORE_COOKIES)
				.setConnectTimeout(connectTimeout).setSocketTimeout(readTimeout)
				.setConnectionRequestTimeout(connectionRequestTimeout).build();
	}

	protected SocketConfig buildSocketConfig() {
		if (readTimeout < 1) {
			return null;
		}
		return SocketConfig.custom().setSoTimeout(readTimeout).build();
	}

	protected void initTarget() throws ServletException {
		targetUri = getConfigParam(P_TARGET_URI);
		if (targetUri == null) {
			throw new ServletException(P_TARGET_URI + " is required.");
		}
		try {
			targetUriObj = new URI(targetUri);
		} catch (URISyntaxException e) {
			throw new ServletException("Trying to process targetUri init parameter: " + e, e);
		}
		targetHost = URIUtils.extractHost(targetUriObj);
	}

	protected HttpClient createHttpClient() {
		HttpClientBuilder clientBuilder = getHttpClientBuilder().setDefaultRequestConfig(buildRequestConfig())
				.setDefaultSocketConfig(buildSocketConfig());

		clientBuilder.setMaxConnTotal(maxConnections);
		clientBuilder.setMaxConnPerRoute(maxConnections);
		if (!doHandleCompression) {
			clientBuilder.disableContentCompression();
		}

		if (useSystemProperties) {
			clientBuilder = clientBuilder.useSystemProperties();
		}
		return buildHttpClient(clientBuilder);
	}

	protected HttpClient buildHttpClient(HttpClientBuilder clientBuilder) {
		return clientBuilder.build();
	}

	protected HttpClientBuilder getHttpClientBuilder() {
		return HttpClientBuilder.create();
	}

	protected HttpClient getProxyClient() {
		return proxyClient;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void destroy() {
		if (proxyClient instanceof Closeable) {
			try {
				((Closeable) proxyClient).close();
			} catch (IOException e) {
				log("While destroying servlet, shutting down HttpClient: " + e, e);
			}
		} else {
			if (proxyClient != null)
				proxyClient.getConnectionManager().shutdown();
		}
		super.destroy();
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void service(HttpServletRequest servletRequest, HttpServletResponse servletResponse)
			throws ServletException, IOException {
		setTarget(servletRequest);
		if (servletRequest.getAttribute(ATTR_TARGET_URI) == null) {
			servletRequest.setAttribute(ATTR_TARGET_URI, targetUri);
		}
		if (servletRequest.getAttribute(ATTR_TARGET_HOST) == null) {
			servletRequest.setAttribute(ATTR_TARGET_HOST, targetHost);
		}

		String method = servletRequest.getMethod();
		String proxyRequestUri = rewriteUrlFromRequest(servletRequest);
		HttpRequest proxyRequest;
		if (HttpServletUtil.getHeader(servletRequest, HttpHeaders.CONTENT_LENGTH) != null
				|| HttpServletUtil.getHeader(servletRequest, HttpHeaders.TRANSFER_ENCODING) != null) {
			proxyRequest = newProxyRequestWithEntity(method, proxyRequestUri, servletRequest);
		} else {
			proxyRequest = new BasicHttpRequest(method, proxyRequestUri);
		}

		copyRequestHeaders(servletRequest, proxyRequest);

		setXForwardedForHeader(servletRequest, proxyRequest);

		HttpResponse proxyResponse = null;
		try {
			proxyResponse = doExecute(servletRequest, servletResponse, proxyRequest);
			int statusCode = proxyResponse.getStatusLine().getStatusCode();
			servletResponse.setStatus(statusCode, proxyResponse.getStatusLine().getReasonPhrase());
			copyResponseHeaders(proxyResponse, servletRequest, servletResponse);

			if (statusCode == HttpServletResponse.SC_NOT_MODIFIED) {
				servletResponse.setIntHeader(HttpHeaders.CONTENT_LENGTH, 0);
			} else {
				copyResponseEntity(proxyResponse, servletResponse, proxyRequest, servletRequest);
			}

		} catch (Exception e) {
			handleRequestException(proxyRequest, proxyResponse, e);
		} finally {
			if (proxyResponse != null) {
				EntityUtils.consumeQuietly(proxyResponse.getEntity());
			}
		}
	}
	
	protected String createTarget(HttpServletRequest servletRequest) {
		String targetUri = HttpServletUtil.getParameter(servletRequest, "target");
		return targetUri;
	}
	
	protected void setTarget(HttpServletRequest servletRequest) throws ServletException {
		targetUri = createTarget(servletRequest);
		try {
			targetUriObj = new URI(targetUri);
		} catch (URISyntaxException e) {
			throw new ServletException("Trying to process targetUri init parameter: " + e, e);
		}
		targetHost = URIUtils.extractHost(targetUriObj);
	}
	
	@SuppressWarnings("deprecation")
	protected void handleRequestException(HttpRequest proxyRequest, HttpResponse proxyResonse, Exception e)
			throws ServletException, IOException {
		if (proxyRequest instanceof org.apache.http.client.methods.AbortableHttpRequest) {
			org.apache.http.client.methods.AbortableHttpRequest abortableHttpRequest = (org.apache.http.client.methods.AbortableHttpRequest) proxyRequest;
			abortableHttpRequest.abort();
		}
		if (proxyResonse instanceof Closeable) {
			((Closeable) proxyResonse).close();
		}
		if (e instanceof RuntimeException) {
			throw (RuntimeException) e;
		}
		if (e instanceof ServletException) {
			throw (ServletException) e;
		}
		if (e instanceof IOException) {
			throw (IOException) e;
		}
		throw new RuntimeException(e);
	}

	protected HttpResponse doExecute(HttpServletRequest servletRequest, HttpServletResponse servletResponse,
			HttpRequest proxyRequest) throws IOException {
		if (doLog) {
			log("proxy " + servletRequest.getMethod() + " uri: " + servletRequest.getRequestURI() + " -- "+ proxyRequest.getRequestLine().getUri());
		}
		return proxyClient.execute(getTargetHost(servletRequest), proxyRequest);
	}

	protected HttpRequest newProxyRequestWithEntity(String method, String proxyRequestUri,
			HttpServletRequest servletRequest) throws IOException {
		HttpEntityEnclosingRequest eProxyRequest = new BasicHttpEntityEnclosingRequest(method, proxyRequestUri);
		eProxyRequest.setEntity(new InputStreamEntity(servletRequest.getInputStream(), getContentLength(servletRequest)));
		return eProxyRequest;
	}

	private long getContentLength(HttpServletRequest request) {
		String contentLengthHeader = HttpServletUtil.getHeader(request, "Content-Length");
		if (contentLengthHeader != null) {
			return Long.parseLong(contentLengthHeader);
		}
		return -1L;
	}

	protected void closeQuietly(Closeable closeable) {
		try {
			closeable.close();
		} catch (IOException e) {
			log("关闭流异常");
		}
	}

	protected void copyRequestHeaders(HttpServletRequest servletRequest, HttpRequest proxyRequest) {
		Enumeration<String> enumerationOfHeaderNames = HttpServletUtil.getHeaderNames(servletRequest);
		while (enumerationOfHeaderNames.hasMoreElements()) {
			String headerName = enumerationOfHeaderNames.nextElement();
			copyRequestHeader(servletRequest, proxyRequest, headerName);
		}
	}

	protected void copyRequestHeader(HttpServletRequest servletRequest, HttpRequest proxyRequest, String headerName) {
		if (headerName.equalsIgnoreCase(HttpHeaders.CONTENT_LENGTH)) {
			return;
		}
		if (hopByHopHeaders.containsHeader(headerName)) {
			return;
		}
		if (doHandleCompression && headerName.equalsIgnoreCase(HttpHeaders.ACCEPT_ENCODING)) {
			return;
		}
		Enumeration<String> headers = HttpServletUtil.getHeaders(servletRequest, headerName);
		while (headers.hasMoreElements()) {
			String headerValue = headers.nextElement();
			if (!doPreserveHost && headerName.equalsIgnoreCase(HttpHeaders.HOST)) {
				HttpHost host = getTargetHost(servletRequest);
				headerValue = host.getHostName();
				if (host.getPort() != -1)
					headerValue += ":" + host.getPort();
			} else if (!doPreserveCookies && headerName.equalsIgnoreCase(org.apache.http.cookie.SM.COOKIE)) {
				headerValue = getRealCookie(headerValue);
			}
			proxyRequest.addHeader(headerName, headerValue);
		}
	}

	protected void setXForwardedForHeader(HttpServletRequest servletRequest, HttpRequest proxyRequest) {
		if (doForwardIP) {
			String forHeaderName = "X-Forwarded-For";
			String forHeader = servletRequest.getRemoteAddr();
			String existingForHeader = HttpServletUtil.getHeader(servletRequest, forHeaderName);
			if (existingForHeader != null) {
				forHeader = existingForHeader + ", " + forHeader;
			}
			proxyRequest.setHeader(forHeaderName, forHeader);

			String protoHeaderName = "X-Forwarded-Proto";
			String protoHeader = servletRequest.getScheme();
			proxyRequest.setHeader(protoHeaderName, protoHeader);
		}
	}

	protected void copyResponseHeaders(HttpResponse proxyResponse, HttpServletRequest servletRequest,
			HttpServletResponse servletResponse) {
		for (Header header : proxyResponse.getAllHeaders()) {
			copyResponseHeader(servletRequest, servletResponse, header);
		}
	}

	protected void copyResponseHeader(HttpServletRequest servletRequest, HttpServletResponse servletResponse,
			Header header) {
		String headerName = header.getName();
		if (hopByHopHeaders.containsHeader(headerName))
			return;
		String headerValue = header.getValue();
		if (headerName.equalsIgnoreCase(org.apache.http.cookie.SM.SET_COOKIE)
				|| headerName.equalsIgnoreCase(org.apache.http.cookie.SM.SET_COOKIE2)) {
			copyProxyCookie(servletRequest, servletResponse, headerValue);
		} else if (headerName.equalsIgnoreCase(HttpHeaders.LOCATION)) {
			servletResponse.addHeader(headerName, rewriteUrlFromResponse(servletRequest, headerValue));
		} else {
			servletResponse.addHeader(headerName, headerValue);
		}
	}

	protected void copyProxyCookie(HttpServletRequest servletRequest, HttpServletResponse servletResponse,
			String headerValue) {
		for (HttpCookie cookie : HttpCookie.parse(headerValue)) {
			Cookie servletCookie = createProxyCookie(servletRequest, cookie);
			servletResponse.addCookie(servletCookie);
		}
	}

	protected Cookie createProxyCookie(HttpServletRequest servletRequest, HttpCookie cookie) {
		String proxyCookieName = getProxyCookieName(cookie);
		Cookie servletCookie = new Cookie(proxyCookieName, cookie.getValue());
		servletCookie.setPath(buildProxyCookiePath(servletRequest));
		servletCookie.setComment(cookie.getComment());
		servletCookie.setMaxAge((int) cookie.getMaxAge());
		servletCookie.setSecure(cookie.getSecure());
		servletCookie.setVersion(cookie.getVersion());
		servletCookie.setHttpOnly(cookie.isHttpOnly());
		return servletCookie;
	}

	protected String getProxyCookieName(HttpCookie cookie) {
		return doPreserveCookies ? cookie.getName() : getCookieNamePrefix(cookie.getName()) + cookie.getName();
	}

	protected String buildProxyCookiePath(HttpServletRequest servletRequest) {
		String path = servletRequest.getContextPath();
		path += servletRequest.getServletPath();
		if (path.isEmpty()) {
			path = "/";
		}
		return path;
	}

	protected String getRealCookie(String cookieValue) {
		StringBuilder escapedCookie = new StringBuilder();
		String cookies[] = cookieValue.split("[;,]");
		for (String cookie : cookies) {
			String cookieSplit[] = cookie.split("=");
			if (cookieSplit.length == 2) {
				String cookieName = cookieSplit[0].trim();
				if (cookieName.startsWith(getCookieNamePrefix(cookieName))) {
					cookieName = cookieName.substring(getCookieNamePrefix(cookieName).length());
					if (escapedCookie.length() > 0) {
						escapedCookie.append("; ");
					}
					escapedCookie.append(cookieName).append("=").append(cookieSplit[1].trim());
				}
			}
		}
		return escapedCookie.toString();
	}

	protected String getCookieNamePrefix(String name) {
		return "";
	}

	protected void copyResponseEntity(HttpResponse proxyResponse, HttpServletResponse servletResponse,
			HttpRequest proxyRequest, HttpServletRequest servletRequest) throws IOException {
		HttpEntity entity = proxyResponse.getEntity();
		if (entity != null) {
			if (entity.isChunked()) {
				InputStream inputStream = cryptoeEtityContent(servletResponse, servletRequest, entity.getContent(), entity.getContentLength());
				OutputStream os = servletResponse.getOutputStream();
				byte[] buffer = new byte[10 * 1024];
				int read;
				while ((read = inputStream.read(buffer)) != -1) {
					os.write(buffer, 0, read);
					if (doHandleCompression || inputStream.available() == 0 /* next is.read will block */) {
						os.flush();
					}
				}
			} else {
				OutputStream servletOutputStream = servletResponse.getOutputStream();
				InputStream inputStream = cryptoeEtityContent(servletResponse, servletRequest, entity.getContent(), entity.getContentLength());
		        IOUtils.copy(inputStream, servletOutputStream);
				//OutputStream servletOutputStream = servletResponse.getOutputStream();
				//entity.writeTo(servletOutputStream);
			}
		}
	}
	
	protected InputStream cryptoeEtityContent(HttpServletResponse servletResponse, HttpServletRequest servletRequest, InputStream inputStream, long contentLength) {
		return null;
	}
	
	protected String rewriteUrlFromRequest(HttpServletRequest servletRequest) {
		StringBuilder uri = new StringBuilder(500);
		uri.append(getTargetUri(servletRequest));
		String pathInfo = rewritePathInfoFromRequest(servletRequest);
		if (pathInfo != null) {
			uri.append(encodeUriQuery(pathInfo, true));
		}
		String queryString = servletRequest.getQueryString();
		String fragment = null;
		if (queryString != null) {
			int fragIdx = queryString.indexOf('#');
			if (fragIdx >= 0) {
				fragment = queryString.substring(fragIdx + 1);
				queryString = queryString.substring(0, fragIdx);
			}
		}

		queryString = rewriteQueryStringFromRequest(servletRequest, queryString);
		if (queryString != null && queryString.length() > 0) {
			uri.append('?');
			uri.append(encodeUriQuery(queryString, false));
		}

		if (doSendUrlFragment && fragment != null) {
			uri.append('#');
			uri.append(encodeUriQuery(fragment, false));
		}
		return uri.toString();
	}

	protected String rewriteQueryStringFromRequest(HttpServletRequest servletRequest, String queryString) {
		return queryString;
	}

	protected String rewritePathInfoFromRequest(HttpServletRequest servletRequest) {
		return servletRequest.getPathInfo();
	}

	protected String rewriteUrlFromResponse(HttpServletRequest servletRequest, String theUrl) {
		final String targetUri = getTargetUri(servletRequest);
		if (theUrl.startsWith(targetUri)) {
			StringBuffer curUrl = servletRequest.getRequestURL();
			int pos;
			if ((pos = curUrl.indexOf("://")) >= 0) {
				if ((pos = curUrl.indexOf("/", pos + 3)) >= 0) {
					curUrl.setLength(pos);
				}
			}
			curUrl.append(servletRequest.getContextPath());
			curUrl.append(servletRequest.getServletPath());
			curUrl.append(theUrl, targetUri.length(), theUrl.length());
			return curUrl.toString();
		}
		return theUrl;
	}

	public String getTargetUri() {
		return targetUri;
	}

	protected CharSequence encodeUriQuery(CharSequence in, boolean encodePercent) {
		StringBuilder outBuf = null;
		Formatter formatter = null;
		for (int i = 0; i < in.length(); i++) {
			char c = in.charAt(i);
			boolean escape = true;
			if (c < 128) {
				if (asciiQueryChars.get(c) && !(encodePercent && c == '%')) {
					escape = false;
				}
			} else if (!Character.isISOControl(c) && !Character.isSpaceChar(c)) {
				escape = false;
			}
			if (!escape) {
				if (outBuf != null)
					outBuf.append(c);
			} else {
				if (outBuf == null) {
					outBuf = new StringBuilder(in.length() + 5 * 3);
					outBuf.append(in, 0, i);
					formatter = new Formatter(outBuf);
				}
				formatter.format("%%%02X", (int) c);
			}
		}
		return outBuf != null ? outBuf : in;
	}

	protected static final BitSet asciiQueryChars;
	static {
		char[] c_unreserved = "_-!.~'()*".toCharArray();
		char[] c_punct = ",;:$&+=".toCharArray();
		char[] c_reserved = "/@".toCharArray();
		asciiQueryChars = new BitSet(128);
		for (char c = 'a'; c <= 'z'; c++) {
			asciiQueryChars.set(c);
		}
		for (char c = 'A'; c <= 'Z'; c++) {
			asciiQueryChars.set(c);
		}
		for (char c = '0'; c <= '9'; c++) {
			asciiQueryChars.set(c);
		}
		for (char c : c_unreserved) {
			asciiQueryChars.set(c);
		}
		for (char c : c_punct) {
			asciiQueryChars.set(c);
		}
		for (char c : c_reserved) {
			asciiQueryChars.set(c);
		}

		asciiQueryChars.set('%');
	}

}
