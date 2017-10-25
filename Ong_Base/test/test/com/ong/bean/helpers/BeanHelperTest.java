package test.com.ong.bean.helpers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ong.bean.User;
import com.ong.bean.helpers.BeanHelper;

public class BeanHelperTest {
	
	public static void test1(){
		Map<String,Object> map = null;
		System.out.println(BeanHelper.isMapNull(map));
		System.out.println(BeanHelper.isMapEmpty(map));
		
		map = new HashMap<String,Object>();
		System.out.println(BeanHelper.isMapNull(map));
		System.out.println(BeanHelper.isMapEmpty(map));
		
		map.put("1", "2");
		System.out.println(BeanHelper.isMapNull(map));
		System.out.println(BeanHelper.isMapEmpty(map));
	}
	
	public static void test2(){
		Map<String,Object> map = null;
		System.out.println(BeanHelper.isMapNull(map));
		System.out.println(BeanHelper.isMapEmpty(map));
		
		Map<String, Object> map2 = BeanHelper.getInstanceIfNull(map,java.util.ArrayList.class);
		System.out.println(BeanHelper.isMapNull(map2));
		System.out.println(BeanHelper.isMapEmpty(map2));
		
		System.out.println(map2.getClass());
	}
	
	public static void test3(){
		User user = new User();
		user.setAddress("lalalal");
		user.setAge(1);
		user.setId(123123l);
		user.setName("name");
		Map<String, Object> map = BeanHelper.bean2Map(user);
		System.out.println(map);
	}
	
	public static void test4(){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("address", 1234);
		map.put("age", "123");
		map.put("id", 123123l);
		map.put("name", "Tom");
		User user = BeanHelper.map2Bean(map, User.class);
		System.out.println(user);
	}
	
	public static void test5(){
		A a = new A();
		a.setA("a");
		a.setB(1);
		
		B b = new B();
		b.setA("b");
		b.setB(2);
		
		System.out.println(a);
		System.out.println(b);
		
//			transBeanAToBeanB(a, b);
		B b2 = BeanHelper.transBeanAToBeanB(a, B.class);
		
		System.out.println(a);
		System.out.println(b2);
	}
	
	public static void test6(){
		A a = new A();
		a.setA("a");
		a.setB(1);
		
		List<A> alist = new ArrayList<A>();
		alist.add(a);
		
		a = new A();
		a.setA("b");
		a.setB(2);
		alist.add(a);
		
		a = new A();
		a.setA("c");
		a.setB(3);
		alist.add(a);
//			transBeanAToBeanB(a, b);
		List<B> transBeanAToBeanB = BeanHelper.transAListToBeanB(alist, B.class);
		
		System.out.println(transBeanAToBeanB);
	}
	
	public static void test7(){
		
		System.out.println(BeanHelper.getStaticFile("user.json"));
	}

	public static void main(String[] args) {
		test7();
	}

}
