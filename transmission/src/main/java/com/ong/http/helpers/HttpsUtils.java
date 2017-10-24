package com.ong.http.helpers;

import java.io.File;
import java.io.IOException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLContext;

import org.apache.http.Consts;
import org.apache.http.HeaderIterator;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.AllowAllHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 基于httpclient的http请求包装
 * @Description:  用户Bean类
 * @Author:       Ong
 * @CreateDate:   2017-05-20 12:00:00
 * @E-mail:		  865208597@qq.com
 */
@SuppressWarnings("deprecation")
public class HttpsUtils {
	private static final String HTTP = "http";
    private static final String HTTPS = "https";
    private static SSLConnectionSocketFactory sslsf = null;
    private static PoolingHttpClientConnectionManager cm = null;
    private static RequestConfig requestConfig = null;
    private static CookieStore cookieStore = null;
    
    /**
     * 响应cookie
     */
    private static final String RESPONSECOOKIE = "responseCookie";
    /**
     * 响应头
     */
    private static final String RESPONSEHEADER = "responseHeader";
    /**
     * 响应状态
     */
    private static final String RESPONSESTATUS = "responseStatus";
    /**
     * 响应报文体
     */
    private static final String RESPONSEBODY = "responseBody";
    
    private static String equalsConst = "="; 
    
    private static final String EMPTYSTR = "";
    
    private static Logger logger = LoggerFactory.getLogger(HttpsUtils.class);
    
    static {
        try {
        	SSLContext sslContext = SSLContexts.custom().useTLS().loadTrustMaterial(null, new TrustStrategy() {
        		
        		public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                    //信任所有
                    return true;
                }
            }).build();
            sslsf = new SSLConnectionSocketFactory(sslContext, new AllowAllHostnameVerifier());

            Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
                    .register(HTTP, new PlainConnectionSocketFactory())
                    .register(HTTPS, sslsf)
                    .build();
            cm = new PoolingHttpClientConnectionManager(registry);
            cm.setMaxTotal(200);//max connection
            cm.setDefaultMaxPerRoute(350);
            
            
            SocketConfig socketConfig = SocketConfig.custom()
                    .setSoKeepAlive(true)
                    .setTcpNoDelay(true)
                    .setSoTimeout(20000)
                    .build();
            cm.setDefaultSocketConfig(socketConfig);

            requestConfig = RequestConfig.custom().setConnectionRequestTimeout(20000).setConnectTimeout(20000).setSocketTimeout(20000).build();
            
            cookieStore = new BasicCookieStore();  
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * httpClient post请求
     * @param url 请求url
     * @param formparams 请求参数 form提交适用
     * @param context 响应总线 里面包含响应所用信息
     * @return 200为成功 其他为失败
     *
     */
    public static boolean postFormParams(String  url, Map<String,Object> formparams, Map<String,Object> context) {
    	List<NameValuePair> list = map2NVpair(formparams);
    	return postFormParams(url, list, null, context);
    }
    
    /**
     * httpClient post请求
     * @param url 请求url
     * @param formparams 请求参数 form提交适用
     * @param context 响应总线 里面包含响应所用信息
     * @return 200为成功 其他为失败
     *
     */
    public static boolean postFormParams(String  url, List<NameValuePair> formparams, Map<String,Object> context) {
    	return postFormParams(url, formparams, null, context);
    }
    
    /**
     * httpClient post请求
     * @param url 请求url
     * @param formparams 请求参数 form提交适用
     * @param headers 请求headers头信息
     * @param context 响应总线 里面包含响应所用信息
     * @return 200为成功 其他为失败
     *
     */
    public static boolean postFormParams(String  url, List<NameValuePair> formparams, Map<String,String> headers, Map<String,Object> context) {
    	//定义总线
    	boolean retBol = false;
        CloseableHttpClient httpClient = null;
        HttpPost httpPost = null;
        try {
            httpClient = getHttpClient();
            httpPost = new HttpPost(url);
            // 设置请求参数
            if(formparams!=null && !formparams.isEmpty()){
            	UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(formparams, Consts.UTF_8);
                httpPost.setEntity(urlEncodedFormEntity);
            }
            //设置请求头
            if(!isMapEmpty(headers)){
            	Iterator<String> headerIterator = headers.keySet().iterator();
            	
            	while(headerIterator.hasNext()){
            		String headerKey = headerIterator.next();
            		httpPost.addHeader(headerKey, headers.get(headerKey));
            	}
            }
            
            logger.info("execute post url {}",url);
            HttpResponse httpResponse = httpClient.execute(httpPost);
            
            //封装结果
            int statusCode = httpResponse.getStatusLine().getStatusCode();
            context.put(RESPONSESTATUS, String.valueOf(statusCode));
            
            String responseBodyStr = new String();
            
            if (statusCode == HttpStatus.SC_OK) {
                HttpEntity resEntity = httpResponse.getEntity();
                responseBodyStr = EntityUtils.toString(resEntity);
                retBol = true;
            } else {
            	responseBodyStr = readHttpResponse(httpResponse);
            	retBol = false;
            }
            
            context.put(RESPONSEBODY, responseBodyStr);
            
            context.put(RESPONSEHEADER,getHeader(httpResponse));
            context.put(RESPONSECOOKIE,cookieStore.getCookies());
            
            //logger response
            logger.info("response status is {}",context.get(RESPONSESTATUS));
            logger.info("response body is {}",context.get(RESPONSEBODY));
            logger.info("response header is {}",context.get(RESPONSEHEADER));
            logger.info("response cookie is {}",context.get(RESPONSECOOKIE));
        } catch (Exception e) {
        	logger.error("PostParams Exception {}",e.getMessage());
        	retBol = false;
        } finally {
            if (httpPost != null) {
            	httpPost.abort();
            }
        }
        return retBol;
    }
    
    /**
     * httpClient post请求 发送文件
     * @param url 请求url
     * @param formparams 请求参数 form提交适用
     * @param headers 请求headers头信息
     * @param file 需要上传的文件
     * @param context 响应总线 里面包含响应所用信息
     * @return 200为成功 其他为失败
     *
     */
    public static boolean postFormParamsFiles(String  url, Map<String,Object> formparams, Map<String,String> headers, File file, Map<String,Object> context) {
    	List<File> files = new ArrayList<File>();
    	files.add(file);
    	return postFormParamsFiles(url, map2NVpair(formparams), headers, files, context);
    }
    
    /**
     * httpClient post请求 发送文件
     * @param url 请求url
     * @param formparams 请求参数 form提交适用
     * @param headers 请求headers头信息
     * @param files 需要上传的文件
     * @param context 响应总线 里面包含响应所用信息
     * @return 200为成功 其他为失败
     *
     */
    public static boolean postFormParamsFiles(String  url, List<NameValuePair> formparams, Map<String,String> headers, List<File> files, Map<String,Object> context) {
    	boolean retBol = false;
        CloseableHttpClient httpClient = null;
        HttpPost httpPost = null;
        try {
            httpClient = getHttpClient();
            httpPost = new HttpPost(url);
            HttpEntity reqEntity = null;
            MultipartEntityBuilder builder = null;	
            // 设置请求参数
            if(formparams!=null && !formparams.isEmpty()){
            	/*UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(formparams, Consts.UTF_8);
                httpPost.setEntity(urlEncodedFormEntity);*/
            	builder = MultipartEntityBuilder.create();
            	for(NameValuePair formparam: formparams){
            		builder.addPart(formparam.getName(), new StringBody(formparam.getValue(), ContentType.TEXT_PLAIN) );
            	}
            }
            //设置请求头
            if(!isMapEmpty(headers)){
            	Iterator<String> headerIterator = headers.keySet().iterator();
            	
            	while(headerIterator.hasNext()){
            		String headerKey = headerIterator.next();
            		httpPost.addHeader(headerKey, headers.get(headerKey));
            	}
            }
            //设置上传文件
            if(files !=null){
            	if(builder == null){
            		builder = MultipartEntityBuilder.create();
            	}
            	for(File file : files){
            		FileBody fileBody = new FileBody(file);
            		builder.addPart(file.getName(), fileBody);
            	}
            }
            
            if(builder != null){
            	reqEntity = builder.build();
            	httpPost.setEntity(reqEntity);
            }
            
            logger.info("execute post url {}",url);
            HttpResponse httpResponse = httpClient.execute(httpPost);
            
            //封装结果
            int statusCode = httpResponse.getStatusLine().getStatusCode();
            context.put(RESPONSESTATUS, String.valueOf(statusCode));
            
            String responseBodyStr = new String();
            
            if (statusCode == HttpStatus.SC_OK) {
                HttpEntity resEntity = httpResponse.getEntity();
                responseBodyStr = EntityUtils.toString(resEntity);
                retBol = true;
            } else {
            	responseBodyStr = readHttpResponse(httpResponse);
            	retBol = false;
            }
            
            context.put(RESPONSEBODY, responseBodyStr);
            
            context.put(RESPONSEHEADER,getHeader(httpResponse));
            context.put(RESPONSECOOKIE,cookieStore.getCookies());
            
            //logger response
            logger.info("response status is {}",context.get(RESPONSESTATUS));
            logger.info("response body is {}",context.get(RESPONSEBODY));
            logger.info("response header is {}",context.get(RESPONSEHEADER));
            logger.info("response cookie is {}",context.get(RESPONSECOOKIE));
        } catch (Exception e) {
        	logger.error("PostParams Exception {}",e.getMessage());
        	retBol = false;
        } finally {
            if (httpPost != null) {
            	httpPost.abort();
            }
        }
        return retBol;
    }
    
    /**
     * httpClient post请求
     * @param url 请求url
     * @param soapXml String型的参数
     * @param headers 请求headers头信息
     * @param context 响应总线 里面包含响应所用信息
     * @return 200为成功 其他为失败
     */
    public static boolean postStringParams(String  url, String soapXml, Map<String,String> headers, Map<String,Object> context) throws Exception {
    	boolean retBol = false;
        CloseableHttpClient httpClient = null;
        HttpPost httpPost = null;
        try {
            httpClient = getHttpClient();
            httpPost = new HttpPost(url);
            // 设置请求参数
            if(soapXml!=null ){
            	StringEntity entity = new StringEntity(soapXml,Consts.UTF_8); 
                httpPost.setEntity(entity);
            }
            //设置请求头
            if(!isMapEmpty(headers)){
            	Iterator<String> headerIterator = headers.keySet().iterator();
            	
            	while(headerIterator.hasNext()){
            		String headerKey = headerIterator.next();
            		httpPost.addHeader(headerKey, headers.get(headerKey));
            	}
            }
            
            logger.info("execute post url {}",url);
            HttpResponse httpResponse = httpClient.execute(httpPost);
            
            //封装结果
            int statusCode = httpResponse.getStatusLine().getStatusCode();
            context.put(RESPONSESTATUS, String.valueOf(statusCode));
            
            String responseBodyStr = new String();
            
            if (statusCode == HttpStatus.SC_OK) {
                HttpEntity resEntity = httpResponse.getEntity();
                responseBodyStr = EntityUtils.toString(resEntity);
                retBol = true;
            } else {
            	responseBodyStr = readHttpResponse(httpResponse);
            	retBol = false;
            }
            
            context.put(RESPONSEBODY, responseBodyStr);
            
            context.put(RESPONSEHEADER,getHeader(httpResponse));
            context.put(RESPONSECOOKIE,cookieStore.getCookies());
            
            //logger response
            logger.info("response status is {}",context.get(RESPONSESTATUS));
            logger.info("response body is {}",context.get(RESPONSEBODY));
            logger.info("response header is {}",context.get(RESPONSEHEADER));
            logger.info("response cookie is {}",context.get(RESPONSECOOKIE));
        } catch (Exception e) {
        	logger.error("PostParams Exception {}",e.getMessage());
        	retBol = false;
        } finally {
            if (httpPost != null) {
            	httpPost.abort();
            }
        }
        return retBol;
    }
    
    public static CloseableHttpClient getHttpClient() throws Exception {
        CloseableHttpClient httpClient = HttpClients.custom()
//                .setSSLSocketFactory(sslsf)
                .setConnectionManager(cm)
                .setDefaultRequestConfig(requestConfig)
                .setDefaultCookieStore(cookieStore)
//                .setConnectionManagerShared(true)
                .build();
        return httpClient;
    }
    /**
     * 拼接响应报文
     * @param httpResponse
     * @return
     * @throws ParseException
     * @throws IOException
     */
    public static String readHttpResponse(HttpResponse httpResponse)
            throws ParseException, IOException {
        StringBuilder builder = new StringBuilder();
        // 获取响应消息实体
        HttpEntity entity = httpResponse.getEntity();
        // 响应状态
        builder.append("status:" + httpResponse.getStatusLine());
        builder.append("headers:");
        HeaderIterator iterator = httpResponse.headerIterator();
        while (iterator.hasNext()) {
            builder.append("\t" + iterator.next());
        }
        // 判断响应实体是否为空
        if (entity != null) {
            String responseString = EntityUtils.toString(entity);
            builder.append("response length:" + responseString.length());
            builder.append("response content:" + responseString.replace("\r\n", ""));
        }
        return builder.toString();
    }
    
    /**
     * 从response 中 获取 header
     * @param httpResponse
     * @return
     */
    public static List<String> getHeader(HttpResponse httpResponse){
    	List<String> resutlList = new ArrayList<String>();
    	HeaderIterator iterator = httpResponse.headerIterator();
        while (iterator.hasNext()) {
        	resutlList.add(iterator.next().toString());
        }
    	return resutlList;
    }
    
    public static List<NameValuePair> map2NVpair(Map<String,Object> map){
    	List<NameValuePair> resultList = new ArrayList<NameValuePair>();
    	
    	Iterator<String> keyIte = map.keySet().iterator();
    	while(keyIte.hasNext()){
    		String key = keyIte.next();
    		resultList.add(new BasicNameValuePair(key, getStr(map.get(key))));
    	}
    	
    	return resultList;
    }
    
    /**
     * httpClient get请求
     * @param url 请求url
     * @param params 请求参数 form提交适用
     * @param context 响应总线 里面包含响应所用信息
     * @param enableRedirect 是否自动重定向
     * @return 200为成功 其他为失败
     *
     */
    public static boolean getParams(String  url, Map<String,Object> params, Map<String,Object> context, boolean enableRedirect) {
    	return getParams(url, params, null, context, enableRedirect);
    }
    
    /**
     * httpClient get请求
     * @param url 请求url
     * @param params 请求参数 form提交适用
     * @param context 响应总线 里面包含响应所用信息
     * @return 200为成功 其他为失败
     *
     */
    public static boolean getParams(String  url, Map<String,Object> params, Map<String,Object> context) {
    	return getParams(url, params, null, context, true);
    }
    
    /**
     * httpClient get请求
     * @param url 请求url
     * @param params 请求参数 form提交适用
     * @param headers 请求headers头信息
     * @param context 响应总线 里面包含响应所用信息
     * @param enableRedirect 是否自动重定向
     * @return 200为成功 其他为失败
     *
     */
    public static boolean getParams(String  url, Map<String,Object> params, 
    		Map<String,String> headers, Map<String,Object> context, boolean enableRedirect) {
    	
    	boolean retBol = false;
        CloseableHttpClient httpClient = null;
        HttpGet httpGet = null;
        try {
        	if(enableRedirect){
        		httpClient = getHttpClient();
        	}else{
        		httpClient = getHttpClient_noredirect();
        	}
            
        	
            // 设置请求参数
        	StringBuilder sbUrl = new StringBuilder(url);
        	if(!isMapEmpty(params)){
            	Iterator<String> keyIter = params.keySet().iterator();
            	while(keyIter.hasNext()){
            		String key = keyIter.next();
            		String value = getStr(params.get(key));
            		if(sbUrl.indexOf("?")>-1){
            			sbUrl.append("&");
            		}else{
            			sbUrl.append("?");
            		}
            		sbUrl.append(key)
            			.append(equalsConst)
            			.append(value);
            	}
            }
        	
        	httpGet = new HttpGet(sbUrl.toString());
        	
            //设置请求头
            if(!isMapEmpty(headers)){
            	Iterator<String> headerIterator = headers.keySet().iterator();
            	
            	while(headerIterator.hasNext()){
            		String headerKey = headerIterator.next();
            		httpGet.addHeader(headerKey, headers.get(headerKey));
            	}
            }
            
            logger.info("execute get url {}",sbUrl);
            HttpResponse httpResponse = httpClient.execute(httpGet);
            
            //封装结果
            int statusCode = httpResponse.getStatusLine().getStatusCode();
            context.put(RESPONSESTATUS, String.valueOf(statusCode));
            
            String responseBodyStr = new String();
            
            if (statusCode == HttpStatus.SC_OK) {
                HttpEntity resEntity = httpResponse.getEntity();
                responseBodyStr = EntityUtils.toString(resEntity);
                retBol = true;
            } else {
            	responseBodyStr = readHttpResponse(httpResponse);
            	retBol = false;
            }
            
            context.put(RESPONSEBODY, responseBodyStr);
            
            context.put(RESPONSEHEADER,getHeader(httpResponse));
            context.put(RESPONSECOOKIE,cookieStore.getCookies());
            
            //logger response
            logger.info("response status is {}",context.get(RESPONSESTATUS));
            logger.info("response body is {}",context.get(RESPONSEBODY));
            logger.info("response header is {}",context.get(RESPONSEHEADER));
            logger.info("response cookie is {}",context.get(RESPONSECOOKIE));
        } catch (Exception e) {
        	logger.error("GetParams Exception {}",e.getMessage());
        	retBol = false;
        } finally {
            if (httpGet != null) {
            	httpGet.abort();
            }
        }
        return retBol;
    }
    
    public static CloseableHttpClient getHttpClient_noredirect() throws Exception {
    	cookieStore = new BasicCookieStore();  
        CloseableHttpClient httpClient = HttpClients.custom()
                .setConnectionManager(cm)
                .setDefaultRequestConfig(createConfig(20000, false))
                .setDefaultCookieStore(cookieStore)
                .build();
        return httpClient;
    }
    
    public static RequestConfig createConfig(int timeout, boolean redirectsEnabled)
    {
        return RequestConfig.custom()
            .setSocketTimeout(timeout)
            .setConnectTimeout(timeout)
            .setConnectionRequestTimeout(timeout)
            .setRedirectsEnabled(redirectsEnabled)
            .build();
    }
    
    /**
	 * return if map is null
	 * @param map
	 * @return
	 */
	public static boolean isMapNull(Map<?,?> map){
		return map == null;
	}
	
	/**
	 * return map is null or empty
	 * @param map
	 * @return
	 */
	public static boolean isMapEmpty(Map<?,?> map){
		return isMapNull(map)||map.isEmpty();
	}
	
	/**
	 * Object to String
	 * @param obj
	 * @return
	 */
	public static String getStr(Object obj){
		String retStr = EMPTYSTR;
		
		if(obj == null){
			return retStr;
		}
		
		try{
			retStr = (String)obj;
		}catch(Exception e){
			logger.warn("cast the class {} ,Exception is {}",obj,e.getMessage());
			retStr = obj.toString();
		}
		
		
		return retStr;
		
	}
}
