package test.com.ong.http.helpers;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.ong.http.helpers.HttpsUtils;
import com.ong.log.helpers.Log;

public class HttpsUtilsTest {
	
	private static Log logger = Log.getLog(HttpsUtilsTest.class);
	
	public static void test1(){
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("abc", "123");
		Map<String,Object> context = new HashMap<String,Object>();
		logger.info("post result {0}",HttpsUtils.PostFormParams("http://172.16.29.23/v2/user/login", map, context));
	}
	
	public static void test2(){
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("abc", "123");
		Map<String,Object> context = new HashMap<String,Object>();
		logger.info("get result {0}",HttpsUtils.GetParams("http://172.16.29.23/v2/user/login", map, context));
	}
	
	public static void test3(){
		String sessionId = "7sh3gesf3rctb54u9h2dp6hmp6";
		String JSESSIONID = "aaaWdykYICCWadgCTJ81v";
		
		
		
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("neid", "595");
		params.put("path_type", "self");
		params.put("_", "1500974041935");
		params.put("account_id", "1");
		params.put("uid", "27");
		params.put("file_name", "ceshi.txt");
		params.put("bytes", "7");
		
		Map<String,String> headers = new HashMap<String,String>();
		headers.put("Connection", "Keep-Alive");
		headers.put("Charset", "UTF-8");
		headers.put("FileName", "fileName");
//		headers.put("Content-Type", "multipart/form-data");
		headers.put("cookie", "X-LENOVO-SESS-ID="+sessionId+";JSESSIONID="+JSESSIONID);
		
		File file = new File("D:/eclipseworkspace/cheliantianxia3.9.0/custom/custom-interface/STATIC/file/ceshi.txt");
		
		Map<String,Object> context = new HashMap<String,Object>();
		logger.info("get result {0}",HttpsUtils.PostFormParamsFiles("http://172.16.29.32/v2/fileops/auth_upload_neid/databox/", params, headers, file, context));
	}

	public static void main(String[] args) {
		test3();
	}
}
