package com.ong.stdata.impl;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;

import com.ong.bean.helpers.BeanHelper;
import com.ong.file.ReadFile;
import com.ong.log.helpers.Log;
import com.ong.stdata.StaticData;

/**
 * 读取挡板数据的XML实现
 * @Description:  读取挡板数据的XML实现
 * @Author:       Ong
 * @CreateDate:   2017-06-27 19:00:00
 * @E-mail:		  865208597@qq.com
 */
public class XMLStaticData implements StaticData {
	
	private static Log logger = Log.getLog(XMLStaticData.class);
	
	/**
	 * 从文件中获取挡板数据
	 */
	public Document getStaticData(String file) {
		String strStaticData = getStrStaticData(file);
		try {
			Document parseText = DocumentHelper.parseText(strStaticData);
			return parseText;
		} catch (DocumentException e) {
			e.printStackTrace();
			logger.error("Exception {0}",e);
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see com.ong.stdata.StaticData#getStrStaticData(java.lang.String)
	 */
	public String getStrStaticData(String file) {
		return ReadFile.readFileByMultipleChars(BeanHelper.getStaticFile(file));
	}

}
