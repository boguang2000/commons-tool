package cn.aotcloud.utils;

import org.apache.commons.codec.Charsets;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.aotcloud.utils.HttpTrustUtil.HttpTrustManager;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HttpUtil {

	protected static Logger logger = LoggerFactory.getLogger(HttpUtil.class);
	
    /**
     * HTTPCLIENT连接池配置
     */
    private static HttpClient HTTPCLIENT = null;

    /**
     * @param url    请求地址
     * @param params 请求参数
     * @return String
     * @Title: post
     * @Description:post请求
     */
    public static String post(String url, Map <String, String> params) {
        HTTPCLIENT = getHttpClient();

        String result = null;
        HttpPost httpPost = null;
        HttpEntity reqEntity = null;
        HttpEntity responseEntity = null;

        try {
            httpPost = new HttpPost(url);

            List <NameValuePair> formparams = new ArrayList <NameValuePair>();

            for (String name : params.keySet()) {
                formparams.add(new BasicNameValuePair(name, params.get(name)));
            }

            reqEntity = new UrlEncodedFormEntity(formparams, Consts.UTF_8);
            httpPost.setEntity(reqEntity);

            HttpResponse response = HTTPCLIENT.execute(httpPost);

            responseEntity = response.getEntity();
            result = EntityUtils.toString(responseEntity);
        
        } catch (ClientProtocolException e) {
        	logger.error(e.getMessage());
		} catch (IOException | ParseException e) {
            logger.error(e.getMessage());
		} finally {
            httpPost.releaseConnection();
            EntityUtils.consumeQuietly(reqEntity);
            EntityUtils.consumeQuietly(responseEntity);
        }

        return result;
    }

    /**
     * @param url  请求地址
     * @param json 请求json内容
     * @return JSONObject
     * @Title: postJson
     * @Description:post方法，不指定参数名
     */
    public static String postJson(String url, String json) {
        HTTPCLIENT = getHttpClient();

        HttpPost post = new HttpPost(url);
        HttpEntity resEntity = null;
        StringEntity entity = null;

        try {
            entity = new StringEntity(json, "UTF-8");
            entity.setContentEncoding("UTF-8");
            entity.setContentType("application/json");    // 设置为 json数据
            post.setEntity(entity);

            HttpResponse response = HTTPCLIENT.execute(post);

            resEntity = response.getEntity();

            String res = EntityUtils.toString(resEntity, "UTF-8");

            return res;
        } catch (ClientProtocolException e) {
        	logger.error(e.getMessage());
            return null;
		} catch (IOException | ParseException e) {
			logger.error(e.getMessage());
            return null;
		} finally {
            post.releaseConnection();
            EntityUtils.consumeQuietly(entity);
            EntityUtils.consumeQuietly(resEntity);
        }
    }

    /**
     * @param url  请求地址
     * @param file 待上传文件
     * @return JSONObject
     * @Title: postMediaFile
     * @Description:post方法，上传素材文件
     */
    public static String postMediaFile(String url, File file) {
        HTTPCLIENT = getHttpClient();

        HttpPost post = new HttpPost(url);
        HttpEntity reqEntity = null;
        HttpEntity resEntity = null;

        try {
            FileBody fileBody = new FileBody(file);
            MultipartEntityBuilder builder = MultipartEntityBuilder.create().addPart("media", fileBody);

            reqEntity = builder.build();
            post.setEntity(reqEntity);

            HttpResponse response = HTTPCLIENT.execute(post);

            resEntity = response.getEntity();

            String res = EntityUtils.toString(resEntity);

            return res;
            
        } catch (ClientProtocolException e) {
        	logger.error(e.getMessage());
            return null;
		} catch (IOException | ParseException e) {
			logger.error(e.getMessage());
            return null;
		} finally {
            post.releaseConnection();
            EntityUtils.consumeQuietly(reqEntity);
            EntityUtils.consumeQuietly(resEntity);
        }
    }

    /**
     * @param url 请求地址
     * @return String
     * @Title: get
     * @Description:get方法，当只需要请求地址就能取到想要得数据时使用此方法
     */
    public static String get(String url) {
        HTTPCLIENT = getHttpClient();
        HttpGet get = new HttpGet();
        String result = "";
        HttpEntity resEntity = null;
        try {
            get.setURI(new URI(url));
            HttpResponse response = HTTPCLIENT.execute(get);
            if (response != null) {
                resEntity = response.getEntity();
                if (resEntity != null) {
                    result = EntityUtils.toString(resEntity, "utf-8");
                }
            }
            get.releaseConnection();
            get.abort();
        } catch (URISyntaxException e) {
        	logger.error(e.getMessage());
		} catch (ClientProtocolException e) {
			logger.error(e.getMessage());
		} catch (IOException | ParseException e) {
			logger.error(e.getMessage());
		} finally {
            get.releaseConnection();
            get.abort();
            try {
                EntityUtils.consume(resEntity);
            } catch (IOException e) {
                logger.error(e.getMessage());
            }
        }

        return result;
    }
    
    /**
     * @param url 请求地址
     * @return String
     * @Title: get
     * @Description:get方法，当只需要请求地址就能取到想要得数据时使用此方法
     */
    public static String get(String url, String headerName, String headerValue) {
        HTTPCLIENT = getHttpClient();
        HttpGet get = new HttpGet();
        String result = "";
        HttpEntity resEntity = null;
        try {
            get.setURI(new URI(url));
            if(!StringUtils.isAnyBlank(headerName, headerValue)) {
        		get.setHeader(headerName, headerValue);
            }
            HttpResponse response = HTTPCLIENT.execute(get);
            if (response != null) {
                resEntity = response.getEntity();
                if (resEntity != null) {
                    result = EntityUtils.toString(resEntity, "utf-8");
                }
            }
            get.releaseConnection();
            get.abort();
        
        } catch (URISyntaxException e) {
        	logger.error(e.getMessage());
		} catch (ClientProtocolException e) {
			logger.error(e.getMessage());
		} catch (IOException | ParseException e) {
            logger.error(e.getMessage());
		} finally {
            get.releaseConnection();
            get.abort();
            try {
                EntityUtils.consume(resEntity);
            } catch (IOException e) {
                logger.error(e.getMessage());
            }
        }

        return result;
    }

    /**
     * @return HttpClient
     * @Title: getHttpClient
     * @Description:HTTPCLIENT实例化，自动
     */
    private static HttpClient getHttpClient() {
        if (HTTPCLIENT != null) {
            return HTTPCLIENT;
        } else {
            RegistryBuilder <ConnectionSocketFactory> registryBuilder =
                    RegistryBuilder. <ConnectionSocketFactory>create();
            ConnectionSocketFactory plainSF = new PlainConnectionSocketFactory();

            registryBuilder.register("http", plainSF);
            TrustManager[] trustAllCerts = new TrustManager[1];
            TrustManager tm = new HttpTrustManager();
            trustAllCerts[0] = tm;

            // 指定信任密钥存储对象和连接套接字工厂
            try {
                //KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
                SSLContext sslContext = SSLContext.getInstance("TLS");//sc = SSLContext.getInstance("TLS")
                sslContext.init(null, trustAllCerts, null);
                LayeredConnectionSocketFactory sslSF = new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE);

                registryBuilder.register("https", sslSF);
            } catch (KeyManagementException e) {
                throw new RuntimeException(e);
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }

            Registry <ConnectionSocketFactory> registry = registryBuilder.build();

            // 设置连接管理器
            int maxConn = 200;
            PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager(registry);

            // 设置编码格式
            connManager.setDefaultConnectionConfig(ConnectionConfig.custom()
                    .setCharset(
                            Charsets.toCharset(Charset.forName("UTF-8")))
                    .build());

            // 设置超时
            connManager.setDefaultSocketConfig(SocketConfig.custom().setSoTimeout(30000).build());

            // 设置最大路由
            connManager.setDefaultMaxPerRoute(maxConn);

            // 设置最大链接数
            connManager.setMaxTotal(maxConn);

            // 构建客户端
            HTTPCLIENT = HttpClientBuilder.create().setConnectionManager(connManager).build();

            return HTTPCLIENT;
        }
    }
//    //设置 https 请求证书
//    static class miTM implements TrustManager,javax.net.ssl.X509TrustManager {
//
//        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
//            return null;
//        }
//
//        public boolean isServerTrusted(
//                java.security.cert.X509Certificate[] certs) {
//            return true;
//        }
//
//        public boolean isClientTrusted(
//                java.security.cert.X509Certificate[] certs) {
//            return true;
//        }
//
//        public void checkServerTrusted(
//                java.security.cert.X509Certificate[] certs, String authType)
//                throws java.security.cert.CertificateException {
//            return;
//        }
//
//        public void checkClientTrusted(
//                java.security.cert.X509Certificate[] certs, String authType)
//                throws java.security.cert.CertificateException {
//            return;
//        }
//    }
    
    public static String sendPostRequestByJava(String reqURL, String sendData, Map<String, String> headers) {
    	return sendPostRequestByJava(reqURL, "POST", 150000, 150000, "application/json", sendData, headers);
	}
    
    public static String sendPostRequestByJava(String reqURL, String method, int connectTimeout, int readTimeout, String requestContentType, String sendData, Map<String, String> headers) {
		HttpURLConnection httpURLConnection = null;
		OutputStream out = null;
		InputStream in = null;
		int httpStatusCode = 0;
		ByteArrayOutputStream bos = null;
		try {
			URL sendUrl = new URL(reqURL);
			httpURLConnection = (HttpURLConnection) sendUrl.openConnection();
			httpURLConnection.setRequestMethod(method);
			httpURLConnection.setDoOutput(true);
			httpURLConnection.setUseCaches(false);
			httpURLConnection.setConnectTimeout(connectTimeout);
			httpURLConnection.setReadTimeout(readTimeout);
			httpURLConnection.setRequestProperty("Content-Type", requestContentType);

			if (headers != null && !headers.isEmpty()) {
				for (Map.Entry<String, String> entry : headers.entrySet()) {
					httpURLConnection.setRequestProperty(entry.getKey(), entry.getValue());
				}
			}
			out = httpURLConnection.getOutputStream();
			out.write(sendData.getBytes());
			out.flush();
			httpStatusCode = httpURLConnection.getResponseCode();

			in = httpURLConnection.getInputStream();
			byte[] by = new byte[1024];
			bos = new ByteArrayOutputStream();
			int len = -1;
			while ((len = in.read(by)) != -1) {
				bos.write(by, 0, len);
			}
			return bos.toString("utf-8");
		
		} catch (MalformedURLException e) {
			return "Failed" + httpStatusCode;
		} catch (IOException e) {
			return "Failed" + httpStatusCode;
		} finally {
			IOUtils.closeQuietly(out);
			IOUtils.closeQuietly(in);
			IOUtils.closeQuietly(bos);
			if (httpURLConnection != null) {
				httpURLConnection.disconnect();
			}
		}
	}
}
