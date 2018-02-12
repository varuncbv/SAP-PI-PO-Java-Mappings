package iris_call_rfc_removeduplicatefiles;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;




import com.sap.aii.mapping.api.AbstractTransformation;
import com.sap.aii.mapping.api.DynamicConfiguration;
import com.sap.aii.mapping.api.DynamicConfigurationKey;
import com.sap.aii.mapping.api.StreamTransformationException;
import com.sap.aii.mapping.api.TransformationInput;
import com.sap.aii.mapping.api.TransformationOutput;
import com.sap.aii.mapping.lookup.LookupService;

public class RemoveDuplicateFiles_Through_RFC_Call_JavaMap extends AbstractTransformation {

	public StringBuffer endResult = new StringBuffer();
	public String isSales="";
	public String msgType="";
	public String inputFileName="";
	// Enable tracing
	private static com.sap.aii.mapping.api.AbstractTrace trace =  null;
	public synchronized void  transform(TransformationInput arg0, TransformationOutput arg1)
	throws StreamTransformationException {
		
		try
		{
			trace = this.getTrace();
			//create instance of document builder factory
			DocumentBuilderFactory domBuilderFactory=DocumentBuilderFactory.newInstance();
			
			//from document builder factory build dom builder.			
			DocumentBuilder domBuilder=domBuilderFactory.newDocumentBuilder();
			
			//from document builder,parse the input file
			Document dom=domBuilder.parse(arg0.getInputPayload().getInputStream());
			
            isSales="NO";
			
			String result=traversingXML(dom);
			
			String directory="";
			
			String posDir=(String)arg0.getInputParameters().getString("posDir");
			String newDir=(String)arg0.getInputParameters().getString("newDir");
			String dupDir=(String)arg0.getInputParameters().getString("dupDir");
			String SERVICE = (String)arg0.getInputParameters().getString("SERVICE"); // Name of service defined in XI configuration
			String CHANNEL_NAME = (String)arg0.getInputParameters().getString("CHANNEL_NAME"); ; // Name of communication channel defined for service
			
			DynamicConfiguration conf = arg0.getDynamicConfiguration();
			DynamicConfigurationKey KEY_FILENAME = 
				 DynamicConfigurationKey.create("http://sap.com/xi/XI/System/File","FileName");
		 	 
		 	DynamicConfigurationKey KEY_DIRECTORY = 
				 DynamicConfigurationKey.create("http://sap.com/xi/XI/System/File","Directory");
			 
			 
			 
			 inputFileName=conf.get(KEY_FILENAME);
			 
				if(isSales.equals("YES")){
					directory= posDir+"/NewData1";
					
				}else{
					
					String fileName=inputFileName;
					
					NodeList transactionList= dom.getElementsByTagName("Transaction");
					for(int k=0;k<transactionList.getLength();k++){
					String businessDate= ((Element)transactionList.item(k)).getElementsByTagName("BusinessDayDate").item(0).getFirstChild().getTextContent();
					businessDate=businessDate.replaceAll("-", "");
					
				    String messageType= msgType;
				    
				    String seuquenceNo= ((Element)transactionList.item(k)).getElementsByTagName("SequenceNumber").item(0).getFirstChild().getTextContent();
				   
				    String storeNo= ((Element)transactionList.item(k)).getElementsByTagName("RetailStoreID").item(0).getFirstChild().getTextContent();
				    
				    String workstationId= ((Element)transactionList.item(k)).getElementsByTagName("WorkstationID").item(0).getFirstChild().getTextContent();
				    
				    
				    String returnValue=performRFCLookup( businessDate, fileName, messageType, seuquenceNo, storeNo, workstationId,SERVICE,CHANNEL_NAME);
				    
				    if(returnValue.equals("0")){
				    	directory= posDir+"/"+newDir;
				    }else if(returnValue.equals("4")){
				    	directory=posDir+"/"+dupDir;
				    }
				    
					}
					
					
				}
				 conf.put(KEY_DIRECTORY,directory );
				 arg1.getOutputPayload().getOutputStream().write(result.getBytes());
		}catch(Exception e){
			
		}
		
	}
	
	private  String performRFCLookup(String businessDate,String fileName,String messageType,String seuquenceNo,String storeNo,String workstationId,String SERVICE1,String CHANNEL_NAME1)
	{
		//String result="4";
		String result1=null;
		
		String SERVICE = SERVICE1; // Name of service defined in XI configuration
		String CHANNEL_NAME = CHANNEL_NAME1; // Name of communication channel defined for service
		String SAP_RFC_NAMESPACE = "urn:sap-com:document:sap:rfc:functions", // Namespace for SAP RFC definitions
		FUNCTION_MODULE = "Z_FM_DUPLICATE_CHECK", // Name of the function module called
		VALUE_NOT_FOUND = ""; // Default return value in case something goes wrong
		
		
		
		// Create document builder to create DOM XML document
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;
		factory.setNamespaceAware(false);
		factory.setValidating(false);
		 
		try {
		        // Create XML document using document builder
		        builder = factory.newDocumentBuilder();
		} catch (Exception e) {
		        trace.addWarning("Error creating DocumentBuilder - " +
		                e.getMessage());
		       // return null;
		}
		
		String rfcXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
				"<ns0:Z_FM_DUPLICATE_CHECK xmlns:ns0=\"urn:sap-com:document:sap:rfc:functions\">" +
				"<IV_BDATE>"+businessDate+"<IV_BDATE/>" +
				"<IV_FILEN>"+fileName+"<IV_FILEN/>" +
				"<IV_MESTYP>"+messageType+"<IV_MESTYP/>" +
				"<IV_SEQNO>" +seuquenceNo+"<IV_SEQNO/>" +
				"<IV_WERKS>"+storeNo+"<IV_WERKS/>" +
				"<IV_WSTID>"+workstationId+"<IV_WSTID/>" +
				"</ns0:Z_FM_DUPLICATE_CHECK>";
		
		// Prepare and perform RFC Lookup ...
		com.sap.aii.mapping.lookup.RfcAccessor accessor;
		com.sap.aii.mapping.lookup.Payload result=null;
		 
		 
		try {
		        //  Determine a communication channel (Business system + Communication channel)
		        com.sap.aii.mapping.lookup.Channel channel = LookupService.getChannel(SERVICE, CHANNEL_NAME);
		 
		        //  Get a RFC accessor for the channel.
		        accessor = LookupService.getRfcAccessor(channel);
		 
		        //  Create an XML input stream that represents the RFC request message.
		        java.io.InputStream is = new java.io.ByteArrayInputStream(rfcXML.getBytes());
		 
		        //  Create the XML Payload
		        com.sap.aii.mapping.lookup.XmlPayload payload = LookupService.getXmlPayload(is);
		 
		        //  Execute the lookup.
		        result = accessor.call(payload);
		        
		        
		 
		        if (result == null) {
		                trace.addWarning("result of RFC call is null");
		        }
		 
		} catch (com.sap.aii.mapping.lookup.LookupException e) {
		        trace.addWarning("Error during lookup - " + e);
		}
		
		// Parsing RFC Response Document
		Document docResponse = null;
		java.io.InputStream in = result.getContent();
		String returnValue = VALUE_NOT_FOUND;
		NodeList poItems = null;
		
		try {
			
	        docResponse = builder.parse(in);
	        if (docResponse == null) {
	                trace.addWarning("docResponse is null");
	        }
	       
	    	NodeList transactionList= docResponse.getElementsByTagName("rfc:Z_FM_DUPLICATE_CHECK.Response");
	    	
	    	trace.addWarning("success1");
			String endResult="";
			for(int k=0;k<transactionList.getLength();k++){
				String EV_SUBRC= ((Element)transactionList.item(k)).getElementsByTagName("EV_SUBRC").item(0).getFirstChild().getTextContent();
				trace.addWarning("success2"+EV_SUBRC);
				result1=EV_SUBRC;
			}
	}
	catch (Exception e) {
	        trace.addWarning("Error when parsing RFC Response - " + e.getMessage());
	}
		
	trace.addWarning("result1 is - " + result1);
		return result1;
	}
	
	private  String traversingXML(Node node) {
		NodeList children=node.getChildNodes();
		NamedNodeMap attributes;
		Node attrnode;
		for(int j=0;j<children.getLength();j++){
			Node child=children.item(j);
			short childType=child.getNodeType();
			String attrName="NA";
			if(childType==Node.ELEMENT_NODE){
				String nodeName=child.getNodeName();
				
				StringBuffer tempNodeName=new StringBuffer();
				tempNodeName.append("<"+nodeName);
				NamedNodeMap attr=child.getAttributes();
				for(int k=0;k<attr.getLength();k++){
					Node attrvalue=attr.item(k);
					attrvalue.getNodeName();
					attrvalue.getNodeValue();
					tempNodeName.append("  "+attrvalue.getNodeName()+"="+"\""+attrvalue.getNodeValue()+"\"");
				}
				tempNodeName.append(">");
				
				 endResult.append(tempNodeName);
					 
				 
				 traversingXML(child);
				 endResult.append("</"+nodeName+">");
				
				
				if(nodeName.equals("RetailTransaction")||nodeName.equals("WN:BSCMTransaction")||nodeName.equals("ControlTransaction")){
					isSales="YES";
										
				}else if(nodeName.equals("SOQ")){
					isSales="NO";
					msgType="SOQ";
				}else if(nodeName.equals("EODConfirmation")){
					isSales="NO";
					msgType="inventorycount";
				}
				 
			}else if(childType==Node.TEXT_NODE){
				String nodeValue=child.getNodeValue();
				nodeValue=nodeValue.replaceAll("&", "&amp;");
				nodeValue=nodeValue.replaceAll("<", "&lt;");
				endResult.append(nodeValue);
				if(nodeValue.equals("goodsreceipt")){
					isSales="NO";
					msgType="goodsreceipt";
				}else if(nodeValue.equals("poreturn")){
					isSales="NO";
					msgType="poreturn";
				}else if(nodeValue.equals("storetransfer")){
					isSales="NO";
					msgType="storetransfer";
				}else if(nodeValue.equals("sloctosloctransfer")){
					isSales="NO";
					msgType="sloctosloctransfer";
				}else if(nodeValue.equals("dailyadjustments")){
					isSales="NO";
					msgType="dailyadjustments";
				}else if(nodeValue.equals("stocktake")){
					isSales="NO";
					msgType="stocktake";
				}else if(nodeValue.equals("Nonconsumableorder")){
					isSales="NO";
					msgType="Nonconsumableorder";
				}else if(nodeValue.equals("consumableorder")){
					isSales="NO";
					msgType="consumableorder";
				}
			}
			
			
		}
		return endResult.toString();
	}

}




