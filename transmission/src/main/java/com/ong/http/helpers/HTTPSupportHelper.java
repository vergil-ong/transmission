package com.ong.http.helpers;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.ConnectionReuseStrategy;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.HttpContext;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.TrustStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ong.bean.helpers.BeanHelper;

/**
 * 基于httpclient的http请求包装
 * 
 * @Description:
 * @Author: Ong
 * @CreateDate: 2017-10-25 12:00:00
 * @E-mail: 865208597@qq.com
 */
public class HTTPSupportHelper {
	private static Logger logger = LoggerFactory.getLogger(HTTPSupportHelper.class);

	private static Map<String, CloseableHttpClient> HTTP_CLIENTS = new HashMap();
	private static final Object SYNC_LOCK = new Object();

	private static final int MAX_TOTAL = 0;
	private static final int MAX_PER_ROUTE = 1;
	private static final int DEFAULT_MAX_PER_ROUTE = 2;

	public static CloseableHttpClient getHttpClient(String url, String keyStore, String keyStorePassword) {
		String[] urlArr = url.split("/");

		String key = urlArr[0] + "//" + urlArr[2];
		CloseableHttpClient httpClient = (CloseableHttpClient) HTTP_CLIENTS.get(key);
		if (httpClient == null) {
			synchronized (SYNC_LOCK) {
				httpClient = (CloseableHttpClient) HTTP_CLIENTS.get(key);
				if (httpClient == null) {
					String host = urlArr[2];

					int port = 80;
					if (host.contains(":")) {
						String[] arr = host.split(":");
						host = arr[0];
						port = TypeHelper.toInt(arr[1]);
					}
					httpClient = createHttpClient(host, port, url.toUpperCase().startsWith("HTTPS://"), keyStore,
							keyStorePassword);
					logger.debug("HTTP适配器，新建HttpClient实例[{0}]成功", new Object[] { key });
					HTTP_CLIENTS.put(key, httpClient);
					logger.debug("HTTP适配器，缓存HttpClient实例[{0}]成功", new Object[] { key });
				}
			}
		} else {
			logger.debug("HTTP适配器，命中HttpClient实例[{0}]缓存", new Object[] { key });
		}
		return httpClient;
	}

	private static CloseableHttpClient createHttpClient(String host, int port, boolean isSSL, String keyStore,
			String keyStorePassword) {
		HttpClientBuilder httpClientBuilder = HttpClients.custom();
		RegistryBuilder<ConnectionSocketFactory> registryBuilder = RegistryBuilder.create();
		if (isSSL) {
			LayeredConnectionSocketFactory sslsf = SSLConnectionSocketFactory.getSocketFactory();
			configSSLContext(httpClientBuilder, keyStore, keyStorePassword);
			registryBuilder.register("https", sslsf);
		} else {
			ConnectionSocketFactory plainsf = PlainConnectionSocketFactory.getSocketFactory();
			registryBuilder.register("http", plainsf);
		}
		PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(
				registryBuilder.build());
		connectionManager.setMaxTotal(MAX_TOTAL);
		connectionManager.setMaxPerRoute(new HttpRoute(new HttpHost(host, port)), MAX_PER_ROUTE);
		connectionManager.setDefaultMaxPerRoute(DEFAULT_MAX_PER_ROUTE);

		httpClientBuilder.setConnectionManager(connectionManager);

		httpClientBuilder.setRetryHandler(HTTPAdapterRequestRetryHandler.newInstance());

		httpClientBuilder.setConnectionReuseStrategy(new ConnectionReuseStrategy() {
			public boolean keepAlive(HttpResponse response, HttpContext context) {
				return false;
			}
		});
		return httpClientBuilder.build();
	}

	private static void configSSLContext(HttpClientBuilder httpClientBuilder, String keyStore,
			String keyStorePassword) {
		SSLContext context = null;
		if ((BeanHelper.isEmpty(keyStore)) || (BeanHelper.isEmpty(keyStorePassword))) {
			HostnameVerifier verifier = new HostnameVerifier() {
				public boolean verify(String arg0, SSLSession arg1) {
					return true;
				}
			};
			httpClientBuilder.setSSLHostnameVerifier(verifier);

			X509TrustManager tm = new X509TrustManager() {
				public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
				}

				public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
				}

				public X509Certificate[] getAcceptedIssuers() {
					return null;
				}
			};
			try {
				context = SSLContext.getInstance("TLS");
				context.init(null, new TrustManager[] { tm }, null);
				httpClientBuilder.setSslcontext(context);
			} catch (NoSuchAlgorithmException e) {
				throw new BaseException("加密算法不存在", new Object[] { e });
			} catch (KeyManagementException e) {
				throw new BaseException("密钥管理异常", new Object[] { e });
			}
		} else {
			FileInputStream fis = null;
			try {
				fis = new FileInputStream(FileHelper.CERTIFICATE_DIR + File.separator + keyStore);
				KeyStore store = KeyStore.getInstance("JKS");
				store.load(fis, keyStorePassword.toCharArray());
				context = new SSLContextBuilder().loadTrustMaterial(store, new TrustStrategy() {
					public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
						return true;
					}
				}).build();
			} catch (IOException e) {
				throw new BaseException("加载证书文件异常", new Object[] { e });
			} catch (KeyStoreException e) {
				throw new BaseException("证书初始化异常", new Object[] { e });
			} catch (NoSuchAlgorithmException e) {
				throw new BaseException("加密算法不存在", new Object[] { e });
			} catch (CertificateException e) {
				throw new BaseException("证书过期或证书异常", new Object[] { e });
			} catch (KeyManagementException e) {
				throw new BaseException("密钥管理异常", new Object[] { e });
			} finally {
				if (fis != null) {
					try {
						fis.close();
					} catch (IOException e) {
						throw new BaseException("关闭FileInputStream异常", new Object[] { e });
					}
				}
			}
		}
	}
}
