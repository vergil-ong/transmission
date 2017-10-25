package com.ong.stdata;

import com.ong.stdata.impl.JSONStaticData;
import com.ong.stdata.impl.XMLStaticData;

public class StaticDataTest {
	
	public static void test1(){
		JSONStaticData sd = new JSONStaticData();
		System.out.println(sd.getStrStaticData("user.json"));
		System.out.println(sd.getStaticData("user.json"));
	}
	
	public static void test2(){
		XMLStaticData sd = new XMLStaticData();
		System.out.println(sd.getStrStaticData("applicationContext.xml"));
		System.out.println(sd.getStaticData("applicationContext.xml"));
	}

	public static void main(String[] args) {
//		test1();
		test2();
	}

}
