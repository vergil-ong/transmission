package ong.transmission;

import java.util.HashMap;
import java.util.Map;

import com.ong.http.helpers.HttpsUtils;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
    	Map<String,Object> context = new HashMap<String,Object>();
    	
    	Map<String, Object> params = new HashMap<String, Object>();
    	params.put("a", "1");
    	params.put("b", "2");
    	if(HttpsUtils.getParams("http://127.0.0.1:8001/test", params, context)){
    		
    	}
       
    }
}
