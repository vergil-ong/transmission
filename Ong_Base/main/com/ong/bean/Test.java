package com.ong.bean;

import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

public class Test {
	
	static class TestBean{
		private String _id;
		private String name;
		/**
		 * @return the _id
		 */
		public String get_id() {
			return _id;
		}
		/**
		 * @param _id the _id to set
		 */
		public void set_id(String _id) {
			this._id = _id;
		}
		/**
		 * @return the name
		 */
		public String getName() {
			return name;
		}
		/**
		 * @param name the name to set
		 */
		public void setName(String name) {
			this.name = name;
		}
	}
	
	public static String generateJsonStr(){
		String str = "";
//		str = "{'_id':'qqqqqqqwwwwwww','name':'123123123123123'}";
		str = "[{'_id':'qqqqqqqwwwwwww','name':'123123123123123'},{'_id':'qqqqqqqwwwwwww','name':'123123123123123'}]";
		return str;
	}
	
	public static void main(String[] args) {
		String str = generateJsonStr();
		System.out.println("Original str is "+str);
		List<TestBean> testBean = JSON.parseArray(str,TestBean.class);
		for(TestBean tb : testBean)
			System.out.println(tb.get_id());
	}
}
