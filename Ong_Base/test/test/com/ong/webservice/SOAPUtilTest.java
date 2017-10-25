package test.com.ong.webservice;

import java.util.HashMap;
import java.util.Map;

import com.ong.context.impl.SOAPResponseContext;
import com.ong.webservice.helpers.SOAPUtil;
import com.ong.webservice.mode.SOAPConfig;

public class SOAPUtilTest {

	public static void main(String[] args) {
		SOAPConfig config = new SOAPConfig();
		Map<String,String> reqParams = new HashMap<String,String>();
		reqParams.put("name", "中文");
		config.setWholeTargetNameSpace("http://impl.service.webservice.css.lenovo.com/")
			.setWsdlUrl("http://127.0.0.1:9000/importUsers/?wsdl")
			.setWholeName("ImportUsersImplService")
			.setServicePortName("ImportUsersImplPort")
			.setServiceTargetNameSpace("http://127.0.0.1/")
			.setServiceMethod("sayHello")
			.setRequestParams(reqParams)
			.setSuccessSymbol("userName")
			.setSuccessAssert("中文");
		
		SOAPResponseContext<String> responseContext = new SOAPResponseContext<String>(false);
		
		
		try {
			System.out.println(SOAPUtil.postSOAP(config, responseContext));
			System.out.println(responseContext.getResponseBody());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

}
