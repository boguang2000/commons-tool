package cn.aotcloud.utils;

import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

public class RestTemplateUtil {
	
	protected Logger logger = LoggerFactory.getLogger(getClass());

	private PoolingHttpClientConnectionManager connManager;
	
	public PoolingHttpClientConnectionManager getConnManager() {
		return connManager;
	}

	public RestTemplate getSSLRestTemplate(int connectTimeout, int readTimeout, int connectionRequestTimeout, int maxConnTotal, int maxConnPerRoute) {
		//SSLContext sslContext = SSLContexts.createSystemDefault();
    	SSLContext sslContext = getSSLContext();
    	SSLConnectionSocketFactory sslConnectionSocketFactory = new SSLConnectionSocketFactory(
    			sslContext, new String[] { "TLSv1", "TLSv1.1", "TLSv1.2", "SSLv3" }, null,
    	        SSLConnectionSocketFactory.getDefaultHostnameVerifier());

    	Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
    	        .register("http", PlainConnectionSocketFactory.INSTANCE)
    	        .register("https", sslConnectionSocketFactory)
    	        .build();
    	connManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
    	HttpClientBuilder httpClientBuilder = HttpClients.custom();
    	httpClientBuilder.setConnectionManager(connManager)
		 				 .setMaxConnTotal(maxConnTotal)
		 				 .setMaxConnPerRoute(maxConnPerRoute);
    	
        HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory(httpClientBuilder.build());
        // 连接超时 30000
        clientHttpRequestFactory.setConnectTimeout(connectTimeout);
        // 数据读取超时时间，即SocketTimeout 30000
        clientHttpRequestFactory.setReadTimeout(readTimeout);
        // 连接不够用的等待时间，不宜过长，必须设置，比如连接不够用时，时间过长将是灾难性的 10000
        clientHttpRequestFactory.setConnectionRequestTimeout(connectionRequestTimeout);
        // 缓冲请求数据，默认值是true。通过POST或者PUT大量发送数据时，建议将此属性更改为false，以免耗尽内存。
        clientHttpRequestFactory.setBufferRequestBody(false);
        
        RestTemplate restTemplate = getUTF8restTemplate();
        restTemplate.setRequestFactory(clientHttpRequestFactory);
        
        //开启监控线程,对异常和空闲线程进行关闭
        closeConnectionsScheduled(connManager);
        
        return restTemplate;
    }
	
	public RestTemplate getTrustTemplate() {
        //SSLContext sslContext = SSLContexts.createSystemDefault();
        SSLContext sslContext = getSSLContext();
    	SSLConnectionSocketFactory sslConnectionSocketFactory = new SSLConnectionSocketFactory(
    			sslContext, new String[] { "TLSv1", "TLSv1.1", "TLSv1.2", "SSLv3" }, null,
    	        SSLConnectionSocketFactory.getDefaultHostnameVerifier());

    	Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
    	        .register("http", PlainConnectionSocketFactory.INSTANCE)
    	        .register("https", sslConnectionSocketFactory)
    	        .build();
    	connManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
        // httpClient连接配置，底层是配置RequestConfig
        HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory();
        RestTemplate restTemplate = getUTF8restTemplate();
        restTemplate.setRequestFactory(clientHttpRequestFactory);
        //开启监控线程,对异常和空闲线程进行关闭
        closeConnectionsScheduled(connManager);
        
        return restTemplate;
    }
	
	public RestTemplate getUTF8restTemplate() {
		RestTemplate restTemplate = new RestTemplate();
        //用UTF-8 StringHttpMessageConverter替换默认StringHttpMessageConverter
        List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();
        for(HttpMessageConverter<?> converter : restTemplate.getMessageConverters()){
            if(converter instanceof StringHttpMessageConverter){
                // 默认的是ios 8859-1
                StringHttpMessageConverter messageConverter = new StringHttpMessageConverter(StandardCharsets.UTF_8);
                messageConverters.add(messageConverter);
            }else {
            	messageConverters.add(converter);
            }
        }
        restTemplate.setMessageConverters(messageConverters);
        
        return restTemplate;
	}
	
	/**
	 * 忽略HTTPS证书
	 * @return
	 */
	private SSLContext getSSLContext() {
        try {
            // 这里可以填两种值 TLS和LLS , 具体差别可以自行搜索
            SSLContext sc = SSLContext.getInstance("TLS");
            // 构建新对象
            X509TrustManager manager = HttpTrustUtil.createX509TrustManager();
            sc.init(null, new TrustManager[]{manager}, null);
            return sc;
        } catch (NoSuchAlgorithmException e) {
        	logger.error(e.getMessage(), e);
		} catch (KeyManagementException e) {
			logger.error(e.getMessage(), e);
		}
        return null;
    }
	
	public void closeConnectionsScheduled(PoolingHttpClientConnectionManager connManager) {
		//开启监控线程,对异常和空闲线程进行关闭
        ScheduledExecutorService monitorExecutor = Executors.newScheduledThreadPool(1);
        monitorExecutor.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                //关闭异常连接
            	connManager.closeExpiredConnections();
                //关闭5s空闲的连接
            	connManager.closeIdleConnections(5, TimeUnit.SECONDS);
            }
        }, 5, 5, TimeUnit.SECONDS);
	}
}
