package ong.transmission;

import java.io.IOException;

import org.beetl.core.Configuration;
import org.beetl.core.GroupTemplate;
import org.beetl.core.Template;
import org.beetl.core.resource.StringTemplateResourceLoader;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws Exception
    {
    	/*Map<String,Object> context = new HashMap<String,Object>();
    	
    	Map<String, Object> params = new HashMap<String, Object>();
    	params.put("a", "1");
    	params.put("b", "2");
    	if(HttpsUtils.getParams("http://127.0.0.1:8001/test", params, context)){
    		
    	}*/
    	
    	StringTemplateResourceLoader resourceLoader = new StringTemplateResourceLoader();
    	Configuration cfg = Configuration.defaultConfiguration();
    	GroupTemplate gt = new GroupTemplate(resourceLoader, cfg);
    	Template t = gt.getTemplate("hello,${name}");
    	t.binding("name", "beetl");
    	String str = t.render();
    	System.out.println(str);
       
    }
}
