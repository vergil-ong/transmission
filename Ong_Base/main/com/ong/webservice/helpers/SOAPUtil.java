package com.ong.webservice.helpers;

import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPBodyElement;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.ws.Dispatch;
import javax.xml.ws.Service;

import org.w3c.dom.Document;

import com.ong.bean.helpers.BeanHelper;
import com.ong.context.impl.SOAPResponseContext;
import com.ong.log.helpers.Log;
import com.ong.webservice.mode.SOAPConfig;

/**
 * SOAP请求工具类
 * @Description:  SOAP请求工具类
 * @Author:       Ong
 * @CreateDate:   2017-05-22 12:00:00
 * @E-mail:		  865208597@qq.com
 */
public class SOAPUtil {
	
	private static Log logger = Log.getLog(SOAPUtil.class);
	
	public static boolean postSOAP(SOAPConfig config, SOAPResponseContext<String> responseContext) throws Exception {
		boolean retBol = false;
		
		//1、创建服务(Service)  
		URL url = new URL(config.getWsdlUrl());  
        QName sname = new QName(config.getWholeTargetNameSpace(),config.getWholeName());  
        Service service = Service.create(url,sname); 
        
        //2、创建Dispatch  
        Dispatch<SOAPMessage> dispatch = service.createDispatch(new QName(config.getWholeTargetNameSpace(),config.getServicePortName()),SOAPMessage.class,Service.Mode.MESSAGE);
        
        //3、创建SOAPMessage  
        SOAPMessage msg = MessageFactory.newInstance(SOAPConstants.SOAP_1_1_PROTOCOL).createMessage();  
        SOAPEnvelope envelope = msg.getSOAPPart().getEnvelope();  
        SOAPBody body = envelope.getBody();  
        
        //4、创建QName来指定消息中传递数据  
        QName ename = new QName(config.getServiceTargetNameSpace(),config.getServiceMethod(),"ong");
        SOAPBodyElement ele = body.addBodyElement(ename);  
        // 传递参数
        Map<String, String> requestParams = config.getRequestParams();
        if(!BeanHelper.isMapEmpty(requestParams)){
        	Iterator<String> reqIter = requestParams.keySet().iterator();
        	
        	while(reqIter.hasNext()){
        		String reqKey = reqIter.next();
        		ele.addChildElement(reqKey).setValue(requestParams.get(reqKey));
        	}
        	
        }
        
      //5、通过Dispatch传递消息,会返回响应消息
        SOAPMessage response = dispatch.invoke(msg);
        
        Document doc = response.getSOAPPart().getEnvelope().getBody().extractContentAsDocument();
        TransformerFactory   tf   =   TransformerFactory.newInstance();
        Transformer t = tf.newTransformer();
        ByteArrayOutputStream   bos   =   new   ByteArrayOutputStream();
        t.transform(new DOMSource(doc), new StreamResult(bos));
        String xmlStr = bos.toString();
        
        logger.info("response SOAP is {0}",xmlStr);
        responseContext.setResponseBody(xmlStr);
      //6、响应消息处理,将响应的消息转换为dom对象  
        try{
        	String str = doc.getElementsByTagName(config.getSuccessSymbol()).item(0).getTextContent();
        	str = BeanHelper.getStr(str);
        	
        	if(str.equals(config.getSuccessAssert())){
        		retBol = true;
        	}else{
        		retBol = false;
        	}
            
        }catch(Exception e){
        	logger.error(e);
        	retBol = false;
        }
          
        
        
		return retBol;
	}
	
}
