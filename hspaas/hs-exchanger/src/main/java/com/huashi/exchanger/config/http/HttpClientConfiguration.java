package com.huashi.exchanger.config.http;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLException;

import org.apache.http.HeaderElement;
import org.apache.http.HeaderElementIterator;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

/**
 * 
 * TODO HTTP配置信息
 *
 * @author zhengying
 * @version V1.0.0
 * @date 2017年11月8日 下午9:55:32
 */
//@Configuration
public class HttpClientConfiguration {

	// 重试次数
	@Value("${httpclient.retryTimes}")
	private int retryTimes;
	/**
	 * 连接池最大连接数
	 */
	@Value("${httpclient.connMaxTotal}")
	private int connMaxTotal = 20;
	/** 
	 *  
	 */
	@Value("${httpclient.maxPerRoute}")
	private int maxPerRoute = 20;

	/**
	 * 连接存活时间，单位为s
	 */
	@Value("${httpclient.timeToLive}")
	private int timeToLive = 60;
	@Value("${httpclient.keepAliveTime}")
	private int keepAliveTime = 30;

	// 代理的host地址
	@Value("${httpclient.proxyhost}")
	private String proxyHost;
	// 代理的端口号
	@Value("${httpclient.proxyPort}")
	private int proxyPort = 6060;

	// 建立连接超时时间
	@Value("${httpclient.connectTimeout}")
	private int connectTimeout = 5000;
	// 连接池中获取连接超时时间
	@Value("${httpclient.connectRequestTimeout}")
	private int connectRequestTimeout = 5000;
	// 连接数据相应超时时间
	@Value("${httpclient.socketTimeout}")
	private int socketTimeout = 5000;

	/**
	 * 
	 * TODO 声明重试处理器
	 * 
	 * @return
	 */
	@Bean
	public HttpRequestRetryHandler httpRequestRetryHandler() {
		// 请求重试
		final int retryTime = this.retryTimes;
		return new HttpRequestRetryHandler() {
			@Override
            public boolean retryRequest(IOException exception, int executionCount, HttpContext context) {
				// Do not retry if over max retry
				// count,如果重试次数超过了retryTime,则不再重试请求
				if (executionCount >= retryTime) {
					return false;
				}
				// 服务端断掉客户端的连接异常
				if (exception instanceof NoHttpResponseException) {
					return true;
				}
				// time out 超时重试
				if (exception instanceof InterruptedIOException) {
					return true;
				}
				// Unknown host
				if (exception instanceof UnknownHostException) {
					return false;
				}
				// Connection refused
				if (exception instanceof ConnectTimeoutException) {
					return false;
				}
				// SSL handshake exception
				if (exception instanceof SSLException) {
					return false;
				}
				HttpClientContext clientContext = HttpClientContext.adapt(context);
				HttpRequest request = clientContext.getRequest();
				if (!(request instanceof HttpEntityEnclosingRequest)) {
					return true;
				}
				return false;
			}
		};
	}

	@Bean
	public PoolingHttpClientConnectionManager poolingClientConnectionManager() {
		PoolingHttpClientConnectionManager poolHttpcConnManager = new PoolingHttpClientConnectionManager(
				this.timeToLive, TimeUnit.SECONDS);
		// 最大连接数
		poolHttpcConnManager.setMaxTotal(this.connMaxTotal);
		// 路由基数
		poolHttpcConnManager.setDefaultMaxPerRoute(this.maxPerRoute);
		return poolHttpcConnManager;
	}

	/**
	 * 
	 * TODO 连接保持策略
	 * 
	 * @return
	 */
	@Bean("connectionKeepAliveStrategy")
	public ConnectionKeepAliveStrategy connectionKeepAliveStrategy() {
		return new ConnectionKeepAliveStrategy() {

			@Override
            public long getKeepAliveDuration(HttpResponse response, HttpContext context) {
				// Honor 'keep-alive' header
				HeaderElementIterator it = new BasicHeaderElementIterator(
						response.headerIterator(HTTP.CONN_KEEP_ALIVE));
				while (it.hasNext()) {
					HeaderElement he = it.nextElement();
					String param = he.getName();
					String value = he.getValue();
					if (value != null && param.equalsIgnoreCase("timeout")) {
						try {
							return Long.parseLong(value) * 1000;
						} catch (NumberFormatException ignore) {
						}
					}
				}
				return keepAliveTime * 1000;
			}
		};
	}

	/**
	 * 
	 * TODO HttpClient代理（暂不用）
	 * 
	 * @return
	 */
	@Bean
	public DefaultProxyRoutePlanner defaultProxyRoutePlanner() {
		HttpHost proxy = new HttpHost(this.proxyHost, this.proxyPort);
		return new DefaultProxyRoutePlanner(proxy);
	}

	@Bean
	public RequestConfig requestConfig() {
		return RequestConfig.custom().setConnectionRequestTimeout(this.connectRequestTimeout)
				.setConnectTimeout(this.connectTimeout).setSocketTimeout(this.socketTimeout).build();
	}
	
	
}
