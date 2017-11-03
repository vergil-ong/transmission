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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.ConnectionReuseStrategy;
import org.apache.http.Consts;
import org.apache.http.HeaderIterator;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.TrustStrategy;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ong.util.DateHelper;
import com.ong.util.MapHelper;
import com.ong.util.StringHelper;
import com.ong.util.TypeHelper;

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

	private static Map<String, CloseableHttpClient> HTTP_CLIENTS = new HashMap<String, CloseableHttpClient>();
	private static final Object SYNC_LOCK = new Object();

	private static final int MAX_TOTAL = 10;
	private static final int MAX_PER_ROUTE = 10;
	private static final int DEFAULT_MAX_PER_ROUTE = 20;
	private static final int TIME_OUT = 20000;

	private static final String CERTIFICATE_DIR = "";

	private static final String EQUALSCONST = "=";

	/**
	 * 响应cookie <br/>
	 * List<Cookie> 类型
	 */
	private static final String RESPONSECOOKIE = "responseCookie";
	/**
	 * 响应头 <br/>
	 * List<String>类型
	 */
	private static final String RESPONSEHEADER = "responseHeader";
	/**
	 * 响应状态 <br/>
	 * int 类型
	 */
	private static final String RESPONSESTATUS = "responseStatus";
	/**
	 * 响应报文体 <br/>
	 * String 类型
	 */
	private static final String RESPONSEBODY = "responseBody";

	/**
	 * 是否需要cookie
	 */
	private static final String REQUIRECOOKIE = "requireCookie";

	/**
	 * 需要cookie
	 */
	private static final String COOKIE_REQUIRE = "yes";

	/**
	 * 不需要cookie
	 */
	// private static final String COOKIE_UNREQUIRE = "no";

	private static final String DEFAULTCHARSET = "UTF-8";

	/**
	 * 从缓冲中获取http链接
	 * 
	 * @param url
	 * @param keyStore
	 * @param keyStorePassword
	 * @param redirectsEnabled
	 * @return
	 */
	public static CloseableHttpClient getHttpClient(String url, String keyStore, String keyStorePassword,
			boolean redirectsEnabled) {
		String[] urlArr = url.split("/");

		String key = urlArr[0] + "//" + urlArr[2] + "_" + redirectsEnabled;
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
							keyStorePassword, redirectsEnabled);
					logger.debug("HTTP适配器，新建HttpClient实例[{}]成功", new Object[] { key });
					HTTP_CLIENTS.put(key, httpClient);
					logger.debug("HTTP适配器，缓存HttpClient实例[{}]成功", new Object[] { key });
				}
			}
		} else {
			logger.debug("HTTP适配器，命中HttpClient实例[{}]缓存", new Object[] { key });
		}
		return httpClient;
	}

	/**
	 * 创建http链接
	 * 
	 * @param host
	 * @param port
	 * @param isSSL
	 * @param keyStore
	 * @param keyStorePassword
	 * @param redirectsEnabled
	 * @return
	 */
	private static CloseableHttpClient createHttpClient(String host, int port, boolean isSSL, String keyStore,
			String keyStorePassword, boolean redirectsEnabled) {
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

		httpClientBuilder.setRetryHandler(HTTPRetryHandler.newInstance());

		httpClientBuilder.setConnectionReuseStrategy(new ConnectionReuseStrategy() {
			public boolean keepAlive(HttpResponse response, HttpContext context) {
				return false;
			}
		});
		httpClientBuilder.setDefaultRequestConfig(createConfig(TIME_OUT, redirectsEnabled));
		return httpClientBuilder.build();
	}

	/**
	 * 配置请求信息
	 * 
	 * @param timeout
	 * @param redirectsEnabled
	 * @return
	 */
	public static RequestConfig createConfig(int timeout, boolean redirectsEnabled) {
		return RequestConfig.custom().setSocketTimeout(timeout).setConnectTimeout(timeout)
				.setConnectionRequestTimeout(timeout).setRedirectsEnabled(redirectsEnabled).build();
	}

	/**
	 * 设置ssl证书
	 * 
	 * @param httpClientBuilder
	 * @param keyStore
	 * @param keyStorePassword
	 */
	@SuppressWarnings("deprecation")
	private static void configSSLContext(HttpClientBuilder httpClientBuilder, String keyStore,
			String keyStorePassword) {
		SSLContext context = null;
		if ((StringHelper.isEmpty(keyStore)) || (StringHelper.isEmpty(keyStorePassword))) {
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
				logger.error("加密算法不存在 {}", e);
			} catch (KeyManagementException e) {
				logger.error("密钥管理异常 {}", e);
			}
		} else {
			FileInputStream fis = null;
			try {
				fis = new FileInputStream(CERTIFICATE_DIR + File.separator + keyStore);
				KeyStore store = KeyStore.getInstance("JKS");
				store.load(fis, keyStorePassword.toCharArray());
				context = new SSLContextBuilder().loadTrustMaterial(store, new TrustStrategy() {
					public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
						return true;
					}
				}).build();
			} catch (IOException e) {
				logger.error("加载证书文件异常  {}", e);
			} catch (KeyStoreException e) {
				logger.error("证书初始化异常  {}", e);
			} catch (NoSuchAlgorithmException e) {
				logger.error("加密算法不存在  {}", e);
			} catch (CertificateException e) {
				logger.error("证书过期或证书异常  {}", e);
			} catch (KeyManagementException e) {
				logger.error("密钥管理异常  {}", e);
			} finally {
				if (fis != null) {
					try {
						fis.close();
					} catch (IOException e) {
						logger.error("关闭FileInputStream异常 {}", e);
					}
				}
			}
		}
	}

	/**
	 * 请求执行
	 * 
	 * @param url
	 * @param request
	 * @param charset
	 * @param keyStore
	 * @param keyStorePassword
	 * @param redirectsEnabled
	 * @param context
	 * @return
	 */
	public static boolean doRequest(String url, HttpRequestBase request, String charset, String keyStore,
			String keyStorePassword, boolean redirectsEnabled, Map<String, Object> context) {
		boolean resultBol = false;

		CloseableHttpResponse response = null;

		HttpEntity httpEntity = null;

		String responseMessage = null;

		CloseableHttpClient httpClient = null;

		CookieStore cookieStore = null;

		try {
			long statrTime = DateHelper.getNowDate().getTime();
			httpClient = getHttpClient(url, keyStore, keyStorePassword, redirectsEnabled);

			HttpClientContext localContext = HttpClientContext.create();
			if (COOKIE_REQUIRE.equals(TypeHelper.toString(context.get(REQUIRECOOKIE)))) {
				// 需要cookie
				cookieStore = new BasicCookieStore();
				localContext.setCookieStore(cookieStore);
			} else {
				// 不需要cookie，默认不需要cookie
			}
			response = httpClient.execute(request, localContext);
			if (response != null) {
				int status = response.getStatusLine().getStatusCode();
				context.put(RESPONSESTATUS, TypeHelper.toString(status));
				if (status == 200) {
					httpEntity = response.getEntity();

					responseMessage = EntityUtils.toString(httpEntity, charset);
					logger.debug("HTTP适配器，服务端响应报文[{0}毫秒]：\n{}\n{}\n{}\n",
							new Object[] {
									Long.valueOf(
											DateHelper.getTimeDifference(statrTime, DateHelper.getNowDate().getTime())),
									"begin -----------------------------------", responseMessage,
									"end -------------------------------------" });
					context.put(RESPONSEBODY, responseMessage);
					resultBol = true;
				} else {
					logger.error("服务端异常[{}]", new Object[] { Integer.valueOf(status) });
				}
				context.put(RESPONSEHEADER, getHeader(response));
				context.put(RESPONSECOOKIE, cookieStore == null ? null : cookieStore.getCookies());
			}

			logger.info("response status is {}", context.get(RESPONSESTATUS));
			logger.info("response body is {}", context.get(RESPONSEBODY));
			logger.info("response header is {}", context.get(RESPONSEHEADER));
			logger.info("response cookie is {}", context.get(RESPONSECOOKIE));
			return resultBol;
		} catch (Exception e) {
			logger.error("HTTP适配器，服务端[{}]响应异常", new Object[] { url, e });
			return false;
		} finally {
			request.abort();
			if (response != null) {
				try {
					EntityUtils.consume(httpEntity);
				} catch (IOException e) {
					logger.error("关闭InputStream异常", new Object[] { e });
				}
				try {
					response.close();
				} catch (IOException e) {
					logger.error("关闭CloseableHttpResponse异常", new Object[] { e });
				}
			}
		}
	}

	/**
	 * 从response 中 获取 header
	 * 
	 * @param httpResponse
	 * @return
	 */
	public static List<String> getHeader(HttpResponse httpResponse) {
		List<String> resutlList = new ArrayList<String>();
		HeaderIterator iterator = httpResponse.headerIterator();
		while (iterator.hasNext()) {
			resutlList.add(iterator.next().toString());
		}
		return resutlList;
	}

	/**
	 * get请求
	 * 
	 * @param url
	 * @param params
	 * @param headers
	 * @param context
	 * @return
	 */
	public static boolean doGet(String url, Map<String, Object> params, Map<String, String> headers,
			Map<String, Object> context) {
		return doGet(url, null, null, params, headers, true, context);
	}

	/**
	 * get请求
	 * 
	 * @param url
	 * @param params
	 * @param context
	 * @return
	 */
	public static boolean doGet(String url, Map<String, Object> params, Map<String, Object> context) {
		return doGet(url, null, null, params, null, true, context);
	}

	/**
	 * get请求
	 * 
	 * @param url
	 * @param keyStore
	 * @param keyStorePassword
	 * @param params
	 * @param headers
	 * @param redirectsEnabled
	 * @param context
	 * @return
	 */
	public static boolean doGet(String url, String keyStore, String keyStorePassword, Map<String, Object> params,
			Map<String, String> headers, boolean redirectsEnabled, Map<String, Object> context) {
		// 设置请求参数
		StringBuilder sbUrl = new StringBuilder(url);
		if (!MapHelper.isMapEmpty(params)) {
			Iterator<String> keyIter = params.keySet().iterator();
			while (keyIter.hasNext()) {
				String key = keyIter.next();
				String value = TypeHelper.toString(params.get(key));
				if (sbUrl.indexOf("?") > -1) {
					sbUrl.append("&");
				} else {
					sbUrl.append("?");
				}
				sbUrl.append(key).append(EQUALSCONST).append(value);
			}
		}

		HttpGet httpGet = new HttpGet(sbUrl.toString());
		logger.info("execute get url {}", sbUrl);

		// 设置请求头
		if (!MapHelper.isMapEmpty(headers)) {
			Iterator<String> headerIterator = headers.keySet().iterator();

			while (headerIterator.hasNext()) {
				String headerKey = headerIterator.next();
				httpGet.addHeader(headerKey, headers.get(headerKey));
			}
		}

		return doRequest(url, httpGet, DEFAULTCHARSET, keyStore, keyStorePassword, redirectsEnabled, context);
	}

	/**
	 * httpClient post请求
	 * 
	 * @param url
	 *            请求url
	 * @param soapXml
	 *            String型的参数
	 * @param headers
	 *            请求headers头信息
	 * @param context
	 *            响应总线 里面包含响应所用信息
	 * @return 200为成功 其他为失败
	 */
	public static boolean postStringParams(String url, String soapXml, Map<String, String> headers,
			Map<String, Object> context) {
		HttpPost httpPost = new HttpPost(url);
		logger.info("execute post url {}", url);

		// 设置请求参数
		if (soapXml != null) {
			StringEntity entity = new StringEntity(soapXml, Consts.UTF_8);
			httpPost.setEntity(entity);
		}

		return doRequest(url, httpPost, DEFAULTCHARSET, null, null, false, context);
	}

	/**
	 * httpClient post请求
	 * 
	 * @param url
	 *            请求url
	 * @param formparams
	 *            请求参数 form提交适用
	 * @param headers
	 *            请求headers头信息
	 * @param context
	 *            响应总线 里面包含响应所用信息
	 * @return 200为成功 其他为失败
	 *
	 */
	public static boolean PostFormParams(String url, List<NameValuePair> formparams, Map<String, String> headers,
			Map<String, Object> context) {
		HttpPost httpPost = new HttpPost(url);
		logger.info("execute post url {}", url);

		// 设置请求参数
		if (formparams != null && !formparams.isEmpty()) {
			UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(formparams, Consts.UTF_8);
			httpPost.setEntity(urlEncodedFormEntity);
		}

		return doRequest(url, httpPost, DEFAULTCHARSET, null, null, false, context);
	}

	/**
	 * httpClient post请求
	 * 
	 * @param url
	 *            请求url
	 * @param formparams
	 *            请求参数 form提交适用
	 * @param headers
	 *            请求头信息
	 * @param context
	 *            响应总线 里面包含响应所用信息
	 * @return 200为成功 其他为失败
	 *
	 */
	public static boolean PostFormParams(String url, Map<String, Object> formparams, Map<String, String> headers,
			Map<String, Object> context) {
		List<NameValuePair> list = Map2NVpair(formparams);
		return PostFormParams(url, list, headers, context);
	}

	public static List<NameValuePair> Map2NVpair(Map<String, Object> map) {
		List<NameValuePair> resultList = new ArrayList<NameValuePair>();

		Iterator<String> keyIte = map.keySet().iterator();
		while (keyIte.hasNext()) {
			String key = keyIte.next();
			resultList.add(new BasicNameValuePair(key, TypeHelper.toString(map.get(key))));
		}

		return resultList;
	}

	/**
	 * httpClient post请求 发送文件
	 * 
	 * @param url
	 *            请求url
	 * @param formparams
	 *            请求参数 form提交适用
	 * @param headers
	 *            请求headers头信息
	 * @param files
	 *            需要上传的文件
	 * @param context
	 *            响应总线 里面包含响应所用信息
	 * @return 200为成功 其他为失败
	 *
	 */
	public static boolean PostFormParamsFiles(String url, List<NameValuePair> formparams, Map<String, String> headers,
			List<File> files, Map<String, Object> context) {

		HttpPost httpPost = new HttpPost(url);
		logger.info("execute post url {}", url);

		MultipartEntityBuilder builder = null;
		HttpEntity reqEntity = null;
		// 设置请求参数
		if (formparams != null && !formparams.isEmpty()) {
			builder = MultipartEntityBuilder.create();
			for (NameValuePair formparam : formparams) {
				builder.addPart(formparam.getName(), new StringBody(formparam.getValue(), ContentType.TEXT_PLAIN));
			}
		}

		// 设置上传文件
		if (files != null) {
			if (builder == null)
				builder = MultipartEntityBuilder.create();
			for (File file : files) {
				FileBody fileBody = new FileBody(file);
				builder.addPart(file.getName(), fileBody);
			}
		}

		if (builder != null) {
			reqEntity = builder.build();
			httpPost.setEntity(reqEntity);
		}

		return doRequest(url, httpPost, DEFAULTCHARSET, null, null, false, context);
	}
}
