package com.ong.context;

/**
 * 响应总线接口
 * @Description:  响应总线接口<br/>
 * 其中泛型为responsebody的类型
 * @Author:       Ong
 * @CreateDate:   2017-05-22 13:40:00
 * @E-mail:		  865208597@qq.com
 */
public interface ResponseContext<T> {
	
	/**
	 * 响应是否成功
	 * @return
	 */
	public boolean isSuccess();
	
	/**
	 * 返回响应报文主体
	 * @param T
	 * @return
	 */
	public T getResponseBody();
	
	/**
	 * 设置响应报文主体
	 * @param responseBody
	 * @return
	 */
	public void setResponseBody(T responseBody);
	
	/**
	 * 返回响应报文头
	 * @param T
	 * @return
	 */
	public String getResponseHeader();
	
	/**
	 * 设置响应报文头
	 * @param responseHeader
	 * @return
	 */
	public void setResponseHeader(String responseHeader);
}
