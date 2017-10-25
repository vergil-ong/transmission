package com.ong.webservice.mode;

import java.util.Map;

/**
 * SOAP请求的配置文件
 * @Description:  SOAP请求的配置文件
 * @Author:       Ong
 * @CreateDate:   2017-05-22 12:00:00
 * @E-mail:		  865208597@qq.com
 */
public class SOAPConfig {
	
	/**
	 * 响应是否成功，内容是个boolean
	 */
	public static final String RESPSUCCESS = "respSuccess";
	
	/**
	 * 响应主体
	 */
	public static final String RESPBODY = "respBody";
	
	/**
	 * 访问wsdl的URL
	 * @Example		http://127.0.0.1:9000/importUsers/?wsdl
	 */
	private String wsdlUrl;
	
	/**
	 * 整个WebService的 targetNameSpace
	 */
	private String wholeTargetNameSpace;
	
	/**
	 * 整个WebService的 名称
	 */
	private String wholeName;
	
	/**
	 * WebService 发布的服务名称
	 */
	private String servicePortName;
	
	/**
	 *	调用方法名 
	 */
	private String serviceMethod;
	
	/**
	 * 调用方法用到的命名空间
	 */
	private String serviceTargetNameSpace;
	
	
	/**
	 * 请求参数
	 */
	private Map<String,String> requestParams;
	
	/**
	 * 成功的标志
	 */
	private String successSymbol;
	
	/**
	 * 成功断言
	 */
	private String successAssert;
	
	public String getWsdlUrl() {
		return wsdlUrl;
	}

	public SOAPConfig setWsdlUrl(String wsdlUrl) {
		this.wsdlUrl = wsdlUrl;
		return this;
	}

	public String getServicePortName() {
		return servicePortName;
	}

	public SOAPConfig setServicePortName(String servicePortName) {
		this.servicePortName = servicePortName;
		return this;
	}

	public String getServiceMethod() {
		return serviceMethod;
	}

	public SOAPConfig setServiceMethod(String serviceMethod) {
		this.serviceMethod = serviceMethod;
		return this;
	}

	public String getServiceTargetNameSpace() {
		return serviceTargetNameSpace;
	}

	public SOAPConfig setServiceTargetNameSpace(String serviceTargetNameSpace) {
		this.serviceTargetNameSpace = serviceTargetNameSpace;
		return this;
	}

	public Map<String, String> getRequestParams() {
		return requestParams;
	}

	public SOAPConfig setRequestParams(Map<String, String> requestParams) {
		this.requestParams = requestParams;
		return this;
	}

	public String getSuccessSymbol() {
		return successSymbol;
	}

	public SOAPConfig setSuccessSymbol(String successSymbol) {
		this.successSymbol = successSymbol;
		return this;
	}

	public String getSuccessAssert() {
		return successAssert;
	}

	public SOAPConfig setSuccessAssert(String successAssert) {
		this.successAssert = successAssert;
		return this;
	}

	public String getWholeTargetNameSpace() {
		return wholeTargetNameSpace;
	}

	public SOAPConfig setWholeTargetNameSpace(String wholeTargetNameSpace) {
		this.wholeTargetNameSpace = wholeTargetNameSpace;
		return this;
	}

	public String getWholeName() {
		return wholeName;
	}

	public SOAPConfig setWholeName(String wholeName) {
		this.wholeName = wholeName;
		return this;
	}
	
	
}
