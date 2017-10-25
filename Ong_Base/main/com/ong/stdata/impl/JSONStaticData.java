package com.ong.stdata.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ong.bean.helpers.BeanHelper;
import com.ong.file.ReadFile;
import com.ong.stdata.StaticData;

/**
 * 读取挡板数据的JSON实现
 * @Description:  读取挡板数据的JSON实现
 * @Author:       Ong
 * @CreateDate:   2017-06-27 19:00:00
 * @E-mail:		  865208597@qq.com
 */
public class JSONStaticData implements StaticData{

	/**
	 * 从文件中获取挡板数据
	 * 如果文件是个JSONArray 则文件显示为{"result":"xxx"}
	 */
	public JSONObject getStaticData(String file) {
		String staticStr = getStrStaticData(file);
		if(staticStr.startsWith("[")&&staticStr.endsWith("]")){
			JSONArray jsonArray = JSON.parseArray(staticStr);
			JSONObject obj = new JSONObject();
			obj.put("result", jsonArray);
			return obj;
		}
		return JSON.parseObject(staticStr);
	}

	/* (non-Javadoc)
	 * @see com.ong.stdata.StaticData#getStrStaticData(java.lang.String)
	 */
	public String getStrStaticData(String file) {
		return ReadFile.readFileByMultipleChars(BeanHelper.getStaticFile(file));
	}

}
