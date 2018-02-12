package iris_call_rfc_removeduplicatefiles;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class RemoveDuplicateFiles_Through_RFC_Call {

	/**
	 * @param args
	 */
	public static StringBuffer endResult = new StringBuffer();
	public static String isSales="";
	public static String msgType="";
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		try{
			
			File inputFile = new File("C:\\RemoveDuplicate\\1992_10_66_20150319110303.xml");
			
			//File inputFile = new File("C:\\RemoveDuplicate\\Test_POSLogSale_6663_20151124_025134_20151124_022133.xml");
			
			//create instance of document builder factory
			DocumentBuilderFactory domBuilderFactory=DocumentBuilderFactory.newInstance();
			
			//from document builder factory build dom builder.			
			DocumentBuilder domBuilder=domBuilderFactory.newDocumentBuilder();
			
			//from document builder,parse the input file
			Document dom=domBuilder.parse(inputFile);
			
			isSales="NO";
			
			String result=traversingXML(dom);
			
			String directory="";
			if(isSales.equals("YES")){
				directory="PosLog/NewData1";
				System.out.println("isSales is "+isSales+" directory is"+directory);
			}else{
				
				String fileName=inputFile.getName();
				System.out.println("fileName is"+fileName);
				NodeList transactionList= dom.getElementsByTagName("Transaction");
				for(int k=0;k<transactionList.getLength();k++){
				String businessDate= ((Element)transactionList.item(k)).getElementsByTagName("BusinessDayDate").item(0).getFirstChild().getTextContent();
				businessDate=businessDate.replaceAll("-", "");
				System.out.println("businessDate is"+businessDate);
			    String messageType= msgType;
			    System.out.println("messageType is"+messageType);
			    String seuquenceNo= ((Element)transactionList.item(k)).getElementsByTagName("SequenceNumber").item(0).getFirstChild().getTextContent();
			    System.out.println("seuquenceNo is"+seuquenceNo);
			    String storeNo= ((Element)transactionList.item(k)).getElementsByTagName("RetailStoreID").item(0).getFirstChild().getTextContent();
			    System.out.println("storeNo is"+storeNo);
			    String workstationId= ((Element)transactionList.item(k)).getElementsByTagName("WorkstationID").item(0).getFirstChild().getTextContent();
			    System.out.println("workstationId is"+workstationId);
			    
			    String returnValue=performRFCLookup( businessDate, fileName, messageType, seuquenceNo, storeNo, workstationId);
			    
			    if(returnValue.equals("0")){
			    	directory="PosLog/NewData1";
			    }else if(returnValue.equals("4")){
			    	directory="duplicates";
			    }
			    
				}
				
				System.out.println("directory is"+directory);
			}
			
			
			System.out.println(result);
		}catch(Exception e){
			
		}

	}
	
	private static String performRFCLookup(String businessDate,String fileName,String messageType,String seuquenceNo,String storeNo,String workstationId)
	{
		String result="4";
		
		return result;
	}
	
	private static String traversingXML(Node node) {
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
				if(nodeName.equals("RetailTransaction")||nodeName.equals("WN:BSCMTransaction")||nodeName.equals("ControlTransaction")||nodeName.equals("EODConfirmation")){
					isSales="YES";
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
				}
			}
			
			
		}
		return endResult.toString();
	}

}



