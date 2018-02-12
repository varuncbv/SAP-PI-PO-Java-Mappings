package IRIS_JTI_PMI;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.sap.aii.mapping.api.AbstractTrace;
import com.sap.aii.mapping.api.AbstractTransformation;
import com.sap.aii.mapping.api.StreamTransformationException;
import com.sap.aii.mapping.api.TransformationInput;
import com.sap.aii.mapping.api.TransformationOutput;

public class AddExcelNamespace extends AbstractTransformation{
	
	String endResult;
	
	
	private static AbstractTrace trace =  null;

	 public void transform(TransformationInput arg0, TransformationOutput out) 
     throws StreamTransformationException 
     {
		 try {
			 trace = this.getTrace();
			 String Header;
				
				String Trailer;
				
				String itemEndResult="";
				
				String tempCountString="";
				
				/**
				
				Header = "<?xml version=\"1.0\"?>"+
			"<?mso-application progid=\"Excel.Sheet\"?>"+
			"<Workbook xmlns=\"urn:schemas-microsoft-com:office:spreadsheet\"" +
			" xmlns:o=\"urn:schemas-microsoft-com:office:office\" "+ 
			" xmlns:x=\"urn:schemas-microsoft-com:office:excel\""+  
			" xmlns:ss=\"urn:schemas-microsoft-com:office:spreadsheet\"  "+
			" xmlns:html=\"http://www.w3.org/TR/REC-html40\">"+		
			"<DocumentProperties xmlns=\"urn:schemas-microsoft-com:office:office\">"+
			"<Author>Nazmul</Author>"+
			"</DocumentProperties>"+
			"<ExcelWorkbook xmlns=\"urn:schemas-microsoft-com:office:excel\">"+
			"<ProtectStructure>False</ProtectStructure>"+
			"<ProtectWindows>False</ProtectWindows>"+
			"</ExcelWorkbook>"+
			"<Styles>"+
			"<Style ss:ID=\"Default\" ss:Name=\"Normal\">"+
			"<Alignment ss:Vertical=\"Bottom\"/>"+
			"<Borders/><Font/><Interior/><NumberFormat/><Protection/></Style>"+
			"<Style ss:ID=\"1\">"+
			"<Font ss:Bold=\"1\"/>"+
			"</Style></Styles><Worksheet ss:Name=\"Sheet1\">"+
			"<Table>"+
			"<Row ss:StyleID=\"1\">"+
			"<Cell><Data ss:Type=\"String\">SNO</Data></Cell>"+
			"<Cell><Data ss:Type=\"String\">DELV_DATE</Data></Cell>"+
			"<Cell><Data ss:Type=\"String\">DELV_NO</Data></Cell>"+
			"<Cell><Data ss:Type=\"String\">STORE_CD</Data></Cell>"+
			"<Cell><Data ss:Type=\"String\">ITEM_CD</Data></Cell>"+
			"<Cell><Data ss:Type=\"String\">ITEM_LONG_NAME</Data></Cell>"+
			"<Cell><Data ss:Type=\"String\">BARCODE_NO</Data></Cell>"+
			"<Cell><Data ss:Type=\"String\">ORD_SLIP_NO</Data></Cell>"+
			"<Cell><Data ss:Type=\"String\">PSA_CD</Data></Cell>"+
			"<Cell><Data ss:Type=\"Number\">ORDER_QTY</Data></Cell>"+
			"<Cell><Data ss:Type=\"Number\">ITEM_UNIT_COST_GST</Data></Cell>"+
			"<Cell><Data ss:Type=\"Number\">ITEM_UNIT_PRICE</Data></Cell>"+
			"<Cell><Data ss:Type=\"Number\">TOTAL_COST_GST</Data></Cell>"+
			"</Row>" ;
			
			**/
				
				Header ="<?xml version=\"1.0\"?>"+
				"<?mso-application progid=\"Excel.Sheet\"?>"+
				"<Workbook xmlns=\"urn:schemas-microsoft-com:office:spreadsheet\" "+
				" xmlns:o=\"urn:schemas-microsoft-com:office:office\" "+
				" xmlns:x=\"urn:schemas-microsoft-com:office:excel\" "+
				" xmlns:ss=\"urn:schemas-microsoft-com:office:spreadsheet\" "+
				" xmlns:html=\"http://www.w3.org/TR/REC-html40\">"+
				"<DocumentProperties xmlns=\"urn:schemas-microsoft-com:office:office\">"+
				"<Author>NEC</Author>"+
				"</DocumentProperties>"+
				"<ExcelWorkbook xmlns=\"urn:schemas-microsoft-com:office:excel\">"+
				"<ProtectStructure>False</ProtectStructure>"+
				"<ProtectWindows>False</ProtectWindows>"+
				"</ExcelWorkbook>"+
				"<Styles>"+
				"<Style ss:ID=\"Default\" ss:Name=\"Normal\">"+
				"<Alignment ss:Vertical=\"Bottom\"/>"+
				"<Borders/>"+
				"<Font/>"+
				"<Interior/>"+
				"<NumberFormat/>"+
				"<Protection/>"+
				"</Style>"+
				"<Style ss:ID=\"1\">"+ 
				"<Font ss:Bold=\"1\"/>"+ 
				"</Style>"+
				"</Styles>"+
				"<Worksheet ss:Name=\"Sheet1\">"+
				"<Table>"+
				"<Row ss:StyleID=\"1\">"+
				"<Cell><Data ss:Type=\"String\">SNO</Data></Cell>"+
				"<Cell><Data ss:Type=\"String\">DELV_DATE</Data></Cell>"+
				"<Cell><Data ss:Type=\"String\">DELV_NO</Data></Cell>"+
				"<Cell><Data ss:Type=\"String\">STORE_CD</Data></Cell>"+
				"<Cell><Data ss:Type=\"String\">ITEM_CD</Data></Cell>"+
				"<Cell><Data ss:Type=\"String\">ITEM_LONG_NAME</Data></Cell>"+
				"<Cell><Data ss:Type=\"String\">BARCODE_NO</Data></Cell>"+
				"<Cell><Data ss:Type=\"String\">ORD_SLIP_NO</Data></Cell>"+
				"<Cell><Data ss:Type=\"String\">PSA_CD</Data></Cell>"+
				"<Cell><Data ss:Type=\"String\">ORDER_QTY</Data></Cell>"+
				"<Cell><Data ss:Type=\"String\">ITEM_UNIT_COST</Data></Cell>"+
				"<Cell><Data ss:Type=\"String\">ITEM_UNIT_PRICE</Data></Cell>"+
				"</Row>";
			
				Trailer="</Table><AutoFilter x:Range=\"R1C1:R1C12\" xmlns = \"urn:schemas-microsoft-com:office:excel\" >"+ 
			            "</AutoFilter></Worksheet></Workbook>";
			
			
				
				
				
				
				//create a document builder factory new instance
				DocumentBuilderFactory dbf=DocumentBuilderFactory.newInstance();
				
				//create document builder from document builder factory
				DocumentBuilder db=dbf.newDocumentBuilder();
				
				//parse inputstream using document builder
				
				Document doc=db.parse(arg0.getInputPayload().getInputStream());
				
				
				NodeList OrdersList= doc.getElementsByTagName("Orders");
				
				StringBuffer itemResult=new StringBuffer();
				
				for(int k=0;k<OrdersList.getLength();k++){
					
					
					NodeList ItemsList= ((Element)OrdersList.item(k)).getElementsByTagName("Item");
					
					
					
					
				
					for(int j=0;j<ItemsList.getLength();j++){
						
						String SNO=((Element)(ItemsList.item(j))).getElementsByTagName("SNO").item(0).getFirstChild().getTextContent();
						String DELV_DATE=((Element)(ItemsList.item(j))).getElementsByTagName("DELV_DATE").item(0).getFirstChild().getTextContent();
						String DELV_NO=((Element)(ItemsList.item(j))).getElementsByTagName("DELV_NO").item(0).getFirstChild().getTextContent();
						String STORE_CD=((Element)(ItemsList.item(j))).getElementsByTagName("STORE_CD").item(0).getFirstChild().getTextContent();
						String ITEM_CD=((Element)(ItemsList.item(j))).getElementsByTagName("ITEM_CD").item(0).getFirstChild().getTextContent();
						String ITEM_LONG_NAME=((Element)(ItemsList.item(j))).getElementsByTagName("ITEM_LONG_NAME").item(0).getFirstChild().getTextContent();
						String BARCODE_NO=((Element)(ItemsList.item(j))).getElementsByTagName("BARCODE_NO").item(0).getFirstChild().getTextContent();
						
						if(((Element)(ItemsList.item(j))).getElementsByTagName("BARCODE_NO").item(0).getFirstChild()==null){
							BARCODE_NO="0";
						}else{
							
							BARCODE_NO=((Element)(ItemsList.item(j))).getElementsByTagName("BARCODE_NO").item(0).getFirstChild().getTextContent();
						}
						String ORD_SLIP_NO=((Element)(ItemsList.item(j))).getElementsByTagName("ORD_SLIP_NO").item(0).getFirstChild().getTextContent();
						String PSA_CD=((Element)(ItemsList.item(j))).getElementsByTagName("PSA_CD").item(0).getFirstChild().getTextContent();
						String ORD_QTY=((Element)(ItemsList.item(j))).getElementsByTagName("ORD_QTY").item(0).getFirstChild().getTextContent();
						String ITEM_UNIT_COST=((Element)(ItemsList.item(j))).getElementsByTagName("ITEM_UNIT_COST").item(0).getFirstChild().getTextContent();
						String ITEM_UNIT_PRICE=((Element)(ItemsList.item(j))).getElementsByTagName("ITEM_UNIT_PRICE").item(0).getFirstChild().getTextContent();
						//String TOTAL_COST_GST=((Element)(ItemsList.item(j))).getElementsByTagName("TOTAL_COST_GST").item(0).getFirstChild().getTextContent();
						
					
						String temp="<Row>"+
						"<Cell><Data ss:Type=\"String\">"+SNO+"</Data></Cell>"+
						"<Cell><Data ss:Type=\"String\">"+DELV_DATE+"</Data></Cell>"+
						"<Cell><Data ss:Type=\"String\">"+DELV_NO+"</Data></Cell>"+
						"<Cell><Data ss:Type=\"String\">"+STORE_CD+"</Data></Cell>"+
						"<Cell><Data ss:Type=\"String\">"+ITEM_CD+"</Data></Cell>"+
						"<Cell><Data ss:Type=\"String\">"+ITEM_LONG_NAME+"</Data></Cell>"+
						"<Cell><Data ss:Type=\"String\">"+BARCODE_NO+"</Data></Cell>"+
						"<Cell><Data ss:Type=\"String\">"+ORD_SLIP_NO+"</Data></Cell>"+
						"<Cell><Data ss:Type=\"String\">"+PSA_CD+"</Data></Cell>"+
						"<Cell><Data ss:Type=\"Number\">"+ORD_QTY.trim()+"</Data></Cell>"+
						"<Cell><Data ss:Type=\"Number\">"+ITEM_UNIT_COST.trim()+"</Data></Cell>"+
						"<Cell><Data ss:Type=\"Number\">"+ITEM_UNIT_PRICE.trim()+"</Data></Cell>"+
												
						"</Row>";
						
						itemResult.append(temp);
						
						
						
					}
						
					itemEndResult=itemResult.toString();
					
					
					String countString=doc.getElementsByTagName("Count").item(0).getFirstChild().getTextContent();
					
					tempCountString="<Row></Row><Row><Cell><Data ss:Type=\"String\">"+countString+"</Data></Cell></Row>";
					
					
					
				}
			
				endResult=Header+itemEndResult+tempCountString+Trailer;
				
				trace.addInfo(endResult);
			
				out.getOutputPayload().getOutputStream().write((endResult.getBytes()));
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

     }
	
	

}
