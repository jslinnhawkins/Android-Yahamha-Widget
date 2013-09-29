package bsharps.yamahawidget.util;

import java.io.StringBufferInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.util.Log;

public class XmlResponseDecoder {

	private static String TAG = "XmlResponseDecoder";
	
	public static String find(Element item, String []path, int depth) throws Exception
	{
		String retVal= null;
		
		int found = 0;
		
		NodeList children = item.getElementsByTagName(path[depth]);
			
		for (int j=0;j<children.getLength();j++){			            
        
			Node n = children.item(j);
			if (n instanceof Element)
			{
				Element element = (Element)n;
                String name= element.getTagName();
                
                if (name.compareTo(path[depth]) == 0)
                {
                	if (depth >= (path.length - 1))
	        		{
                		NodeList values = element.getChildNodes();
                		
                		if (name.compareTo(path[depth]) == 0 && values.getLength() > 0)
                		{
                			retVal = values.item(0).getNodeValue();
                			found++;
                		}                			
	        		}
                	else
                	{
                		retVal = find(element, path, depth+1)   ;
                		if (retVal != null)
                			found++;
                	}
                }
			}
		}
		
		if (found > 1)
		{
			Log.e(TAG, "found multiple!");
			throw new Exception("found multiple!");
		}
			
		
		return retVal;
	}
	
	// Response looks like this so how do you want to extract the info?
	//<YAMAHA_AV rsp="GET" RC="0">
	//  <System>
	//	    <Network>
	//	      <Setting>
	//	        <Network_Standby>No</Network_Standby>
	//	        <DHCP>On</DHCP>
	//	        <IP_Address>192.168.0.6</IP_Address>
	//	        <Subnet_Mask>255.255.255.0</Subnet_Mask>
	//	        <Default_Gateway>192.168.0.200</Default_Gateway>
	//	        <DNS_Server_1>192.168.0.200</DNS_Server_1>
	//	        <DNS_Server_2>0.0.0.0</DNS_Server_2>
	//	      </Setting>
	//	    </Network>
		//  </System>
		//</YAMAHA_AV>
	public static String DecodeStringResponse(String response, String key) throws RuntimeException
	{
		String[] path = key.split("\\.");
		String returnValue = null;
		
		String tag = path[path.length - 1];
		
		String[] splits = response.split("<" + tag + ">");
		
		if (splits.length == 2)
		{
			splits = splits[1].split("</" + tag + ">");
			if (splits.length == 2)
			{
				returnValue = splits[0];
				Log.d(TAG, "Decoded fast: " + returnValue);
			}
		}
		
		if (returnValue == null)
		{
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	        
	        try {
	            DocumentBuilder builder = factory.newDocumentBuilder();
	            Document dom = builder.parse(new StringBufferInputStream(response));
	            
	            Element root = dom.getDocumentElement();
	            returnValue = find(root, path, 1);
	            
	            if (returnValue == null)
	            {
	            	Log.e(TAG,"didnt find " + key + " in " + response );
	            	throw new Exception("didnt find " + key + " in " + response);
	            }
	            	
	            
	        } catch (Exception e) {
	            throw new RuntimeException(e);
	        } 
	        Log.d(TAG, "Decoded slowly: " + returnValue);
		}
        
        return returnValue;
			
	}
}
