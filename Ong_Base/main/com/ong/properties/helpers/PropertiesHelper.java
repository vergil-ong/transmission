package com.ong.properties.helpers;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class PropertiesHelper {
//	public static String adminPro = "/opt/srv/resin/conf/box.properties";
	public static String adminPro = "D:/eclipseworkspace/ong/Ong_Base/resource/box.properties";
	
	public static Map<String, String> properties=new HashMap<String, String>();
	
	static{
		Properties pps = new Properties();
		try {
			pps.load(new FileInputStream(adminPro));
		} catch (IOException e) {
			e.printStackTrace();
		}
		Enumeration<?> enum1 = pps.propertyNames();
		while(enum1.hasMoreElements()) {
			String strKey = (String) enum1.nextElement();
			String strValue = pps.getProperty(strKey);
			properties.put(strKey, strValue);
		}
	}
	
	public static String getPro(String key){
		return getPro(key, "");
	}
	
	public static String getPro(String key, String defaultValue){
		String value = properties.get(key);
		
		value = value==null?defaultValue:value.trim();
		
		return value;
	}
}
