package com.ong.context.impl;

import com.ong.context.ResponseContext;

/**
 * SOAP协议的响应总线
 * @Description:  SOAP协议的响应总线<br/>
 * @Author:       Ong
 * @CreateDate:   2017-05-20 12:00:00
 * @E-mail:		  865208597@qq.com
 */
public class SOAPResponseContext<T> implements ResponseContext<T> {
	
	/**
	 * 响应是否成功
	 */
	private boolean success;
	
	/**
	 * 响应体
	 */
	private T responseBody;
	
	/**
	 * 响应头
	 */
	private String responseHeader;
	
	public SOAPResponseContext(boolean success){
		this.success = success;
	}
	
	public SOAPResponseContext(boolean success, T responseBody){
		this.success = success;
		this.responseBody = responseBody;
	}

	public boolean isSuccess() {
		return success;
	}

	public T getResponseBody() {
		return responseBody;
	}

	public void setResponseBody(T responseBody) {
		this.responseBody = responseBody;
	}

	public String getResponseHeader() {
		return responseHeader;
	}

	public void setResponseHeader(String responseHeader) {
		this.responseHeader = responseHeader;
	}

}
