package com.ong.bean.helpers;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ong.log.helpers.Log;

/**
 * 对Bean类的帮助类
 * @Description:  Bean类的帮助类
 * @Author:       Ong
 * @CreateDate:   2017-05-20 12:00:00
 * @E-mail:		  865208597@qq.com
 */
public class BeanHelper {
	
	private static Log logger = Log.getLog(BeanHelper.class);
	
	private static final String STATICDIR = "D://eclipseworkspace/ong/Ong_Base/STATIC";
	
	private static Class<?> defaultMapClazz = HashMap.class;
	
	private static final String EMPTYSTR = "";
	
	private static final String[] JSONDIR_SUFFIX = {"json","data"};
	
	private static final String[] XML_SUFFIX = {"xml"};

	/**
	 * return if List is null
	 * @param map
	 * @return
	 */
	public static boolean isListNull(List<?> list){
		return list == null;
	}
	
	/**
	 * return List is null or empty
	 * @param map
	 * @return
	 */
	public static boolean isListEmpty(List<?> list){
		return isListNull(list)||list.isEmpty();
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
	 * get Instance of Map if it`s null
	 * @param <P>
	 * @param <T>
	 * @param map
	 * @return
	 */
	public static <T, P> Map<T,P> getInstanceIfNull(Map<T, P> map){
		
		return getInstanceIfNull(map, defaultMapClazz);
	}
	
	/**
	 * get Instance of Map if it`s null
	 * @param <T>
	 * @param <P>
	 * @param map
	 * @param clazz
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T, P> Map<T,P> getInstanceIfNull(Map<T, P> map, Class<?> clazz){
		Map<T,P> resultMap = null;
		/**
		 * 1. check if map is null
		 * 	1.1 map is not null 
		 * 		return map
		 * 	1.2 map is null
		 * 		return new Instance Class
		 */
		
		//1. check if map is null
		if(!isMapNull(map)){
			//1.1 map is not null  return map
			return map;
		}
		
		//return new Instance Class
		try {
			resultMap = (Map<T, P>) clazz.newInstance();
		} catch (Exception e1) {
			logger.error("newInstance the class {0} ,Exception is {1}",clazz,e1.getMessage());
			try {
				resultMap = (Map<T, P>) defaultMapClazz.newInstance();
			} catch (Exception e2) {
				logger.error("newInstance the defaultMapClazz {0} ,Exception is {1}",defaultMapClazz,e2.getMessage());
				resultMap = new HashMap<T, P>();
			}
		} 
		
		return resultMap;
		
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
			logger.warn("cast the class {0} ,Exception is {1}",obj,e.getMessage());
			retStr = obj.toString();
		}
		
		
		return retStr;
		
	}
	
	/**
	 * get Map from bean
	 * @param obj
	 * @return
	 */
	public static Map<String,Object> bean2Map(Object obj){
		Map<String, Object> map = new HashMap<String, Object>();
		
		Method[] declaredMethods = obj.getClass().getDeclaredMethods();
		
		for(Method decMethod : declaredMethods){
			Class<?>[] parameterTypes = decMethod.getParameterTypes();
			String methodName = decMethod.getName();
			if(!methodName.startsWith("get")||parameterTypes.length!=0){
				//filter the method is not start with get
				//or parameters is not null
				continue;
			}
			
			try {
				Object[] params = {};
				Object getValue = decMethod.invoke(obj, params);
				String subName = methodName.substring(3);
				String firstChar = subName.substring(0, 1);
				subName = subName.replaceFirst(firstChar, firstChar.toLowerCase());
				
				map.put(subName, getValue);
				
			} catch (IllegalAccessException e) {
				logger.error(e);
			} catch (IllegalArgumentException e) {
				logger.error(e);
			} catch (InvocationTargetException e) {
				logger.error(e);
			}
			
		}
		
		return map;
	}
	
	/**
	 * set map to bean<br/>
	 * 暂时只支持bean类的无参构造函数，如不存在无参构造，则需手动添加<br/>
	 * 初步构想使用注解来指定构造函数
	 * @param <T>
	 * @param map
	 * @param clazz
	 * @return
	 */
	public static <T> T map2Bean(Map<String, Object> map, Class<T> clazz){
		
		if(isMapEmpty(map)){
			return null;
		}
		
		try {
			T bean = clazz.newInstance();
			Method[] declaredMethods = clazz.getDeclaredMethods();
			for(Method decMethod : declaredMethods){
				String methodName = decMethod.getName();
				Class<?>[] parameterTypes = decMethod.getParameterTypes();
				
				if(!methodName.startsWith("set") || parameterTypes.length != 1){
					continue;
				}
				
				String subName = methodName.substring(3, methodName.length());
				String firstChar = subName.substring(0, 1);
				subName = subName.replaceFirst(firstChar, firstChar.toLowerCase());
				Object setValue = map.get(subName);
				decMethod.invoke(bean, transferExpectClass(setValue, parameterTypes[0]));
			}
			
			return bean;
		} catch (InstantiationException e) {
			logger.error(e);
		} catch (IllegalAccessException e) {
			logger.error(e);
		} catch (IllegalArgumentException e) {
			logger.error(e);
		} catch (InvocationTargetException e) {
			logger.error(e);
		}		
		
		return null;
	}
	
	/**
	 * 将对象转换成期望类型
	 * @param obj
	 * @param expectClazz
	 * @return
	 */
	public static <T> Object transferExpectClass(Object obj, Class<T> expectClazz){
		
		if(obj.getClass().equals(expectClazz)){
			return obj;
		}else if(expectClazz.isAssignableFrom(java.lang.String.class)){
			return getStr(obj);
		}else {
			return invokeValueOf(getStr(obj), expectClazz);
		}
	}
	
	/**
	 * 执行valueOf 方法
	 * @param obj
	 * @param clazz
	 * @return
	 */
	public static <T> Object invokeValueOf(Object obj, Class<T> clazz){
		try {
			Method declaredMethod = clazz.getDeclaredMethod("valueOf", java.lang.String.class);
			return declaredMethod.invoke(null, obj);
		} catch (NoSuchMethodException e) {
			logger.error(e);
		} catch (SecurityException e) {
			logger.error(e);
		} catch (IllegalAccessException e) {
			logger.error(e);
		} catch (IllegalArgumentException e) {
			logger.error(e);
		} catch (InvocationTargetException e) {
			logger.error(e);
		}
		
		return obj;
	}
	 
	/**
	 * 将BeanA 转换成 BeanB
	 * @param a
	 * @param b
	 * @return
	 */
	public static <T, P> P transBeanAToBeanB(T a, P b){
		if(a == null){
			return null;
		}
		
		if(b == null){
			return null;
		}
		
		Class<? extends Object> clazzA = a.getClass();
		Class<? extends Object> classB = b.getClass();
		Method[] declaredMethods = clazzA.getDeclaredMethods();
		
		for(Method decMethod : declaredMethods){
			String methodName = decMethod.getName();
			Class<?>[] parameterTypes = decMethod.getParameterTypes();
			
			if(!methodName.startsWith("get") || parameterTypes.length != 0){
				continue;
			}
			
			Object[] params = {};
			try {
				Object getValue = decMethod.invoke(a, params);
				
				String setMethodName = methodName.replaceFirst("get", "set");
				String fieldName = lowerFirstCase(methodName.replaceFirst("get", ""));
				
				Field declaredField = classB.getDeclaredField(fieldName);
				Class<?> type = declaredField.getType();
				
				Method setMethod = classB.getDeclaredMethod(setMethodName, type);
				
				setMethod.invoke(b, getValue);
				
			} catch (IllegalAccessException e) {
				logger.error(e);
			} catch (IllegalArgumentException e) {
				logger.error(e);
			} catch (InvocationTargetException e) {
				logger.error(e);
			} catch (NoSuchFieldException e) {
				logger.info(e);
			} catch (SecurityException e) {
				logger.error(e);
			} catch (NoSuchMethodException e) {
				logger.info(e);
			}
			
		}
		
		return b;
	}
	
	/**
	 * 将BeanA 转换成 BeanB
	 * @param a
	 * @param b
	 * @return
	 */
	public static <T, P> P transBeanAToBeanB(T a, Class<P> clazzB){
		
		try {
			P b = clazzB.newInstance();
			return transBeanAToBeanB(a, b);
		} catch (InstantiationException | IllegalAccessException e) {
			logger.error(e);
		}
		return null;
	}
	
	public static <T, P> List<P> transAListToBeanB(List<T> a, Class<P> clazzB){
		if(a == null){
			return null;
		}
		
		List<P> resultList = new ArrayList<P>(a.size());
		
		for(T t : a){
			resultList.add(transBeanAToBeanB(t, clazzB));
		}
		
		return resultList;
	}
	
	/**
	 * 首字母大写
	 * @param str
	 * @return
	 */
	public static String upperFirstCase(String str) {  
	    char[] ch = str.toCharArray();  
	    if (ch[0] >= 'a' && ch[0] <= 'z') {  
	        ch[0] = (char) (ch[0] - 32);  
	    }  
	    return new String(ch);  
	}
	
	/**
	 * 首字母小写
	 * @param str
	 * @return
	 */
	public static String lowerFirstCase(String str) {  
	    char[] ch = str.toCharArray();  
	    if (ch[0] >= 'A' && ch[0] <= 'Z') {  
	        ch[0] = (char) (ch[0] + 32);  
	    }  
	    return new String(ch);  
	}  
	
	public static String getStaticFile(String fileName){
		if(fileName == null){
			return null;
		}
		for(String jsonSuffix : JSONDIR_SUFFIX){
			if(fileName.toLowerCase().endsWith(jsonSuffix)){
				return STATICDIR+File.separator+"JSON"+File.separator+fileName;
			}
		}
		for(String xmlSuffix : XML_SUFFIX){
			if(fileName.toLowerCase().endsWith(xmlSuffix)){
				return STATICDIR+File.separator+"XML"+File.separator+fileName;
			}
		}
		return STATICDIR+File.separator+fileName;
	}
	
	/**
	 * 把对象转成对象数组
	 * @param obj
	 * @return
	 */
	public static Object[] transObj2ObjArray(Object obj){
		if(obj == null){
			return null;
		}
		
		if(obj instanceof Object[]){
			return (Object[])obj;
		}
		
		if(obj instanceof List){
			List<?> list = (List<?>)obj;
			return list.toArray();
		}
		
		return new Object[]{obj};
	}
	
	/**
	 * 获取对象数组的类型
	 * @param obj
	 * @return
	 */
	public static Class<?>[] getObjClazz(Object[] objs){
		if(objs == null){
			return null;
		}
		
		Class<?>[] clazzArray = new Class<?>[objs.length];
		for(int i=0; i<clazzArray.length; i++){
			clazzArray[i] = objs[i].getClass();
		}
		
		return clazzArray;
	}
}
