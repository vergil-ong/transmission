package com.ong.log.helpers;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.logging.LogFactory;

/**
 * 日志封装类
 * @Description:  日志封装类<br/>
 * 基于apache的common-logging 包 <br/>
 * 对日志录入消息添加模板功能
 * @Author:       Ong
 * @CreateDate:   2017-05-22 12:00:00
 * @E-mail:		  865208597@qq.com
 */
public class Log {
	
	private org.apache.commons.logging.Log logger;
	
	public static Log getLog(Class<?> clazz){
		return new Log(LogFactory.getLog(clazz));
	}
	
	public Log(org.apache.commons.logging.Log logger){
		this.logger = logger;
	}
	
	public org.apache.commons.logging.Log getInherited(){
		return logger;
	}
	
	public void info(Object object){
		StringBuilder resStr = new StringBuilder();
		if(filter1(object, resStr)){
			object = resStr;
		}
		logger.info(object);
	}
	
	public void info(String format, Object... objects){
		List<Object> objList = Arrays.asList(objects);
		for(int i=0;i<objList.size();i++){
			Object object = objList.get(i);
			StringBuilder resStr = new StringBuilder();
			if(filter1(object, resStr)){
				object = resStr;
			}
			objList.set(i, object);
		}
		logger.info(MessageFormat.format(format, objects));
	}
	
	public void debug(Object object){
		StringBuilder resStr = new StringBuilder();
		if(filter1(object, resStr)){
			object = resStr;
		}
		
		logger.debug(object);
	}
	
	public void debug(String format, Object... objects){
		List<Object> objList = Arrays.asList(objects);
		for(int i=0;i<objList.size();i++){
			Object object = objList.get(i);
			StringBuilder resStr = new StringBuilder();
			if(filter1(object, resStr)){
				object = resStr;
			}
			objList.set(i, object);
		}
		
		logger.debug(MessageFormat.format(format, objects));
	}
	
	public void trace(Object object){
		StringBuilder resStr = new StringBuilder();
		if(filter1(object, resStr)){
			object = resStr;
		}
		
		logger.trace(object);
	}
	
	public void trace(String format, Object... objects){
		List<Object> objList = Arrays.asList(objects);
		for(int i=0;i<objList.size();i++){
			Object object = objList.get(i);
			StringBuilder resStr = new StringBuilder();
			if(filter1(object, resStr)){
				object = resStr;
			}
			objList.set(i, object);
		}
		
		logger.trace(MessageFormat.format(format, objects));
	}
	
	public void warn(Object object){
		StringBuilder resStr = new StringBuilder();
		if(filter1(object, resStr)){
			object = resStr;
		}
		
		logger.warn(object);
	}
	
	public void warn(String format, Object... objects){
		List<Object> objList = Arrays.asList(objects);
		for(int i=0;i<objList.size();i++){
			Object object = objList.get(i);
			StringBuilder resStr = new StringBuilder();
			if(filter1(object, resStr)){
				object = resStr;
			}
			objList.set(i, object);
		}
		
		logger.warn(MessageFormat.format(format, objects));
	}
	
	public void error(Object object){
		StringBuilder resStr = new StringBuilder();
		if(filter1(object, resStr)){
			object = resStr;
		}
		
		logger.error(object);
	}
	
	public void error(String format, Object... objects){
		List<Object> objList = Arrays.asList(objects);
		for(int i=0;i<objList.size();i++){
			Object object = objList.get(i);
			StringBuilder resStr = new StringBuilder();
			if(filter1(object, resStr)){
				object = resStr;
			}
			objList.set(i, object);
		}
		
		logger.error(MessageFormat.format(format, objects));
	}
	
	public void fatal(Object object){
		StringBuilder resStr = new StringBuilder();
		if(filter1(object, resStr)){
			object = resStr;
		}
		
		logger.fatal(object);
	}
	
	public void fatal(String format, Object... objects){
		List<Object> objList = Arrays.asList(objects);
		for(int i=0;i<objList.size();i++){
			Object object = objList.get(i);
			StringBuilder resStr = new StringBuilder();
			if(filter1(object, resStr)){
				object = resStr;
			}
			objList.set(i, object);
		}
		
		logger.fatal(MessageFormat.format(format, objects));
	}
	
	
	/**
	 * 如果里面有List则遍历List
	 * @param object
	 * @return
	 */
	private boolean filter1(Object object, StringBuilder resStr){
		if(object == null){
			return false;
		}
		
		if(java.util.List.class.isAssignableFrom(object.getClass())){
			//判断继承自List或者是List的实现类
			List<?> list = (List<?>)object;
			resStr.delete(0, resStr.length());
			for(Object subObj : list){
				resStr.append(subObj.toString());
			}
			return true;
		}
		
		return false;
	}
}
