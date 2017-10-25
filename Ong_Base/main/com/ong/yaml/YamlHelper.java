package com.ong.yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;

import com.ong.log.helpers.Log;
import com.ong.yaml.model.Person;

/**
 * 对YAML文件的帮助类
 * @Description:  YAML文件的帮助类
 * @Author:       Ong
 * @CreateDate:   2017-10-09 10:00:00
 * @E-mail:		  865208597@qq.com
 */
public class YamlHelper {
	
	private static Log logger = Log.getLog(YamlHelper.class);
	
	private static String resource = "person.yaml";
	
	private static File resourceFile;
	
	public static File readSource(){
		return readSource(resource);
	}
	
	public static File readSource(String resource){
		
		String path = YamlHelper.class.getClassLoader().getResource(resource).getPath();
		
		logger.info("path is {0}",path);
		
		resourceFile = new File(path);
		if(!resourceFile.exists()){
			//文件不存在
			logger.error("文件不存在,{0}",resource);
			return null;
		}
		
		return resourceFile;
	}
	
	public static <T> T readFile(File file, Class<T> clazz){
		T t = null;
		Yaml yaml = new Yaml();
		try {
			t = yaml.loadAs(new FileInputStream(file), clazz);
		} catch (FileNotFoundException e) {
			logger.error("readFile Error {0}",e);
		}
		
		return t;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String,Object> readFile(File file){
		Map<String, Object> map = new HashMap<String,Object>();
		Yaml yaml = new Yaml();
		try {
			map = (Map<String, Object>) yaml.load(new FileInputStream(file));
		} catch (FileNotFoundException e) {
			logger.error("readFile Error {0}",e);
		}
		
		return map;
	}
	
	public static <T> String exportStr(T t){
		Yaml yaml = new Yaml();
		String dumpStr = yaml.dump(t);
		return dumpStr;
	}
	
	public static void main(String[] args) {
		Person person = readFile(readSource(), Person.class);
		logger.info("person is {0}",person);
		Map<String, Object> map = readFile(readSource());
		logger.info("map is {0}",map);
		person = new Person();
		person.setAge(1);
		person.setName("name");
		person.setSex("66");
		
		logger.info("exportStr is {0}",exportStr(person));
		
	}
}
