package com.ong.stdata;

/**
 * 挡板数据接口
 * @Description:  挡板数据接口
 * @Author:       Ong
 * @CreateDate:   2017-06-27 19:00:00
 * @E-mail:		  865208597@qq.com
 */
public interface StaticData {
	
	/**
	 * 从文件中获取挡板数据
	 * @param file
	 * @return
	 */
	public Object getStaticData(String file);
	
	/**
	 * 从文件中获取挡板数据
	 * @param file
	 * @return
	 */
	public String getStrStaticData(String file);
	
}
