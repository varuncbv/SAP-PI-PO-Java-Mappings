package EDI;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.sap.aii.mapping.api.AbstractTransformation;
import com.sap.aii.mapping.api.StreamTransformationException;
import com.sap.aii.mapping.api.TransformationInput;
import com.sap.aii.mapping.api.TransformationOutput;

public class Build_EDI_Through_JavaMap extends AbstractTransformation {
	
	public static StringBuffer sb = new StringBuffer();
	
	public void transform(TransformationInput arg0, TransformationOutput arg1)
	throws StreamTransformationException {
// TODO Auto-generated method stub



		try{
			
			
			DocumentBuilderFactory dbf=DocumentBuilderFactory.newInstance();
			
			//create documentBuilder
			DocumentBuilder db=dbf.newDocumentBuilder();
			
			Document doc=db.parse(arg0.getInputPayload().getInputStream());
			
			String ReceiverCode=doc.getElementsByTagName("ReceiverCode").item(0).getFirstChild().getTextContent();
			
			String SalesOrganization=doc.getElementsByTagName("SalesOrganization").item(0).getFirstChild().getTextContent();
			
			//System.out.println("ReceiverCode is "+ReceiverCode+" SalesOrganization is "+SalesOrganization);
			buildDEBINT(ReceiverCode,SalesOrganization);
			
			//get all "WHOLESALERS" nodes from doc
			NodeList wholeSalersList= doc.getElementsByTagName("WHOLESALERS");
			
			
			for(int k=0;k<wholeSalersList.getLength();k++){
				
				//get all "DeliveryDetailsList" nodes from WHOLESALERS
				NodeList deliveryDetailsList= ((Element)wholeSalersList.item(k)).getElementsByTagName("DELIVERYDETAILS");
				
				for(int j=0;j<deliveryDetailsList.getLength();j++){
					
					
					String CarrierCode=((Element)(wholeSalersList.item(k))).getElementsByTagName("CarrierCode").item(0).getFirstChild().getTextContent();
					String Name=((Element)(wholeSalersList.item(k))).getElementsByTagName("Name").item(0).getFirstChild().getTextContent();
					String Delivery_number=((Element)(deliveryDetailsList.item(j))).getElementsByTagName("Delivery_number").item(0).getFirstChild().getTextContent();
					String Delivery_date_time=((Element)(deliveryDetailsList.item(j))).getElementsByTagName("Delivery_date_time").item(0).getFirstChild().getTextContent();
					String Schedule_line_date=((Element)(deliveryDetailsList.item(j))).getElementsByTagName("Schedule_line_date").item(0).getFirstChild().getTextContent();
					String Total_Weight=((Element)(deliveryDetailsList.item(j))).getElementsByTagName("Total_Weight").item(0).getFirstChild().getTextContent();
					String Sum_of_order_lines=((Element)(deliveryDetailsList.item(j))).getElementsByTagName("Sum_of_order_lines").item(0).getFirstChild().getTextContent();
					
					NodeList addressList=((Element)(wholeSalersList.item(k))).getElementsByTagName("Address");
					
					String House_Num=((Element)(addressList.item(0))).getElementsByTagName("House_Num").item(0).getFirstChild().getTextContent();
					String Street=((Element)(addressList.item(0))).getElementsByTagName("Street").item(0).getFirstChild().getTextContent();
					String Post_Code=((Element)(addressList.item(0))).getElementsByTagName("Post_Code").item(0).getFirstChild().getTextContent();
					String City=((Element)(addressList.item(0))).getElementsByTagName("City").item(0).getFirstChild().getTextContent();
					String Country=((Element)(addressList.item(0))).getElementsByTagName("Country").item(0).getFirstChild().getTextContent();
					String PO_Box="";
					if(((Element)(addressList.item(0))).getElementsByTagName("PO_Box").item(0).getFirstChild()==null){
						PO_Box="";
					}else{				
						PO_Box=((Element)(addressList.item(0))).getElementsByTagName("PO_Box").item(0).getFirstChild().getTextContent();
						
					}
					//System.out.println("House_Num"+House_Num+Street+Post_Code+City+Country+PO_Box);
					
					//System.out.println("Delivery_number is"+Delivery_number+" CarrierCode is"+CarrierCode);
					buildDEBMSG(Delivery_number,CarrierCode);
					buildDATHEU(Delivery_date_time,Schedule_line_date,Total_Weight,Delivery_number,SalesOrganization,
							ReceiverCode,CarrierCode,Name,House_Num,Street,PO_Box,Post_Code,City,Country,Sum_of_order_lines);
					
					NodeList itemList=((Element)(deliveryDetailsList.item(j))).getElementsByTagName("Item");
					
					for(int m=0;m<itemList.getLength();m++){
						
						String Weight=((Element)(itemList.item(m))).getElementsByTagName("Weight").item(0).getFirstChild().getTextContent();
						String Material_number=((Element)(itemList.item(m))).getElementsByTagName("Material_number").item(0).getFirstChild().getTextContent();
						String Batch_number=((Element)(itemList.item(m))).getElementsByTagName("Batch_number").item(0).getFirstChild().getTextContent();
						String Item_description=((Element)(itemList.item(m))).getElementsByTagName("Item_description").item(0).getFirstChild().getTextContent();
						String Delivered_quantity=((Element)(itemList.item(m))).getElementsByTagName("Item_description").item(0).getFirstChild().getTextContent();
						String Shelf_Life_Expiration=((Element)(itemList.item(m))).getElementsByTagName("Shelf_Life_Expiration").item(0).getFirstChild().getTextContent();
						String Sales_order_number=((Element)(itemList.item(m))).getElementsByTagName("Sales_order_number").item(0).getFirstChild().getTextContent();
						String Sales_order_line_item=((Element)(itemList.item(m))).getElementsByTagName("Sales_order_line_item").item(0).getFirstChild().getTextContent();
						String PO_Date=((Element)(itemList.item(m))).getElementsByTagName("PO_Date").item(0).getFirstChild().getTextContent();
						
								buildINFLIN(Weight,Material_number,Batch_number,Item_description,Delivered_quantity,Shelf_Life_Expiration,Sales_order_number,Sales_order_line_item,PO_Date);
					}
					sb.append("\n\"FINMSG 000000000000000001\"");
					
					
				}
			
			}
			
			
			arg1.getOutputPayload().getOutputStream().write((sb.toString().getBytes()));
			
			
		}catch (Exception e) {
			// TODO: handle exception
		}

	}

	private static String buildINFLIN(String weight, String materialNumber,
			String batchNumber, String itemDescription,
			String deliveredQuantity, String shelfLifeExpiration,
			String salesOrderNumber, String salesOrderLineItem, String pODate) {
		// TODO Auto-generated method stub
		
		String temp1 ="\n\"INFLIN "+materialNumber+"                       C13\"";
		String temp2="\n\"INFCOM "+salesOrderLineItem+"   "+batchNumber+"                              NB\"";
		String temp3="\n\""+itemDescription            +"            91  DU\"";
		String temp4="\n\"QTEART 12  "+deliveredQuantity+" PCE\"";
		String temp5="\n\"DATART 36  "+pODate+"                            102\"";
		String temp6="\n\"TXTLIB 888 "+salesOrderLineItem+"               91  00000000000000000000000000000000000\"";
		String temp7="\n\"REFART 16/78355                            ON\"";
		String temp8="\n\"REFART "+salesOrderNumber+"                             UC\"";
		String temp9="\n\"REFART "+salesOrderLineItem+"                                 LI\"";
		String temp10="\n\"DATREF "+pODate+"                            102\"";
		sb.append(temp1+temp2+temp3+temp4+temp5+temp6+temp7+temp8+temp9+temp10);
		return null;
	}

	private static void buildDATHEU(String deliveryDateTime,
			String scheduleLineDate, String totalWeight,String deliveryNumber,String salesorganization,
			String receivercode,String carriercode,String name,String House_Num,String Street,String PO_Box,
			String Post_Code,String City,String Country,String Sum_of_order_lines) {
		// TODO Auto-generated method stub
		String temp1="\n\"DATHEU 137 "+deliveryDateTime+"                        203\"";
		String temp2="\n\"DATHEU  11 "+deliveryDateTime+"                        203\"";
		String temp3="\n\"DATHEU 191 "+scheduleLineDate+"                            102\"";
		String temp4="\n\"MESPHY AAD "+totalWeight+" KGM\"";
		String temp5="\n\"REFEXP DQ  "+deliveryNumber+"\"";
		String temp6="\n\"REFEXP CN  8L66528008683\"";
		String temp7="\n\"IDPART BY  "+salesorganization+"                                 ZZZ\"";
		String temp8="\n\"IDPART SH  "+receivercode+"                                 ZZZ\"";
		String temp9="\n\"IDPART ST  "+carriercode+"                              001\"";
		String temp10="\n\"NOMPAR "+name+"\"";
		String temp11="\n\"RUPAR1 "+House_Num+"\"";
		String temp12="\n\"RUPAR2 "+Street+" "+PO_Box+"\"";
		String temp13="\n\"VILPAR "+Post_Code+"             "+City+" "+Country+"\"";
		String temp14="\n\"INFOTP                   30  31  "+carriercode+" 000 000\"";
		String temp15="\n\"NIVEAU 1\"";
		String temp16="\n\"NBUEXP 00000001 99      CT\"";
		String temp17="\n\"MESURE     AAB "+totalWeight+"                                       KGM\"";
		String temp18="\n\"QTECON "+Sum_of_order_lines+"\"";
		String temp19="\n\"IDMARK 17\"";
		String temp20="\n\"CODCOL BJ  000\"";
		sb.append(temp1+temp2+temp3+temp4+temp5+temp6+temp7+temp8+temp9+temp10+temp11+temp12+temp13+temp14+temp15+temp16+temp17+temp18+temp19+temp20);
		
	}

	private static void buildDEBMSG(String deliveryNumber, String carrierCode) {
		// TODO Auto-generated method stub
		
		String temp1="\n\"DEBMSG DESADV ";
		String temp2="                          ORG ";
		String temp3="\"";
		String debMsgResult=temp1+deliveryNumber+temp2+carrierCode+temp3;
		sb.append(debMsgResult);
		
	}

	private static void buildDEBINT(String receiverCode,
			String salesOrganization) {
		String temp1="\"DEBINT ";
		String temp2="                                 EDI ";
		String temp3="                                 EDI T A\"";
		String debIntResult=temp1+salesOrganization+temp2+receiverCode+temp3;
		sb.append(debIntResult);
		
	}

}
