package gst_to_iris_poslog;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.sap.aii.mapping.api.AbstractTrace;
import com.sap.aii.mapping.api.AbstractTransformation;
import com.sap.aii.mapping.api.DynamicConfiguration;
import com.sap.aii.mapping.api.DynamicConfigurationKey;
import com.sap.aii.mapping.api.StreamTransformationException;
import com.sap.aii.mapping.api.TransformationInput;
import com.sap.aii.mapping.api.TransformationOutput;

public class PoslogConversionToBapiJavaMap extends AbstractTransformation {
	
	private static AbstractTrace trace =  null;
	
	public synchronized void  transform(TransformationInput arg0, TransformationOutput arg1)
	throws StreamTransformationException {
		
		try
		{
			
			 DynamicConfigurationKey KEY_FILENAME = 
				 DynamicConfigurationKey.create("http://sap.com/xi/XI/System/File","FileName");
			 
			 DynamicConfiguration conf = arg0.getDynamicConfiguration();
			 
			 String fileName=conf.get(KEY_FILENAME);
				
		//create a document builder factory new instance
		DocumentBuilderFactory dbf=DocumentBuilderFactory.newInstance();
		
		//create document builder from document builder factory
		DocumentBuilder db=dbf.newDocumentBuilder();
		
		//parse inputstream using document builder		
		Document doc=db.parse(arg0.getInputPayload().getInputStream());
		
		//get all "Transactions" nodes from doc
		NodeList transactionList= doc.getElementsByTagName("Transaction");
		
		String Header ="<ns0:_-POSDW_-BAPI_POSTR_CREATE xmlns:ns0=\"urn:sap-com:document:sap:rfc:functions\"><I_COMMIT>X</I_COMMIT>";
		
		//string buffer for end result
		StringBuffer endResult=new StringBuffer();
		
		//string buffer for trx ext
		StringBuffer endResult_trxext=new StringBuffer();
		endResult_trxext.append("<TRANSACTIONEXT>");
		
		//string buffer for trx
		StringBuffer endResult_trx=new StringBuffer();
		endResult_trx.append("<TRANSACTION>");
		
		//string buffer for retail line item 
		StringBuffer endResult_RL=new StringBuffer();
		endResult_RL.append("<RETAILLINEITEM>");
		
		//string buffer for Line Item Loyalty 
		StringBuffer endResult_LIL=new StringBuffer();
		endResult_LIL.append("<LINEITEMLOYALTY>");
		
		//string buffer for TRANSACTION LOYALTY
		StringBuffer endResult_TrxLylt=new StringBuffer();
		endResult_TrxLylt.append("<TRANSACTIONLOYALTY>");
		
		//string buffer for LINE ITEM DISCOUNT
		StringBuffer endResult_itemDisc=new StringBuffer();
		endResult_itemDisc.append("<LINEITEMDISCOUNT>");
		
		//string buffer for LINE ITEM VOID
		StringBuffer endResult_itemVoid=new StringBuffer();
		endResult_itemVoid.append("<LINEITEMVOID>");
		
		//string buffer for LINE ITEM TAX
		StringBuffer endResult_itemTax=new StringBuffer();
		endResult_itemTax.append("<LINEITEMTAX>");
		
		//string buffer for tender
		StringBuffer endResult_Tender=new StringBuffer();
		endResult_Tender.append("<TENDER>");
		
		//string buffer for FINANCIALMOVEMENT
		StringBuffer endResult_FM=new StringBuffer();
		endResult_FM.append("<FINANCIALMOVEMENT>");
		
		//string buffer for FINANCIALMOVEMENTEXT
		StringBuffer endResult_FMEXT=new StringBuffer();
		endResult_FMEXT.append("<FINANCIALMOVEMENTEXT>");
		
		//string buffer for TRANSACTIONDISCOUNT
		StringBuffer endResult_TDEXT=new StringBuffer();
		endResult_TDEXT.append("<TRANSACTIONDISCOUNT>");
		
		endResult.append(Header);
		int bscmfmCount=0;
		String TransactionTypeCode="";
		String RetailTypeCode="";
		String ShiftNo="";
        String salesTotalCount="";
		for(int cntTrx=0;cntTrx<transactionList.getLength();cntTrx++){
			bscmfmCount=0;
						String varSaleOrReturn="";
			
			String RetailStoreID=((Element)(transactionList.item(cntTrx))).getElementsByTagName("RetailStoreID").item(0).getFirstChild().getTextContent();
			String BusinessDayDate=((Element)(transactionList.item(cntTrx))).getElementsByTagName("BusinessDayDate").item(0).getFirstChild().getTextContent();
			String WorkstationID=((Element)(transactionList.item(cntTrx))).getElementsByTagName("WorkstationID").item(0).getFirstChild().getTextContent();
			String SequenceNumber=((Element)(transactionList.item(cntTrx))).getElementsByTagName("SequenceNumber").item(0).getFirstChild().getTextContent();
			String BeginDateTime=((Element)(transactionList.item(cntTrx))).getElementsByTagName("BeginDateTime").item(0).getFirstChild().getTextContent();
			String EndDateTime=((Element)(transactionList.item(cntTrx))).getElementsByTagName("EndDateTime").item(0).getFirstChild().getTextContent();
			String OperatorID=((Element)(transactionList.item(cntTrx))).getElementsByTagName("OperatorID").item(0).getFirstChild().getTextContent();
			String CurrencyCode=((Element)(transactionList.item(cntTrx))).getElementsByTagName("CurrencyCode").item(0).getFirstChild().getTextContent();
			
			

			
			
		
			
			NodeList bscmTransactionList=((Element)transactionList.item(cntTrx)).getElementsByTagName("BSCMTransaction");
			
			if(bscmTransactionList.getLength()>0)
			{
			for(int cntBscm=0;cntBscm<bscmTransactionList.getLength();cntBscm++){
				
				
				
				
				//start of payout
				NodeList payOutList=((Element)bscmTransactionList.item(cntBscm)).getElementsByTagName("PayOut");
								
				for(int cntPayOut=0;cntPayOut<payOutList.getLength();cntPayOut++){
					
					
					
					NodeList accountMovementList=((Element)payOutList.item(cntPayOut)).getElementsByTagName("AccountMovement");
					int tmpCount=1;
					
					for(int accountMovementCnt=0;accountMovementCnt<accountMovementList.getLength();accountMovementCnt++){
						 //bscmfmCount=bscmfmCount+1;
						NamedNodeMap attr=accountMovementList.item(accountMovementCnt).getAttributes();
						
						 ShiftNo="";
						String szAccountCode="";
						String Amount="";
						String attrTypeCode="";
						String szBusinessDate="";
						String AccountGroupID="";
						String bIsChangeFund="";
						try
						{
										
							ShiftNo=((Element)(accountMovementList.item(accountMovementCnt))).getElementsByTagName("ShiftNo").item(0).getFirstChild().getTextContent();
							
							
						
						}catch(Exception e){
							 //trace.addInfo("exception in payout shiftno ");
						}
						
						try
						{
										
							
							Amount=((Element)(accountMovementList.item(accountMovementCnt))).getElementsByTagName("Amount").item(0).getFirstChild().getTextContent();
							
							
						
						}catch(Exception e){
							 //trace.addInfo("exception in payout  accountcode or amount");
						}
						
						try
						{
										
							
							
							szAccountCode=((Element)(accountMovementList.item(accountMovementCnt))).getElementsByTagName("szAccountCode").item(0).getFirstChild().getTextContent();
							
						
						}catch(Exception e){
							 //trace.addInfo("exception in payout  accountcode or amount");
						}
						
						try
						{
										
							
							szBusinessDate=((Element)(accountMovementList.item(accountMovementCnt))).getElementsByTagName("szBusinessDate").item(0).getFirstChild().getTextContent();
							
							
						
						}catch(Exception e){
							 //trace.addInfo("exception in payout szBusinessDate");
						}
						
						try
						{
										
							
							AccountGroupID=((Element)(accountMovementList.item(accountMovementCnt))).getElementsByTagName("AccountGroupID").item(0).getFirstChild().getTextContent();
							
							
						
						}catch(Exception e){
							 //trace.addInfo("exception in payout szBusinessDate");
						}
						
						try
						{
										
							
							bIsChangeFund=((Element)(accountMovementList.item(accountMovementCnt))).getElementsByTagName("bIsChangeFund").item(0).getFirstChild().getTextContent();
							
							
						
						}catch(Exception e){
							 //trace.addInfo("exception in payout szBusinessDate");
						}
						
					//for(int attrCnt=0;attrCnt<attr.getLength();attrCnt++){
						
					//	Node attrvalue=attr.item(attrCnt);
						
						 //trace.addInfo("attrvalue.getNodeName() "+attrvalue.getNodeName());
					//	if(attrvalue.getNodeName().equals("TypeCode")){
						//	attrTypeCode=attrvalue.getNodeValue();
							
						//}
						
						
						if((!(AccountGroupID.equals("StoreSafe")))||bIsChangeFund.equals("1")){
							bscmfmCount=bscmfmCount+1;
							String item ="<item><RETAILSTOREID>"+RetailStoreID+"</RETAILSTOREID>"+
         "<BUSINESSDAYDATE>"+BusinessDayDate+"</BUSINESSDAYDATE>"+
        "<WORKSTATIONID>"+WorkstationID+"</WORKSTATIONID>"+
        "<TRANSACTIONSEQUENCENUMBER>"+SequenceNumber+"_"+(bscmfmCount)+"</TRANSACTIONSEQUENCENUMBER>"+         
         "<FIELDGROUP>POSHDR</FIELDGROUP><FIELDNAME>SHIFTNO</FIELDNAME>"+
         "<FIELDVALUE>"+ShiftNo+"</FIELDVALUE>"+
           "<TRANSACTIONTYPECODE>1301</TRANSACTIONTYPECODE></item>";
							
							endResult_trxext.append(item);
							String item1 ="<item><RETAILSTOREID>"+RetailStoreID+"</RETAILSTOREID>"+
					         "<BUSINESSDAYDATE>"+BusinessDayDate+"</BUSINESSDAYDATE>"+
					        "<WORKSTATIONID>"+WorkstationID+"</WORKSTATIONID>"+
					        "<TRANSACTIONSEQUENCENUMBER>"+SequenceNumber+"_"+(bscmfmCount)+"</TRANSACTIONSEQUENCENUMBER>"+         
					         "<FIELDGROUP>POSLOG</FIELDGROUP><FIELDNAME>FNAME</FIELDNAME>"+
					         "<FIELDVALUE>"+fileName+"</FIELDVALUE>"+
					           "<TRANSACTIONTYPECODE>1301</TRANSACTIONTYPECODE></item>";
												
												endResult_trxext.append(item1);
												
							
							String item_trx ="<item><RETAILSTOREID>"+RetailStoreID+"</RETAILSTOREID>"+
         "<BUSINESSDAYDATE>"+BusinessDayDate+"</BUSINESSDAYDATE>"+
          "<TRANSACTIONTYPECODE>1301</TRANSACTIONTYPECODE>"+           
        "<WORKSTATIONID>"+WorkstationID+"</WORKSTATIONID>"+
         "<TRANSACTIONSEQUENCENUMBER>"+SequenceNumber+"_"+(bscmfmCount)+"</TRANSACTIONSEQUENCENUMBER>"+
         "<BEGINDATETIMESTAMP>"+BeginDateTime+"</BEGINDATETIMESTAMP>"+
         "<ENDDATETIMESTAMP>"+EndDateTime+"</ENDDATETIMESTAMP>"+
         "<DEPARTMENT></DEPARTMENT>"+
         "<OPERATORQUALIFIER></OPERATORQUALIFIER>"+
         "<OPERATORID>"+OperatorID+"</OPERATORID>"+
         "<TRANSACTIONCURRENCY>"+CurrencyCode+"</TRANSACTIONCURRENCY>"+
         "<TRANSACTIONCURRENCY_ISO>"+CurrencyCode+"</TRANSACTIONCURRENCY_ISO>"+
         "<PARTNERQUALIFIER/><PARTNERID/></item>";  
							
							endResult_trx.append(item_trx);
							
							
							String item_fmext ="<item>"+
      "<RETAILSTOREID>"+RetailStoreID+"</RETAILSTOREID>"+
         "<BUSINESSDAYDATE>"+BusinessDayDate+"</BUSINESSDAYDATE>"+
         "<TRANSACTIONTYPECODE>1301</TRANSACTIONTYPECODE>"+         
         "<WORKSTATIONID>"+WorkstationID+"</WORKSTATIONID>"+
         "<TRANSACTIONSEQUENCENUMBER>"+SequenceNumber+"_"+(bscmfmCount)+"</TRANSACTIONSEQUENCENUMBER>"+
         "<FINANCIALSEQUENCENUMBER></FINANCIALSEQUENCENUMBER>"+        
         "<FIELDGROUP>ZWFIB</FIELDGROUP>"+
        " <FIELDNAME>BANKDATUM</FIELDNAME>"+
         "<FIELDVALUE>"+szBusinessDate+"</FIELDVALUE>"+
         
      "</item>";
												
												endResult_FMEXT.append(item_fmext);
												
												
												
												if(AccountGroupID.equals("Bank In Variance"))
												{
													
													
													double tmpAmount=new Double(Amount).doubleValue();
													if(tmpAmount>=0){
														szAccountCode="BIV_POS";
													}else{
														szAccountCode="BIV_NEG";
													}
												}else if(bIsChangeFund.equals("1")){
													szAccountCode="1109201";
												}else{
													szAccountCode=szAccountCode;
												}
							
												String item_fm=" <item>"+
											       "<RETAILSTOREID>"+RetailStoreID+"</RETAILSTOREID>"+
											        " <BUSINESSDAYDATE>"+BusinessDayDate+"</BUSINESSDAYDATE>"+
											         "<TRANSACTIONTYPECODE>1301</TRANSACTIONTYPECODE> "+        
											         "<WORKSTATIONID>"+WorkstationID+"</WORKSTATIONID>"+
											         "<TRANSACTIONSEQUENCENUMBER>"+SequenceNumber+"_"+(bscmfmCount)+"</TRANSACTIONSEQUENCENUMBER>"+
											         "<FINANCIALSEQUENCENUMBER></FINANCIALSEQUENCENUMBER>"+
											         "<FINANCIALTYPECODE>"+szAccountCode+"</FINANCIALTYPECODE>"+
											        " <ACCOUNTID>"+RetailStoreID+"</ACCOUNTID>"+
											         "<ACCOUNTASSIGNMENTOBJECT>"+attrTypeCode+"</ACCOUNTASSIGNMENTOBJECT>"+
											         "<AMOUNT>"+Amount+"</AMOUNT>"+
											         "<FINANCIALCURRENCY>"+CurrencyCode+"</FINANCIALCURRENCY>"+
											         "<FINANCIALCURRENCY_ISO>"+CurrencyCode+"</FINANCIALCURRENCY_ISO>"+
											         "<REFERERENCEID></REFERERENCEID>"+
											     " </item>";
												endResult_FM.append(item_fm);
												
												tmpCount=tmpCount+1;
						}
						
						
					//}
					
					
					
					
					}
					
				} //end of payout
				
				//start of payin
				NodeList payInList=((Element)bscmTransactionList.item(cntBscm)).getElementsByTagName("PayIn");
								
				for(int cntPayIn=0;cntPayIn<payInList.getLength();cntPayIn++){
					
					
					
					NodeList accountMovementList=((Element)payInList.item(cntPayIn)).getElementsByTagName("AccountMovement");
					int tmpCount=1;
					
					for(int accountMovementCnt=0;accountMovementCnt<accountMovementList.getLength();accountMovementCnt++){
					//	bscmfmCount=bscmfmCount+1;
						NamedNodeMap attr=accountMovementList.item(accountMovementCnt).getAttributes();
						
					 ShiftNo="";
						String szAccountCode="";
						String Amount="";
						String attrTypeCode="";
						try
						{
										
							ShiftNo=((Element)(accountMovementList.item(accountMovementCnt))).getElementsByTagName("ShiftNo").item(0).getFirstChild().getTextContent();
							
						
						}catch(Exception e){
							 ShiftNo="";
						}
						try
						{
										
							
							Amount=((Element)(accountMovementList.item(accountMovementCnt))).getElementsByTagName("Amount").item(0).getFirstChild().getTextContent();
							
							
						
						}catch(Exception e){
							 //trace.addInfo("exception in payout  accountcode or amount");
						}
						
						try
						{
										
							
							
							szAccountCode=((Element)(accountMovementList.item(accountMovementCnt))).getElementsByTagName("szAccountCode").item(0).getFirstChild().getTextContent();
							
						
						}catch(Exception e){
							 //trace.addInfo("exception in payout  accountcode or amount");
						}
						
						
						
					for(int attrCnt=0;attrCnt<attr.getLength();attrCnt++){
						
						Node attrvalue=attr.item(attrCnt);
						
						
						if(attrvalue.getNodeValue().equals("Credit")){
							bscmfmCount=bscmfmCount+1;
							String item ="<item><RETAILSTOREID>"+RetailStoreID+"</RETAILSTOREID>"+
         "<BUSINESSDAYDATE>"+BusinessDayDate+"</BUSINESSDAYDATE>"+
        "<WORKSTATIONID>"+WorkstationID+"</WORKSTATIONID>"+
        "<TRANSACTIONSEQUENCENUMBER>"+SequenceNumber+"_"+(bscmfmCount)+"</TRANSACTIONSEQUENCENUMBER>"+         
         "<FIELDGROUP>POSHDR</FIELDGROUP><FIELDNAME>SHIFTNO</FIELDNAME>"+
         "<FIELDVALUE>"+ShiftNo+"</FIELDVALUE>"+
           "<TRANSACTIONTYPECODE>1301</TRANSACTIONTYPECODE></item>";
							
							endResult_trxext.append(item);
							
							String item1 ="<item><RETAILSTOREID>"+RetailStoreID+"</RETAILSTOREID>"+
					         "<BUSINESSDAYDATE>"+BusinessDayDate+"</BUSINESSDAYDATE>"+
					        "<WORKSTATIONID>"+WorkstationID+"</WORKSTATIONID>"+
					        "<TRANSACTIONSEQUENCENUMBER>"+SequenceNumber+"_"+(bscmfmCount)+"</TRANSACTIONSEQUENCENUMBER>"+         
					         "<FIELDGROUP>POSLOG</FIELDGROUP><FIELDNAME>FNAME</FIELDNAME>"+
					         "<FIELDVALUE>"+fileName+"</FIELDVALUE>"+
					           "<TRANSACTIONTYPECODE>1301</TRANSACTIONTYPECODE></item>";
												
												endResult_trxext.append(item1);
												
												
							
							String item_trx ="<item><RETAILSTOREID>"+RetailStoreID+"</RETAILSTOREID>"+
					         "<BUSINESSDAYDATE>"+BusinessDayDate+"</BUSINESSDAYDATE>"+
					          "<TRANSACTIONTYPECODE>1301</TRANSACTIONTYPECODE>"+           
					        "<WORKSTATIONID>"+WorkstationID+"</WORKSTATIONID>"+
					         "<TRANSACTIONSEQUENCENUMBER>"+SequenceNumber+"_"+(bscmfmCount)+"</TRANSACTIONSEQUENCENUMBER>"+
					         "<BEGINDATETIMESTAMP>"+BeginDateTime+"</BEGINDATETIMESTAMP>"+
					         "<ENDDATETIMESTAMP>"+EndDateTime+"</ENDDATETIMESTAMP>"+
					         "<DEPARTMENT></DEPARTMENT>"+
					         "<OPERATORQUALIFIER></OPERATORQUALIFIER>"+
					         "<OPERATORID>"+OperatorID+"</OPERATORID>"+
					         "<TRANSACTIONCURRENCY>"+CurrencyCode+"</TRANSACTIONCURRENCY>"+
					         "<TRANSACTIONCURRENCY_ISO>"+CurrencyCode+"</TRANSACTIONCURRENCY_ISO>"+
					         "<PARTNERQUALIFIER/><PARTNERID/></item>";  
												
												endResult_trx.append(item_trx);
												
												
												
												String item_fm=" <item>"+
											       "<RETAILSTOREID>"+RetailStoreID+"</RETAILSTOREID>"+
											        " <BUSINESSDAYDATE>"+BusinessDayDate+"</BUSINESSDAYDATE>"+
											         "<TRANSACTIONTYPECODE>1301</TRANSACTIONTYPECODE> "+        
											         "<WORKSTATIONID>"+WorkstationID+"</WORKSTATIONID>"+
											         "<TRANSACTIONSEQUENCENUMBER>"+SequenceNumber+"_"+(bscmfmCount)+"</TRANSACTIONSEQUENCENUMBER>"+
											         "<FINANCIALSEQUENCENUMBER></FINANCIALSEQUENCENUMBER>"+
											         "<FINANCIALTYPECODE>3101</FINANCIALTYPECODE>"+
											        " <ACCOUNTID>"+RetailStoreID+"</ACCOUNTID>"+
											         "<ACCOUNTASSIGNMENTOBJECT>"+attrTypeCode+"</ACCOUNTASSIGNMENTOBJECT>"+
											         "<AMOUNT>"+Amount+"</AMOUNT>"+
											         "<FINANCIALCURRENCY>"+CurrencyCode+"</FINANCIALCURRENCY>"+
											         "<FINANCIALCURRENCY_ISO>"+CurrencyCode+"</FINANCIALCURRENCY_ISO>"+
											         "<REFERERENCEID></REFERERENCEID>"+
											     " </item>";
												endResult_FM.append(item_fm);
												
												tmpCount=tmpCount+1;
						}
					}// end of for 
					
					
					
					}
					
				} //end of payin
				
				
				
				//start of session settle based on tender summary
				try
				{
					NodeList sessionSettleList=((Element)bscmTransactionList.item(cntBscm)).getElementsByTagName("SessionSettle");
					
					for(int cntSS=0;cntSS<sessionSettleList.getLength();cntSS++){
						
						 String finCode="";
						 String attrTypeCode="";
						 String Amount="";
						 String tsSLAmount="";
							String externalTenderTypeValue="";
						 
						NodeList tenderSummaryList=((Element)sessionSettleList.item(cntSS)).getElementsByTagName("TenderSummary");
						
						
						for(int tenderSummaryCnt=0;tenderSummaryCnt<tenderSummaryList.getLength();tenderSummaryCnt++)
						{
							
							NamedNodeMap attr=tenderSummaryList.item(tenderSummaryCnt).getAttributes();
							for(int attrCnt=0;attrCnt<attr.getLength();attrCnt++){
								
								Node attrvalue=attr.item(attrCnt);
								
								
								if(attrvalue.getNodeName().equals("ExternalTenderType")){
									
									finCode="SessionSettle-"+attrvalue.getNodeValue();
								}
							}
							
							
						
				bscmfmCount=bscmfmCount+1;
				
				
				//start of short
				
				try
				{
				NodeList tsPIList=((Element)tenderSummaryList.item(tenderSummaryCnt)).getElementsByTagName("Short");
				for(int cnt_tsPIList=0;cnt_tsPIList<tsPIList.getLength();cnt_tsPIList++){
					finCode=finCode+"-Short";
					 tsSLAmount="";
					 externalTenderTypeValue="";
					
					try
					{
						tsSLAmount=((Element)(tsPIList.item(cnt_tsPIList))).getElementsByTagName("Amount").item(0).getFirstChild().getTextContent();
						Amount=tsSLAmount;
					}catch(Exception e){
						 //trace.addInfo(" error in tender summary floatstart amount");
					}
					
					NamedNodeMap attrSales=tsPIList.item(cnt_tsPIList).getAttributes();
					
					for(int attrCnt=0;attrCnt<attrSales.getLength();attrCnt++){
						
						Node attrvalue=attrSales.item(attrCnt);
						
						if(attrvalue.getNodeName().equals("ExternalTenderType")){
							externalTenderTypeValue=attrvalue.getNodeValue();
						}
					}
					
					String item_TSS4="<item>"+
"<RETAILSTOREID>"+RetailStoreID+"</RETAILSTOREID>"+
" <BUSINESSDAYDATE>"+BusinessDayDate+"</BUSINESSDAYDATE>"+
" <TRANSACTIONTYPECODE>1302</TRANSACTIONTYPECODE> "+        
" <WORKSTATIONID>"+WorkstationID+"</WORKSTATIONID>"+
" <TRANSACTIONSEQUENCENUMBER>"+SequenceNumber+"_"+(bscmfmCount)+"</TRANSACTIONSEQUENCENUMBER>"+
"<FINANCIALSEQUENCENUMBER></FINANCIALSEQUENCENUMBER>"+        
"<FIELDGROUP>FINTRANS</FIELDGROUP>"+
"<FIELDNAME>SHORT</FIELDNAME>"+
"<FIELDVALUE>"+tsSLAmount+"</FIELDVALUE>"+
"<EXTERNALTENDERTYPE>"+externalTenderTypeValue+"</EXTERNALTENDERTYPE>"+
"</item>";
					
					endResult_FMEXT.append(item_TSS4);
				}
				
				}catch(Exception e){
					
				}
				
				//end of for Short
				
				
				//start of Over
				
				try
				{
				NodeList tsPIList=((Element)tenderSummaryList.item(tenderSummaryCnt)).getElementsByTagName("Over");
				for(int cnt_tsPIList=0;cnt_tsPIList<tsPIList.getLength();cnt_tsPIList++){
					finCode=finCode+"-Over";
					 tsSLAmount="";
					 externalTenderTypeValue="";
					
					try
					{
						tsSLAmount=((Element)(tsPIList.item(cnt_tsPIList))).getElementsByTagName("Amount").item(0).getFirstChild().getTextContent();
						Amount="-"+tsSLAmount;
					}catch(Exception e){
						 //trace.addInfo(" error in tender summary floatstart amount");
					}
					
					NamedNodeMap attrSales=tsPIList.item(cnt_tsPIList).getAttributes();
					
					for(int attrCnt=0;attrCnt<attrSales.getLength();attrCnt++){
						
						Node attrvalue=attrSales.item(attrCnt);
						
						if(attrvalue.getNodeName().equals("ExternalTenderType")){
							externalTenderTypeValue=attrvalue.getNodeValue();
						}
					}
					
					String item_TSS4="<item>"+
"<RETAILSTOREID>"+RetailStoreID+"</RETAILSTOREID>"+
" <BUSINESSDAYDATE>"+BusinessDayDate+"</BUSINESSDAYDATE>"+
" <TRANSACTIONTYPECODE>1302</TRANSACTIONTYPECODE> "+        
" <WORKSTATIONID>"+WorkstationID+"</WORKSTATIONID>"+
" <TRANSACTIONSEQUENCENUMBER>"+SequenceNumber+"_"+(bscmfmCount)+"</TRANSACTIONSEQUENCENUMBER>"+
"<FINANCIALSEQUENCENUMBER></FINANCIALSEQUENCENUMBER>"+        
"<FIELDGROUP>FINTRANS</FIELDGROUP>"+
"<FIELDNAME>OVER</FIELDNAME>"+
"<FIELDVALUE>"+tsSLAmount+"</FIELDVALUE>"+
"<EXTERNALTENDERTYPE>"+externalTenderTypeValue+"</EXTERNALTENDERTYPE>"+
"</item>";
					
					endResult_FMEXT.append(item_TSS4);
				}
				
				}catch(Exception e){
					
				}
				
				//end of for Over
				
				
//start of sales
				
				try
				{
				NodeList tsPIList=((Element)tenderSummaryList.item(tenderSummaryCnt)).getElementsByTagName("Sales");
				for(int cnt_tsPIList=0;cnt_tsPIList<tsPIList.getLength();cnt_tsPIList++){
					 tsSLAmount="";
					 externalTenderTypeValue="";
					
					try
					{
						tsSLAmount=((Element)(tsPIList.item(cnt_tsPIList))).getElementsByTagName("Amount").item(0).getFirstChild().getTextContent();
						
					}catch(Exception e){
						 //trace.addInfo(" error in tender summary floatstart amount");
					}
					
					NamedNodeMap attrSales=tsPIList.item(cnt_tsPIList).getAttributes();
					
					for(int attrCnt=0;attrCnt<attrSales.getLength();attrCnt++){
						
						Node attrvalue=attrSales.item(attrCnt);
						
						if(attrvalue.getNodeName().equals("ExternalTenderType")){
							externalTenderTypeValue=attrvalue.getNodeValue();
						}
					}
					
					String item_TSS4="<item>"+
"<RETAILSTOREID>"+RetailStoreID+"</RETAILSTOREID>"+
" <BUSINESSDAYDATE>"+BusinessDayDate+"</BUSINESSDAYDATE>"+
" <TRANSACTIONTYPECODE>1302</TRANSACTIONTYPECODE> "+        
" <WORKSTATIONID>"+WorkstationID+"</WORKSTATIONID>"+
" <TRANSACTIONSEQUENCENUMBER>"+SequenceNumber+"_"+(bscmfmCount)+"</TRANSACTIONSEQUENCENUMBER>"+
"<FINANCIALSEQUENCENUMBER></FINANCIALSEQUENCENUMBER>"+        
"<FIELDGROUP>FINTRANS</FIELDGROUP>"+
"<FIELDNAME>SALES</FIELDNAME>"+
"<FIELDVALUE>"+tsSLAmount+"</FIELDVALUE>"+
"<EXTERNALTENDERTYPE>"+externalTenderTypeValue+"</EXTERNALTENDERTYPE>"+
"</item>";
					
					endResult_FMEXT.append(item_TSS4);
				}
				
				}catch(Exception e){
					
				}
				
				//end of for sales
				
				
//start of PayIs
				
				try
				{
				NodeList tsPIList=((Element)tenderSummaryList.item(tenderSummaryCnt)).getElementsByTagName("PayIs");
				for(int cnt_tsPIList=0;cnt_tsPIList<tsPIList.getLength();cnt_tsPIList++){
					 tsSLAmount="";
					 externalTenderTypeValue="";
					
					try
					{
						tsSLAmount=((Element)(tsPIList.item(cnt_tsPIList))).getElementsByTagName("Amount").item(0).getFirstChild().getTextContent();
						
					}catch(Exception e){
						 //trace.addInfo(" error in tender summary floatstart amount");
					}
					
					NamedNodeMap attrSales=tsPIList.item(cnt_tsPIList).getAttributes();
					
					for(int attrCnt=0;attrCnt<attrSales.getLength();attrCnt++){
						
						Node attrvalue=attrSales.item(attrCnt);
						
						if(attrvalue.getNodeName().equals("ExternalTenderType")){
							externalTenderTypeValue=attrvalue.getNodeValue();
						}
					}
					
					String item_TSS4="<item>"+
"<RETAILSTOREID>"+RetailStoreID+"</RETAILSTOREID>"+
" <BUSINESSDAYDATE>"+BusinessDayDate+"</BUSINESSDAYDATE>"+
" <TRANSACTIONTYPECODE>1302</TRANSACTIONTYPECODE> "+        
" <WORKSTATIONID>"+WorkstationID+"</WORKSTATIONID>"+
" <TRANSACTIONSEQUENCENUMBER>"+SequenceNumber+"_"+(bscmfmCount)+"</TRANSACTIONSEQUENCENUMBER>"+
"<FINANCIALSEQUENCENUMBER></FINANCIALSEQUENCENUMBER>"+        
"<FIELDGROUP>FINTRANS</FIELDGROUP>"+
"<FIELDNAME>PAYIS</FIELDNAME>"+
"<FIELDVALUE>"+tsSLAmount+"</FIELDVALUE>"+
"<EXTERNALTENDERTYPE>"+externalTenderTypeValue+"</EXTERNALTENDERTYPE>"+
"</item>";
					
					endResult_FMEXT.append(item_TSS4);
				}
				
				}catch(Exception e){
					
				}
				
				//end of for PayIs
				
//start of FloatStart
				
				try
				{
				NodeList tsPIList=((Element)tenderSummaryList.item(tenderSummaryCnt)).getElementsByTagName("FloatStart");
				for(int cnt_tsPIList=0;cnt_tsPIList<tsPIList.getLength();cnt_tsPIList++){
					 tsSLAmount="";
					 externalTenderTypeValue="";
					
					try
					{
						tsSLAmount=((Element)(tsPIList.item(cnt_tsPIList))).getElementsByTagName("Amount").item(0).getFirstChild().getTextContent();
						
					}catch(Exception e){
						 //trace.addInfo(" error in tender summary floatstart amount");
					}
					
					NamedNodeMap attrSales=tsPIList.item(cnt_tsPIList).getAttributes();
					
					for(int attrCnt=0;attrCnt<attrSales.getLength();attrCnt++){
						
						Node attrvalue=attrSales.item(attrCnt);
						
						if(attrvalue.getNodeName().equals("ExternalTenderType")){
							externalTenderTypeValue=attrvalue.getNodeValue();
						}
					}
					
					String item_TSS4="<item>"+
"<RETAILSTOREID>"+RetailStoreID+"</RETAILSTOREID>"+
" <BUSINESSDAYDATE>"+BusinessDayDate+"</BUSINESSDAYDATE>"+
" <TRANSACTIONTYPECODE>1302</TRANSACTIONTYPECODE> "+        
" <WORKSTATIONID>"+WorkstationID+"</WORKSTATIONID>"+
" <TRANSACTIONSEQUENCENUMBER>"+SequenceNumber+"_"+(bscmfmCount)+"</TRANSACTIONSEQUENCENUMBER>"+
"<FINANCIALSEQUENCENUMBER></FINANCIALSEQUENCENUMBER>"+        
"<FIELDGROUP>FINTRANS</FIELDGROUP>"+
"<FIELDNAME>FLOATSTART</FIELDNAME>"+
"<FIELDVALUE>"+tsSLAmount+"</FIELDVALUE>"+
"<EXTERNALTENDERTYPE>"+externalTenderTypeValue+"</EXTERNALTENDERTYPE>"+
"</item>";
					
					endResult_FMEXT.append(item_TSS4);
				}
				
				}catch(Exception e){
					
				}
				
				//end of for FloatStart
				
//start of Loan
				
				try
				{
				NodeList tsPIList=((Element)tenderSummaryList.item(tenderSummaryCnt)).getElementsByTagName("Loan");
				for(int cnt_tsPIList=0;cnt_tsPIList<tsPIList.getLength();cnt_tsPIList++){
					 tsSLAmount="";
					 externalTenderTypeValue="";
					
					try
					{
						tsSLAmount=((Element)(tsPIList.item(cnt_tsPIList))).getElementsByTagName("Amount").item(0).getFirstChild().getTextContent();
						
					}catch(Exception e){
						 //trace.addInfo(" error in tender summary floatstart amount");
					}
					
					NamedNodeMap attrSales=tsPIList.item(cnt_tsPIList).getAttributes();
					
					for(int attrCnt=0;attrCnt<attrSales.getLength();attrCnt++){
						
						Node attrvalue=attrSales.item(attrCnt);
						
						if(attrvalue.getNodeName().equals("ExternalTenderType")){
							externalTenderTypeValue=attrvalue.getNodeValue();
						}
					}
					
					String item_TSS4="<item>"+
"<RETAILSTOREID>"+RetailStoreID+"</RETAILSTOREID>"+
" <BUSINESSDAYDATE>"+BusinessDayDate+"</BUSINESSDAYDATE>"+
" <TRANSACTIONTYPECODE>1302</TRANSACTIONTYPECODE> "+        
" <WORKSTATIONID>"+WorkstationID+"</WORKSTATIONID>"+
" <TRANSACTIONSEQUENCENUMBER>"+SequenceNumber+"_"+(bscmfmCount)+"</TRANSACTIONSEQUENCENUMBER>"+
"<FINANCIALSEQUENCENUMBER></FINANCIALSEQUENCENUMBER>"+        
"<FIELDGROUP>FINTRANS</FIELDGROUP>"+
"<FIELDNAME>LOAN</FIELDNAME>"+
"<FIELDVALUE>"+tsSLAmount+"</FIELDVALUE>"+
"<EXTERNALTENDERTYPE>"+externalTenderTypeValue+"</EXTERNALTENDERTYPE>"+
"</item>";
					
					endResult_FMEXT.append(item_TSS4);
				}
				
				}catch(Exception e){
					
				}
				
				//end of for Loan
				
//start of Pickup
				
				try
				{
				NodeList tsPIList=((Element)tenderSummaryList.item(tenderSummaryCnt)).getElementsByTagName("Pickup");
				for(int cnt_tsPIList=0;cnt_tsPIList<tsPIList.getLength();cnt_tsPIList++){
					 tsSLAmount="";
					 externalTenderTypeValue="";
					
					try
					{
						tsSLAmount=((Element)(tsPIList.item(cnt_tsPIList))).getElementsByTagName("Amount").item(0).getFirstChild().getTextContent();
						
					}catch(Exception e){
						 //trace.addInfo(" error in tender summary floatstart amount");
					}
					
					NamedNodeMap attrSales=tsPIList.item(cnt_tsPIList).getAttributes();
					
					for(int attrCnt=0;attrCnt<attrSales.getLength();attrCnt++){
						
						Node attrvalue=attrSales.item(attrCnt);
						
						if(attrvalue.getNodeName().equals("ExternalTenderType")){
							externalTenderTypeValue=attrvalue.getNodeValue();
						}
					}
					
					String item_TSS4="<item>"+
"<RETAILSTOREID>"+RetailStoreID+"</RETAILSTOREID>"+
" <BUSINESSDAYDATE>"+BusinessDayDate+"</BUSINESSDAYDATE>"+
" <TRANSACTIONTYPECODE>1302</TRANSACTIONTYPECODE> "+        
" <WORKSTATIONID>"+WorkstationID+"</WORKSTATIONID>"+
" <TRANSACTIONSEQUENCENUMBER>"+SequenceNumber+"_"+(bscmfmCount)+"</TRANSACTIONSEQUENCENUMBER>"+
"<FINANCIALSEQUENCENUMBER></FINANCIALSEQUENCENUMBER>"+        
"<FIELDGROUP>FINTRANS</FIELDGROUP>"+
"<FIELDNAME>PICKUP</FIELDNAME>"+
"<FIELDVALUE>"+tsSLAmount+"</FIELDVALUE>"+
"<EXTERNALTENDERTYPE>"+externalTenderTypeValue+"</EXTERNALTENDERTYPE>"+
"</item>";
					
					endResult_FMEXT.append(item_TSS4);
				}
				
				}catch(Exception e){
					
				}
				
				//end of for Pickup
				
//start of FloatEnd
				
				try
				{
				NodeList tsPIList=((Element)tenderSummaryList.item(tenderSummaryCnt)).getElementsByTagName("FloatEnd");
				for(int cnt_tsPIList=0;cnt_tsPIList<tsPIList.getLength();cnt_tsPIList++){
					 tsSLAmount="";
					 externalTenderTypeValue="";
					
					try
					{
						tsSLAmount=((Element)(tsPIList.item(cnt_tsPIList))).getElementsByTagName("Amount").item(0).getFirstChild().getTextContent();
						
					}catch(Exception e){
						 //trace.addInfo(" error in tender summary floatstart amount");
					}
					
					NamedNodeMap attrSales=tsPIList.item(cnt_tsPIList).getAttributes();
					
					for(int attrCnt=0;attrCnt<attrSales.getLength();attrCnt++){
						
						Node attrvalue=attrSales.item(attrCnt);
						
						if(attrvalue.getNodeName().equals("ExternalTenderType")){
							externalTenderTypeValue=attrvalue.getNodeValue();
						}
					}
					
					String item_TSS4="<item>"+
"<RETAILSTOREID>"+RetailStoreID+"</RETAILSTOREID>"+
" <BUSINESSDAYDATE>"+BusinessDayDate+"</BUSINESSDAYDATE>"+
" <TRANSACTIONTYPECODE>1302</TRANSACTIONTYPECODE> "+        
" <WORKSTATIONID>"+WorkstationID+"</WORKSTATIONID>"+
" <TRANSACTIONSEQUENCENUMBER>"+SequenceNumber+"_"+(bscmfmCount)+"</TRANSACTIONSEQUENCENUMBER>"+
"<FINANCIALSEQUENCENUMBER></FINANCIALSEQUENCENUMBER>"+        
"<FIELDGROUP>FINTRANS</FIELDGROUP>"+
"<FIELDNAME>FLOATEND</FIELDNAME>"+
"<FIELDVALUE>"+tsSLAmount+"</FIELDVALUE>"+
"<EXTERNALTENDERTYPE>"+externalTenderTypeValue+"</EXTERNALTENDERTYPE>"+
"</item>";
					
					endResult_FMEXT.append(item_TSS4);
				}
				
				}catch(Exception e){
					
				}
				
				//end of for FloatEnd
				
				
				
				////System.out.println("<TRANSACTIONSEQUENCENUMBER>"+SequenceNumber+"_"+(bscmfmCount)+"</TRANSACTIONSEQUENCENUMBER>");
				String item ="<item><RETAILSTOREID>"+RetailStoreID+"</RETAILSTOREID>"+
"<BUSINESSDAYDATE>"+BusinessDayDate+"</BUSINESSDAYDATE>"+
"<WORKSTATIONID>"+WorkstationID+"</WORKSTATIONID>"+
"<TRANSACTIONSEQUENCENUMBER>"+SequenceNumber+"_"+(bscmfmCount)+"</TRANSACTIONSEQUENCENUMBER>"+         
"<FIELDGROUP>POSHDR</FIELDGROUP><FIELDNAME>SHIFTNO</FIELDNAME>"+
"<FIELDVALUE>"+ShiftNo+"</FIELDVALUE>"+
"<TRANSACTIONTYPECODE>1302</TRANSACTIONTYPECODE></item>";
				
				endResult_trxext.append(item);
				
				String item1 ="<item><RETAILSTOREID>"+RetailStoreID+"</RETAILSTOREID>"+
		         "<BUSINESSDAYDATE>"+BusinessDayDate+"</BUSINESSDAYDATE>"+
		        "<WORKSTATIONID>"+WorkstationID+"</WORKSTATIONID>"+
		        "<TRANSACTIONSEQUENCENUMBER>"+SequenceNumber+"_"+(bscmfmCount)+"</TRANSACTIONSEQUENCENUMBER>"+         
		         "<FIELDGROUP>POSLOG</FIELDGROUP><FIELDNAME>FNAME</FIELDNAME>"+
		         "<FIELDVALUE>"+fileName+"</FIELDVALUE>"+
		           "<TRANSACTIONTYPECODE>1302</TRANSACTIONTYPECODE></item>";
													
									endResult_trxext.append(item1);
									
									String item_trx ="<item><RETAILSTOREID>"+RetailStoreID+"</RETAILSTOREID>"+
							         "<BUSINESSDAYDATE>"+BusinessDayDate+"</BUSINESSDAYDATE>"+
							          "<TRANSACTIONTYPECODE>1302</TRANSACTIONTYPECODE>"+           
							        "<WORKSTATIONID>"+WorkstationID+"</WORKSTATIONID>"+
							         "<TRANSACTIONSEQUENCENUMBER>"+SequenceNumber+"_"+(bscmfmCount)+"</TRANSACTIONSEQUENCENUMBER>"+
							         "<BEGINDATETIMESTAMP>"+BeginDateTime+"</BEGINDATETIMESTAMP>"+
							         "<ENDDATETIMESTAMP>"+EndDateTime+"</ENDDATETIMESTAMP>"+
							         "<DEPARTMENT></DEPARTMENT>"+
							         "<OPERATORQUALIFIER></OPERATORQUALIFIER>"+
							         "<OPERATORID>"+OperatorID+"</OPERATORID>"+
							         "<TRANSACTIONCURRENCY>"+CurrencyCode+"</TRANSACTIONCURRENCY>"+
							         "<TRANSACTIONCURRENCY_ISO>"+CurrencyCode+"</TRANSACTIONCURRENCY_ISO>"+
							         "<PARTNERQUALIFIER/><PARTNERID/></item>";  
														
														endResult_trx.append(item_trx);
														
														String item_fm=" <item>"+
													       "<RETAILSTOREID>"+RetailStoreID+"</RETAILSTOREID>"+
													        " <BUSINESSDAYDATE>"+BusinessDayDate+"</BUSINESSDAYDATE>"+
													         "<TRANSACTIONTYPECODE>1302</TRANSACTIONTYPECODE> "+        
													         "<WORKSTATIONID>"+WorkstationID+"</WORKSTATIONID>"+
													         "<TRANSACTIONSEQUENCENUMBER>"+SequenceNumber+"_"+(bscmfmCount)+"</TRANSACTIONSEQUENCENUMBER>"+
													         "<FINANCIALSEQUENCENUMBER></FINANCIALSEQUENCENUMBER>"+
													         "<FINANCIALTYPECODE>"+finCode+"</FINANCIALTYPECODE>"+
													        " <ACCOUNTID>"+RetailStoreID+"</ACCOUNTID>"+
													         "<ACCOUNTASSIGNMENTOBJECT>"+attrTypeCode+"</ACCOUNTASSIGNMENTOBJECT>"+
													         "<AMOUNT>"+Amount+"</AMOUNT>"+
													         "<FINANCIALCURRENCY>"+CurrencyCode+"</FINANCIALCURRENCY>"+
													         "<FINANCIALCURRENCY_ISO>"+CurrencyCode+"</FINANCIALCURRENCY_ISO>"+
													         "<REFERERENCEID></REFERERENCEID>"+
													     " </item>";
														endResult_FM.append(item_fm);
														Amount="";
						}
									
					}//end os session for loop
				
				}catch(Exception e){
					
				}

				
				//end of session settle based on tender summary
				
				//start of Pickup
				
				try
				{
				NodeList pickupList=((Element)bscmTransactionList.item(cntBscm)).getElementsByTagName("Pickup");
								
				String finCode="";
				for(int cntPickup=0;cntPickup<pickupList.getLength();cntPickup++){
					
					
					
					NodeList accountMovementList=((Element)pickupList.item(cntPickup)).getElementsByTagName("AccountMovement");
					int tmpCount=1;
					
					for(int accountMovementCnt=0;accountMovementCnt<accountMovementList.getLength();accountMovementCnt++){
						
						// bscmfmCount=bscmfmCount+1;
						NamedNodeMap attr=accountMovementList.item(accountMovementCnt).getAttributes();
						
						 ShiftNo="";
						String szAccountCode="";
						String Amount="";
						String attrTypeCode="";
						String szBusinessDate="";
						try
						{
										
							ShiftNo=((Element)(accountMovementList.item(accountMovementCnt))).getElementsByTagName("ShiftNo").item(0).getFirstChild().getTextContent();
							
						
						}catch(Exception e){
							 ShiftNo="";
						}
						
						try
						{
										
							
							Amount=((Element)(accountMovementList.item(accountMovementCnt))).getElementsByTagName("Amount").item(0).getFirstChild().getTextContent();
							
							
						
						}catch(Exception e){
							 //trace.addInfo("exception in payout  accountcode or amount");
						}
						
						try
						{
										
							
						
							szAccountCode=((Element)(accountMovementList.item(accountMovementCnt))).getElementsByTagName("szAccountCode").item(0).getFirstChild().getTextContent();
							
						
						}catch(Exception e){
							 //trace.addInfo("exception in payout  accountcode or amount");
						}
						
						try
						{
										
							
							szBusinessDate=((Element)(accountMovementList.item(accountMovementCnt))).getElementsByTagName("szBusinessDate").item(0).getFirstChild().getTextContent();
							
							
						
						}catch(Exception e){
							 //trace.addInfo("exception in payout szBusinessDate");
						}
						
					for(int attrCnt=0;attrCnt<attr.getLength();attrCnt++){
						
						Node attrvalue=attr.item(attrCnt);
						
						
						if(attrvalue.getNodeName().equals("ExternalTenderType")){
							
							finCode="Pickup-"+attrvalue.getNodeValue();
							
						}
						
						if(attrvalue.getNodeValue().equals("Credit")){
							bscmfmCount=bscmfmCount+1;
							String item ="<item><RETAILSTOREID>"+RetailStoreID+"</RETAILSTOREID>"+
         "<BUSINESSDAYDATE>"+BusinessDayDate+"</BUSINESSDAYDATE>"+
        "<WORKSTATIONID>"+WorkstationID+"</WORKSTATIONID>"+
        "<TRANSACTIONSEQUENCENUMBER>"+SequenceNumber+"_"+(bscmfmCount)+"</TRANSACTIONSEQUENCENUMBER>"+         
         "<FIELDGROUP>POSHDR</FIELDGROUP><FIELDNAME>SHIFTNO</FIELDNAME>"+
         "<FIELDVALUE>"+ShiftNo+"</FIELDVALUE>"+
           "<TRANSACTIONTYPECODE>1302</TRANSACTIONTYPECODE></item>";
							
							endResult_trxext.append(item);
							
							String item1 ="<item><RETAILSTOREID>"+RetailStoreID+"</RETAILSTOREID>"+
					         "<BUSINESSDAYDATE>"+BusinessDayDate+"</BUSINESSDAYDATE>"+
					        "<WORKSTATIONID>"+WorkstationID+"</WORKSTATIONID>"+
					        "<TRANSACTIONSEQUENCENUMBER>"+SequenceNumber+"_"+(bscmfmCount)+"</TRANSACTIONSEQUENCENUMBER>"+         
					         "<FIELDGROUP>POSLOG</FIELDGROUP><FIELDNAME>FNAME</FIELDNAME>"+
					         "<FIELDVALUE>"+fileName+"</FIELDVALUE>"+
					           "<TRANSACTIONTYPECODE>1302</TRANSACTIONTYPECODE></item>";
												
												endResult_trxext.append(item1);
							
							String item_trx ="<item><RETAILSTOREID>"+RetailStoreID+"</RETAILSTOREID>"+
					         "<BUSINESSDAYDATE>"+BusinessDayDate+"</BUSINESSDAYDATE>"+
					          "<TRANSACTIONTYPECODE>1302</TRANSACTIONTYPECODE>"+           
					        "<WORKSTATIONID>"+WorkstationID+"</WORKSTATIONID>"+
					         "<TRANSACTIONSEQUENCENUMBER>"+SequenceNumber+"_"+(bscmfmCount)+"</TRANSACTIONSEQUENCENUMBER>"+
					         "<BEGINDATETIMESTAMP>"+BeginDateTime+"</BEGINDATETIMESTAMP>"+
					         "<ENDDATETIMESTAMP>"+EndDateTime+"</ENDDATETIMESTAMP>"+
					         "<DEPARTMENT></DEPARTMENT>"+
					         "<OPERATORQUALIFIER></OPERATORQUALIFIER>"+
					         "<OPERATORID>"+OperatorID+"</OPERATORID>"+
					         "<TRANSACTIONCURRENCY>"+CurrencyCode+"</TRANSACTIONCURRENCY>"+
					         "<TRANSACTIONCURRENCY_ISO>"+CurrencyCode+"</TRANSACTIONCURRENCY_ISO>"+
					         "<PARTNERQUALIFIER/><PARTNERID/></item>";  
												
												endResult_trx.append(item_trx);
												
												
												String item_fmext ="<item>"+
											      "<RETAILSTOREID>"+RetailStoreID+"</RETAILSTOREID>"+
											         "<BUSINESSDAYDATE>"+BusinessDayDate+"</BUSINESSDAYDATE>"+
											         "<TRANSACTIONTYPECODE>1302</TRANSACTIONTYPECODE>"+         
											         "<WORKSTATIONID>"+WorkstationID+"</WORKSTATIONID>"+
											         "<TRANSACTIONSEQUENCENUMBER>"+SequenceNumber+"_"+(bscmfmCount)+"</TRANSACTIONSEQUENCENUMBER>"+
											         "<FINANCIALSEQUENCENUMBER></FINANCIALSEQUENCENUMBER>"+        
											         "<FIELDGROUP>ZWFIB</FIELDGROUP>"+
											        " <FIELDNAME>BANKDATUM</FIELDNAME>"+
											         "<FIELDVALUE>"+szBusinessDate+"</FIELDVALUE>"+
											         
											      "</item>";
																							
																							endResult_FMEXT.append(item_fmext);
																							
																							String item_fm=" <item>"+
																						       "<RETAILSTOREID>"+RetailStoreID+"</RETAILSTOREID>"+
																						        " <BUSINESSDAYDATE>"+BusinessDayDate+"</BUSINESSDAYDATE>"+
																						         "<TRANSACTIONTYPECODE>1302</TRANSACTIONTYPECODE> "+        
																						         "<WORKSTATIONID>"+WorkstationID+"</WORKSTATIONID>"+
																						         "<TRANSACTIONSEQUENCENUMBER>"+SequenceNumber+"_"+(bscmfmCount)+"</TRANSACTIONSEQUENCENUMBER>"+
																						         "<FINANCIALSEQUENCENUMBER></FINANCIALSEQUENCENUMBER>"+
																						         "<FINANCIALTYPECODE>"+finCode+"</FINANCIALTYPECODE>"+
																						        " <ACCOUNTID>"+RetailStoreID+"</ACCOUNTID>"+
																						         "<ACCOUNTASSIGNMENTOBJECT>"+attrTypeCode+"</ACCOUNTASSIGNMENTOBJECT>"+
																						         "<AMOUNT>"+Amount+"</AMOUNT>"+
																						         "<FINANCIALCURRENCY>"+CurrencyCode+"</FINANCIALCURRENCY>"+
																						         "<FINANCIALCURRENCY_ISO>"+CurrencyCode+"</FINANCIALCURRENCY_ISO>"+
																						         "<REFERERENCEID></REFERERENCEID>"+
																						     " </item>";
																							endResult_FM.append(item_fm);
												tmpCount=tmpCount+1;

						}
					}//end of for
					
					
					
					
					}
					
				} //end of pick up
				}catch(Exception e){
					
				}
				
			
				
				
				
			}
			salesTotalCount= SequenceNumber+"_"+(bscmfmCount-1);
			}//bscm does not exists
			
			else{// for normal transactions
				
				
				String item="";
				String item_trx="";
				String lCustGenderAndAge="";
				String lCustRace="";
				String TENDERAMOUNT="";
					
					try
					{
						
									
						ShiftNo=((Element)(transactionList.item(cntTrx))).getElementsByTagName("ShiftNo").item(0).getFirstChild().getTextContent();
						
					
					}catch(Exception e){
						 ShiftNo="";
					}
					
					
					if(transactionList.item(cntTrx).hasAttributes())
					{
					NamedNodeMap attrTrx=transactionList.item(cntTrx).getAttributes();
					
for(int attrCnt=0;attrCnt<attrTrx.getLength();attrCnt++){
						
						Node attrvalue=attrTrx.item(attrCnt);
						
						
						if(attrvalue.getNodeValue().equals("true")){
							TransactionTypeCode="1105";
						}
}
					
					}// attributes if loop ended
					
					
					/**
					try{
					
					NodeList eODConfirmationList=((Element)transactionList.item(cntTrx)).getElementsByTagName("EODConfirmation");
					
					if(eODConfirmationList.getLength()>0){
						TransactionTypeCode="1122";
					}
					
					}catch(Exception e){
						 //trace.addInfo("EODConfirmation does not exists");
					}
					**/
					
					try{
						
						NodeList controlTransactionList=((Element)transactionList.item(cntTrx)).getElementsByTagName("ControlTransaction");
						
						for(int cnt_CntTrxList=0;cnt_CntTrxList<controlTransactionList.getLength();cnt_CntTrxList++){
							
							NodeList operatorSignOffList=((Element)controlTransactionList.item(cnt_CntTrxList)).getElementsByTagName("OperatorSignOff");
							
							if(operatorSignOffList.getLength()>0){
								TransactionTypeCode="1102";
							}
							
	NodeList operatorSignOnList=((Element)controlTransactionList.item(cnt_CntTrxList)).getElementsByTagName("OperatorSignOn");
							
							if(operatorSignOnList.getLength()>0){
								TransactionTypeCode="1101";
							}
							
	NodeList trainingModeEndList=((Element)controlTransactionList.item(cnt_CntTrxList)).getElementsByTagName("TrainingModeEnd");
							
							if(trainingModeEndList.getLength()>0){
								TransactionTypeCode="1203";
							}
							
	NodeList trainingModeStartList=((Element)controlTransactionList.item(cnt_CntTrxList)).getElementsByTagName("TrainingModeStart");
							
							if(trainingModeStartList.getLength()>0){
								TransactionTypeCode="1201";
							}
							
							
							for(int cnt_OSOList=0;cnt_OSOList<operatorSignOffList.getLength();cnt_OSOList++){
								String EndOfDay="";
								try{
									NodeList eodList=((Element)(operatorSignOffList.item(cnt_OSOList))).getElementsByTagName("EndOfDay");
									if(eodList.getLength()>0)
									{
										TransactionTypeCode="1124";
									}
									//EndOfDay=((Element)(operatorSignOffList.item(cnt_OSOList))).getElementsByTagName("EndOfDay").item(0).getFirstChild().getTextContent();
									
								}catch(Exception e){
									 //trace.addInfo("EOD field does not exists");
								}
							}
							
						}
						
						}catch(Exception e){
							 //trace.addInfo("ControlTransaction does not exists or error while fetching OperatorSignOff or OperatorSignOn or TrainingModeEnd or TrainingModeStart");
						}
						
						try{
							
							NodeList tenderControlTransactionList=((Element)transactionList.item(cntTrx)).getElementsByTagName("TenderControlTransaction");
							
							for(int cnt_tndTrxList=0;cnt_tndTrxList<tenderControlTransactionList.getLength();cnt_tndTrxList++){
								
								NodeList tenderLoanList=((Element)tenderControlTransactionList.item(cnt_tndTrxList)).getElementsByTagName("TenderLoan");
								
								if(tenderLoanList.getLength()>0){
									TransactionTypeCode="1302";
									
									for(int cnt_tenderLoanList=0;cnt_tenderLoanList<tenderLoanList.getLength();cnt_tenderLoanList++){
										
										NodeList totalsList=(((Element)tenderLoanList.item(cnt_tenderLoanList)).getElementsByTagName("Totals"));
										String totalAmount="";
										String currencyTA="";
										for(int cnt_totalsList=0;cnt_totalsList<totalsList.getLength();cnt_totalsList++)
										{
										
										NamedNodeMap attrLI=totalsList.item(cnt_totalsList).getAttributes();
										
										
										
										try{
											totalAmount=(((Element)totalsList.item(cnt_totalsList)).getElementsByTagName("Amount")).item(0).getTextContent();
											NamedNodeMap attrTLA=(((Element)totalsList.item(cnt_totalsList)).getElementsByTagName("Amount")).item(0).getAttributes();
											
											for(int attrCnt=0;attrCnt<attrTLA.getLength();attrCnt++){
												
												Node attrvalue=attrLI.item(attrCnt);
												
												
												if(attrvalue.getNodeName().equals("Currency")){
													currencyTA=attrvalue.getNodeValue();
												}
											}
										}catch(Exception e){
											 //trace.addInfo("exception in tenderloan amount");
										}
										}
										
										String item_FM= " <item>"+
       " <RETAILSTOREID>"+RetailStoreID+"</RETAILSTOREID>"+
        "  <BUSINESSDAYDATE>"+BusinessDayDate+"</BUSINESSDAYDATE>"+
        "  <TRANSACTIONTYPECODE>1302</TRANSACTIONTYPECODE>"+         
        "  <WORKSTATIONID>"+WorkstationID+"</WORKSTATIONID>"+
        "  <TRANSACTIONSEQUENCENUMBER>"+SequenceNumber+"</TRANSACTIONSEQUENCENUMBER>"+
        "  <FINANCIALSEQUENCENUMBER></FINANCIALSEQUENCENUMBER>"+
        "  <FINANCIALTYPECODE>3304</FINANCIALTYPECODE>"+
        "  <ACCOUNTID></ACCOUNTID>"+
        "  <ACCOUNTASSIGNMENTOBJECT></ACCOUNTASSIGNMENTOBJECT>"+
        "  <AMOUNT>"+totalAmount+"</AMOUNT>"+
        "  <FINANCIALCURRENCY>"+currencyTA+"</FINANCIALCURRENCY>"+
        "  <FINANCIALCURRENCY_ISO>"+currencyTA+"</FINANCIALCURRENCY_ISO>"+
        "  <REFERERENCEID></REFERERENCEID>"+
      " </item>";
										endResult_FM.append(item_FM);
									}//end of tender loan for
								}
								
							}
							
							}catch(Exception e){
								 //trace.addInfo("TenderControlTransaction does not exists or error while fetching TenderLoan");
							}
					
					
					NodeList retailTransactionList=((Element)transactionList.item(cntTrx)).getElementsByTagName("RetailTransaction");
					
					String MOLCodeTender="";
					String TotalIssueSticker="";
                    
                    String ActualIssuedSticker="";
                    String BCardNumber="";
                    
					for(int cntreTrx=0;cntreTrx<retailTransactionList.getLength();cntreTrx++){
						
						try
						{
						//start of bacrd
							System.out.println("checking bcard ");
						NodeList bcardList=((Element)retailTransactionList.item(cntreTrx)).getElementsByTagName("BCARD");
						
						if(bcardList.getLength()>0)
						{
							
						for(int cnt_bcardList=0;cnt_bcardList<bcardList.getLength();cnt_bcardList++)
						{
						try
						{
							BCardNumber=(((Element)bcardList.item(cnt_bcardList)).getElementsByTagName("BCardNumber")).item(0).getTextContent();
						}catch(Exception e){
							 //trace.addInfo(" error while fetching EarnedPointOfItem");
						}
						
						System.out.println("BCardNumber is"+BCardNumber);
						}
						}
						
						//end of bcard
						}catch(Exception e){
							
						}
						
						NodeList lineItemList=((Element)retailTransactionList.item(cntreTrx)).getElementsByTagName("LineItem");
						
						String itemVoidTrxTypeCode="";
						for(int cnt_Litem=0;cnt_Litem<lineItemList.getLength();cnt_Litem++){
							
							String LineSequenceNumber="";
							String entryMethodValue="";
							String tenderTypeCode="";
							String tenderType="";
							String externalTenderType="";
							
							String voidFlag="false";
							String szAccountID="";
							String lEarnedPoint = "";
							
							
							
							try{
								
								LineSequenceNumber=(((Element)lineItemList.item(cnt_Litem)).getElementsByTagName("SequenceNumber")).item(0).getTextContent();
								
								NamedNodeMap attrLI=lineItemList.item(cnt_Litem).getAttributes();
								
								
								
								
							for(int attrCnt=0;attrCnt<attrLI.getLength();attrCnt++){
								
								Node attrvalue=attrLI.item(attrCnt);
								
								
								if(attrvalue.getNodeName().equals("EntryMethod")){
									entryMethodValue=attrvalue.getNodeValue();
								}else if(attrvalue.getNodeName().equals("VoidFlag")){
									voidFlag=attrvalue.getNodeValue();
								}
							}
								
								//////////System.out.println("LineSequenceNumber is"+LineSequenceNumber);
							
							
							
								
								NodeList bpcList=((Element)lineItemList.item(cnt_Litem)).getElementsByTagName("BPointCalculation");
								if(bpcList.getLength()>0){
									for(int cnt_bpcList=0;cnt_bpcList<bpcList.getLength();cnt_bpcList++){
										try
										{
										szAccountID = (((Element)bpcList.item(cnt_bpcList)).getElementsByTagName("szAccountID")).item(0).getTextContent();
										
										}catch(Exception e){
											 //trace.addInfo("error in fetching szAccountID from BPointCalculation");
										}
										
										try
										{
										
										lEarnedPoint=(((Element)bpcList.item(cnt_bpcList)).getElementsByTagName("lEarnedPoint")).item(0).getTextContent();
										
										}catch(Exception e){
											 //trace.addInfo("error in fetching lEarnedPoint from BPointCalculation");
										}
										
										String item_bpcl="<item>"+
     "<RETAILSTOREID>"+RetailStoreID+"</RETAILSTOREID>"+
        " <BUSINESSDAYDATE>"+BusinessDayDate+"</BUSINESSDAYDATE>"+
         "<TRANSACTIONTYPECODE>1001</TRANSACTIONTYPECODE>"+
         "<WORKSTATIONID>"+WorkstationID+"</WORKSTATIONID>"+
         "<TRANSACTIONSEQUENCENUMBER>"+SequenceNumber+"</TRANSACTIONSEQUENCENUMBER>"+         
         "<LOYALTYSEQUENCENUMBER>"+LineSequenceNumber+"</LOYALTYSEQUENCENUMBER>"+
        "<CUSTOMERCARDNUMBER>"+szAccountID+"</CUSTOMERCARDNUMBER>"+
        " <LOYALTYPOINTSAWARDED>"+lEarnedPoint+"</LOYALTYPOINTSAWARDED>"+       
         "<LOYALTYPROGRAMID></LOYALTYPROGRAMID>"+
         "<LOYALTYPROGRAMID/>"+
         "<ELIGIBLEAMOUNT/>"+
         "<ELIGIBLEQUANTITY/>"+
         "<ELIGIBLEQUANTITYUOM/>"+
         "<ELIGIBLEQUANTITYUOM_ISO/>"+
         "<LOYALTYPOINTSREDEEMED/>"+
         "<LOYALTYPOINTSTOTAL/>"+
         "<CUSTOMERCARDHOLDERNAME/>"+
         "<CUSTOMERCARDTYPE/>"+
         "<CUSTOMERCARDVALIDFROM/>"+
         "<CUSTOMERCARDVALIDTO/>"+
      "</item>";
										endResult_TrxLylt.append(item_bpcl);
									}
									
								}// end of bpcl
								
								
								
								NodeList saleList=((Element)lineItemList.item(cnt_Litem)).getElementsByTagName("Sale");
								if(saleList.getLength()>0){
									
									varSaleOrReturn="Sale";
									TransactionTypeCode="1001";
									
									for(int cnt_saleList=0;cnt_saleList<saleList.getLength();cnt_saleList++){
										String ItemID=(((Element)saleList.item(cnt_saleList)).getElementsByTagName("ItemID")).item(0).getTextContent();
										String Quantity=(((Element)saleList.item(cnt_saleList)).getElementsByTagName("Quantity")).item(0).getTextContent();
										String ExtendedAmount=(((Element)saleList.item(cnt_saleList)).getElementsByTagName("ExtendedAmount")).item(0).getTextContent();
										String ActualSalesUnitPrice=(((Element)saleList.item(cnt_saleList)).getElementsByTagName("ActualSalesUnitPrice")).item(0).getTextContent();
										String RegularSalesUnitPrice=(((Element)saleList.item(cnt_saleList)).getElementsByTagName("RegularSalesUnitPrice")).item(0).getTextContent();
										String Units=(((Element)saleList.item(cnt_saleList)).getElementsByTagName("Quantity")).item(0).getTextContent();
										
										String EarnedPointOfItem="";
										
										try
										{
										EarnedPointOfItem=(((Element)saleList.item(cnt_saleList)).getElementsByTagName("EarnedPointOfItem")).item(0).getTextContent();
										}catch(Exception e){
											 //trace.addInfo(" error while fetching EarnedPointOfItem");
										}
										
										String PROMOTIONID="";
										
										String MOLCode="NA";
										MOLCodeTender=MOLCode;
										try
										{
											MOLCode=(((Element)saleList.item(cnt_saleList)).getElementsByTagName("MOLCode")).item(0).getTextContent();
											MOLCodeTender=MOLCode;
										}catch(Exception e){
											MOLCode="Exc";
											MOLCodeTender=MOLCode;
											 //trace.addInfo(" error while fetching MOLCode");
										}
										
										
										
										
										
										
										//start of sales tax
										
										NodeList taxList=((Element)lineItemList.item(cnt_Litem)).getElementsByTagName("Tax");
										
										if(taxList.getLength()>0)
										{
										for(int cnt_taxList=0;cnt_taxList<taxList.getLength();cnt_taxList++){
											String taxAmount="";
											
											try
											{
											taxAmount=(((Element)taxList.item(cnt_taxList)).getElementsByTagName("Amount")).item(0).getTextContent();
											}catch(Exception e){
												 //trace.addInfo("error while fetching sales tax amount");
											}
											  String item_salesTax="<item>"+
										         "<RETAILSTOREID>"+RetailStoreID+"</RETAILSTOREID>"+
										         "<BUSINESSDAYDATE>"+BusinessDayDate+"</BUSINESSDAYDATE>"+
										         "<TRANSACTIONTYPECODE>1001</TRANSACTIONTYPECODE>"+
										         "<WORKSTATIONID>"+WorkstationID+"</WORKSTATIONID>"+
										         "<TRANSACTIONSEQUENCENUMBER>"+SequenceNumber+"</TRANSACTIONSEQUENCENUMBER>"+
										         "<RETAILSEQUENCENUMBER>"+LineSequenceNumber+"</RETAILSEQUENCENUMBER>"+
										         "<TAXSEQUENCENUMBER>"+LineSequenceNumber+"</TAXSEQUENCENUMBER>"+
										         "<TAXTYPECODE>MWST</TAXTYPECODE>"+
										         "<TAXAMOUNT>"+taxAmount+"</TAXAMOUNT>"+
										      "</item>";
											  endResult_itemTax.append(item_salesTax);
										}
										}
										
										//end of sales tax
										
										
										NodeList retailPriceModifierList=((Element)lineItemList.item(cnt_Litem)).getElementsByTagName("RetailPriceModifier");
										
										//start of retail price modifier list
										
										if(retailPriceModifierList.getLength()>0){
											for(int cnt_rpmList=0;cnt_rpmList<retailPriceModifierList.getLength();cnt_rpmList++){
												String IndirectDiscountIdentifier="";
												String IndirectDiscountIdentifierFlag="";
												String rpmlSequenceNumber="";
												String Amount="";
												try{
													IndirectDiscountIdentifier=(((Element)retailPriceModifierList.item(cnt_rpmList)).getElementsByTagName("IndirectDiscountIdentifier")).item(0).getTextContent();
													
													if(IndirectDiscountIdentifier.trim().length()>0){
														if(IndirectDiscountIdentifier.equals("1")){
															IndirectDiscountIdentifierFlag="ZBCP";
														}else{
															IndirectDiscountIdentifierFlag="ZBBP";
														}
													}else{
														IndirectDiscountIdentifierFlag="ZBBP";
													}
												}catch(Exception e){
													IndirectDiscountIdentifierFlag="ZBBP";
													////////System.out.println("error fetching IndirectDiscountIdentifie ");
														
												}
													try{
													rpmlSequenceNumber=(((Element)retailPriceModifierList.item(cnt_rpmList)).getElementsByTagName("SequenceNumber")).item(0).getTextContent();
													
													}catch(Exception e){
														////////System.out.println("error fetching IndirectDiscountIdentifierFlag or rpmlSequenceNumber or Amount");
													}
													
													try{
														
														Amount=(((Element)retailPriceModifierList.item(cnt_rpmList)).getElementsByTagName("GrossAmount")).item(0).getTextContent();
														//Amount=(((Element)retailPriceModifierList.item(cnt_rpmList)).getElementsByTagName("Amount")).item(0).getTextContent();
														}catch(Exception e){
															////////System.out.println("error fetching IndirectDiscountIdentifierFlag or rpmlSequenceNumber or Amount");
														}
													
													try
													{
												NodeList priceDerivationRuleList=((Element)retailPriceModifierList.item(cnt_rpmList)).getElementsByTagName("PriceDerivationRule");
												for(int cnt_pdrList=0;cnt_pdrList<retailPriceModifierList.getLength();cnt_pdrList++){
													PROMOTIONID=(((Element)retailPriceModifierList.item(cnt_pdrList)).getElementsByTagName("PriceDerivationRuleID")).item(0).getTextContent();
												}
													}catch(Exception e){
														
													}
												itemVoidTrxTypeCode="1001";
												String item_Sales_RML="<item>"+
         "<RETAILSTOREID>"+RetailStoreID+"</RETAILSTOREID>"+
        " <BUSINESSDAYDATE>"+BusinessDayDate+"</BUSINESSDAYDATE>"+
         "<TRANSACTIONTYPECODE>1001</TRANSACTIONTYPECODE>"+
         "<WORKSTATIONID>"+WorkstationID+"</WORKSTATIONID>"+
         "<TRANSACTIONSEQUENCENUMBER>"+SequenceNumber+"</TRANSACTIONSEQUENCENUMBER>"+
         "<RETAILSEQUENCENUMBER>"+LineSequenceNumber+"</RETAILSEQUENCENUMBER>"+
         "<DISCOUNTSEQUENCENUMBER>"+rpmlSequenceNumber+"</DISCOUNTSEQUENCENUMBER>"+
         "<DISCOUNTTYPECODE>"+IndirectDiscountIdentifierFlag+"</DISCOUNTTYPECODE>"+
         "<DISCOUNTREASONCODE>D001</DISCOUNTREASONCODE>"+
         "<REDUCTIONAMOUNT>-"+Amount+"</REDUCTIONAMOUNT>"+
         "<STOREFINANCIALLEDGERACCOUNTID/>"+
         "<DISCOUNTID/>"+
         "<DISCOUNTIDQUALIFIER/>"+
         "<BONUSBUYID>"+PROMOTIONID+"</BONUSBUYID>"+
         "<OFFERID/>"+
      "</item>";
												endResult_itemDisc.append(item_Sales_RML);
											}
										}// end of retail price modifier list
										
										//start of sales retail line item
										
										String item_sale="<item><RETAILSTOREID>"+RetailStoreID+"</RETAILSTOREID>"+
								         "<BUSINESSDAYDATE>"+BusinessDayDate+"</BUSINESSDAYDATE>"+
								         "<TRANSACTIONTYPECODE>1001</TRANSACTIONTYPECODE>"+
								         "<WORKSTATIONID>"+WorkstationID+"</WORKSTATIONID>"+
								         "<TRANSACTIONSEQUENCENUMBER>"+SequenceNumber+"</TRANSACTIONSEQUENCENUMBER>"+
								         "<RETAILSEQUENCENUMBER>"+LineSequenceNumber+"</RETAILSEQUENCENUMBER>"+
								         "<RETAILTYPECODE>2001</RETAILTYPECODE>"+
								         "<RETAILREASONCODE></RETAILREASONCODE>"+
								         "<ITEMIDQUALIFIER>2</ITEMIDQUALIFIER>"+
								         "<ITEMID>"+ItemID+"</ITEMID>"+
								         "<RETAILQUANTITY>"+Quantity+"</RETAILQUANTITY>"+
								         "<SALESUNITOFMEASURE>EA</SALESUNITOFMEASURE>"+
								         "<SALESUNITOFMEASURE_ISO>EA</SALESUNITOFMEASURE_ISO>"+
								         "<SALESAMOUNT>"+ExtendedAmount+"</SALESAMOUNT>"+
								         "<NORMALSALESAMOUNT>"+ActualSalesUnitPrice+"</NORMALSALESAMOUNT>"+
								         "<COST></COST>"+
								         "<BATCHID></BATCHID>"+
								         "<SERIALNUMBER></SERIALNUMBER>"+
								         "<PROMOTIONID>"+PROMOTIONID+"</PROMOTIONID>"+
								         "<ITEMIDENTRYMETHODCODE>"+entryMethodValue+"</ITEMIDENTRYMETHODCODE>"+
								         "<ACTUALUNITPRICE>"+RegularSalesUnitPrice+"</ACTUALUNITPRICE>"+
								         "<UNITS>"+Units+"</UNITS>"+
								         "<SCANTIME></SCANTIME></item>";
										
										endResult_RL.append(item_sale);// end of sales retail line item
										
										//build item for LINEITEMLOYALTY
										
										if(EarnedPointOfItem.trim().length()>0){
											String item_LIL="<item><RETAILSTOREID>"+RetailStoreID+"</RETAILSTOREID>"+
         "<BUSINESSDAYDATE>"+BusinessDayDate+"</BUSINESSDAYDATE>"+
         "<TRANSACTIONTYPECODE>1001</TRANSACTIONTYPECODE>"+
        " <WORKSTATIONID>"+WorkstationID+"</WORKSTATIONID>"+
         "<TRANSACTIONSEQUENCENUMBER>"+SequenceNumber+"</TRANSACTIONSEQUENCENUMBER>"+
        " <RETAILSEQUENCENUMBER>"+LineSequenceNumber+"</RETAILSEQUENCENUMBER>"+
         "<LOYALTYSEQUENCENUMBER>"+LineSequenceNumber+"</LOYALTYSEQUENCENUMBER>"+
        " <CUSTOMERCARDNUMBER>"+BCardNumber+"</CUSTOMERCARDNUMBER>"+
        " <LOYALTYPOINTSAWARDED>"+EarnedPointOfItem+"</LOYALTYPOINTSAWARDED>     "+  
         "<LOYALTYPROGRAMID></LOYALTYPROGRAMID>"+        
         "<ELIGIBLEAMOUNT/>"+
        " <ELIGIBLEQUANTITY/>"+
        " <ELIGIBLEQUANTITYUOM/>"+
         "<ELIGIBLEQUANTITYUOM_ISO/>"+
        " <LOYALTYPOINTSREDEEMED/>"+
        " <LOYALTYPOINTSTOTAL/>"+
        " <CUSTOMERCARDHOLDERNAME/>"+
        " <CUSTOMERCARDTYPE/>"+
        " <CUSTOMERCARDVALIDFROM/>"+
        " <CUSTOMERCARDVALIDTO/>"+
     " </item>";
											endResult_LIL.append(item_LIL);
											
										}// end of line item loyalty
									}// end of salesList for loop
								}// end of salesList if loop
								
							}catch(Exception e){
								 //trace.addInfo("Sales does not exists");
							}// end of sale try
							
							
							
							try{ 
								
								//start of return try
								NodeList returnList=((Element)lineItemList.item(cnt_Litem)).getElementsByTagName("Return");
								if(returnList.getLength()>0){
									
									varSaleOrReturn="Return";
									
									// start of return if loop
									for(int cnt_Rlist=0;cnt_Rlist<returnList.getLength();cnt_Rlist++){ // start of return for loop
										
										String MOLCode="NA";
										MOLCodeTender=MOLCode;
										try
										{
														
											MOLCode=((Element)(returnList.item(cnt_Rlist))).getElementsByTagName("MOLCode").item(0).getFirstChild().getTextContent();
											MOLCodeTender=MOLCode;
										
										}catch(Exception e){
											 //trace.addInfo("error fetching mol code");
											MOLCode="Exc";
											MOLCodeTender=MOLCode;
										}
										RetailTypeCode="";
										
										 if(varSaleOrReturn.equals("Sale")){
												TransactionTypeCode="1001";
												MOLCodeTender="NA";
												
												
											}else if(varSaleOrReturn.equals("Return"))
											{
												if(MOLCodeTender.equals("6")){
													TransactionTypeCode="1003";
													RetailTypeCode="2802";
													MOLCodeTender="NA";
												}else{
													TransactionTypeCode="1002";
													RetailTypeCode="2801";
													MOLCodeTender="NA";
												}
												
												
												
										}
										
										
										////////System.out.println("**varSaleOrReturn "+varSaleOrReturn +"**MOLCode is"+MOLCode);
										/**
										if(MOLCode.equals("6")){
											TransactionTypeCode="1003";
											RetailTypeCode="2802";
										}else {
											TransactionTypeCode="1002";
											RetailTypeCode="2801";
										}
										**/
										String ItemID=(((Element)returnList.item(cnt_Rlist)).getElementsByTagName("ItemID")).item(0).getTextContent();
										String Quantity=(((Element)returnList.item(cnt_Rlist)).getElementsByTagName("Quantity")).item(0).getTextContent();
										String ExtendedAmount=(((Element)returnList.item(cnt_Rlist)).getElementsByTagName("ExtendedAmount")).item(0).getTextContent();
										String ActualSalesUnitPrice=(((Element)returnList.item(cnt_Rlist)).getElementsByTagName("ActualSalesUnitPrice")).item(0).getTextContent();
										String RegularSalesUnitPrice=(((Element)returnList.item(cnt_Rlist)).getElementsByTagName("RegularSalesUnitPrice")).item(0).getTextContent();
										String Units=(((Element)returnList.item(cnt_Rlist)).getElementsByTagName("Quantity")).item(0).getTextContent();
										
										String PROMOTIONID="";
										NodeList retailPriceModifierList=((Element)lineItemList.item(cnt_Litem)).getElementsByTagName("RetailPriceModifier");
										
			                           
										//start of return tax
										
										NodeList taxList=((Element)lineItemList.item(cnt_Litem)).getElementsByTagName("Tax");
										
										if(taxList.getLength()>0)
										{
										for(int cnt_taxList=0;cnt_taxList<taxList.getLength();cnt_taxList++){
											String taxAmount="";
											
											try
											{
											taxAmount=(((Element)taxList.item(cnt_taxList)).getElementsByTagName("Amount")).item(0).getTextContent();
											}catch(Exception e){
												 //trace.addInfo("error while fetching sales tax amount");
											}
											  String item_salesTax="<item>"+
										         "<RETAILSTOREID>"+RetailStoreID+"</RETAILSTOREID>"+
										         "<BUSINESSDAYDATE>"+BusinessDayDate+"</BUSINESSDAYDATE>"+
										         "<TRANSACTIONTYPECODE>"+TransactionTypeCode+"</TRANSACTIONTYPECODE>"+
										         "<WORKSTATIONID>"+WorkstationID+"</WORKSTATIONID>"+
										         "<TRANSACTIONSEQUENCENUMBER>"+SequenceNumber+"</TRANSACTIONSEQUENCENUMBER>"+
										         "<RETAILSEQUENCENUMBER>"+LineSequenceNumber+"</RETAILSEQUENCENUMBER>"+
										         "<TAXSEQUENCENUMBER>"+LineSequenceNumber+"</TAXSEQUENCENUMBER>"+
										         "<TAXTYPECODE>MWST</TAXTYPECODE>"+
										         "<TAXAMOUNT>-"+taxAmount+"</TAXAMOUNT>"+
										      "</item>";
											  endResult_itemTax.append(item_salesTax);
										}
										}
										
										//end of return tax
										
										
										
										//start of return retail price modifier list
										if(retailPriceModifierList.getLength()>0){
											
											
											for(int cnt_rpmList=0;cnt_rpmList<retailPriceModifierList.getLength();cnt_rpmList++){
												
												String IndirectDiscountIdentifierReturn="";
												String IndirectDiscountIdentifierFlagReturn="";
												String rpmlSequenceNumberReturn="";
												String AmountReturn="";
												try{
													//IndirectDiscountIdentifierReturn=(((Element)retailPriceModifierList.item(cnt_rpmList)).getElementsByTagName("IndirectDiscountIdentifier")).item(0).getTextContent();
													
													
													
													rpmlSequenceNumberReturn=(((Element)retailPriceModifierList.item(cnt_rpmList)).getElementsByTagName("SequenceNumber")).item(0).getTextContent();
													
												
													
													
												}catch(Exception e){
													 //trace.addInfo("error fetching IndirectDiscountIdentifie or IndirectDiscountIdentifierFlag or rpmlSequenceNumber or Amount");
														
												}
												
												try{
													
													AmountReturn=(((Element)retailPriceModifierList.item(cnt_rpmList)).getElementsByTagName("GrossAmount")).item(0).getTextContent();
													//AmountReturn=(((Element)retailPriceModifierList.item(cnt_rpmList)).getElementsByTagName("Amount")).item(0).getTextContent();
													
												}catch(Exception e){
													 //trace.addInfo("error fetching IndirectDiscountIdentifie or IndirectDiscountIdentifierFlag or rpmlSequenceNumber or Amount");
														
												}
												
												try
												{
												NodeList priceDerivationRuleList=((Element)retailPriceModifierList.item(cnt_rpmList)).getElementsByTagName("PriceDerivationRule");
												for(int cnt_pdrList=0;cnt_pdrList<retailPriceModifierList.getLength();cnt_pdrList++){
													PROMOTIONID=(((Element)retailPriceModifierList.item(cnt_pdrList)).getElementsByTagName("PriceDerivationRuleID")).item(0).getTextContent();
												}
												}catch(Exception e){
													
												}
												
												
												String item_Return_RML="<item>"+
										         "<RETAILSTOREID>"+RetailStoreID+"</RETAILSTOREID>"+
										        " <BUSINESSDAYDATE>"+BusinessDayDate+"</BUSINESSDAYDATE>"+
										         "<TRANSACTIONTYPECODE>"+TransactionTypeCode+"</TRANSACTIONTYPECODE>"+
										         "<WORKSTATIONID>"+WorkstationID+"</WORKSTATIONID>"+
										         "<TRANSACTIONSEQUENCENUMBER>"+SequenceNumber+"</TRANSACTIONSEQUENCENUMBER>"+
										         "<RETAILSEQUENCENUMBER>"+LineSequenceNumber+"</RETAILSEQUENCENUMBER>"+
										         "<DISCOUNTSEQUENCENUMBER>"+rpmlSequenceNumberReturn+"</DISCOUNTSEQUENCENUMBER>"+
										         "<DISCOUNTTYPECODE>ZBBP</DISCOUNTTYPECODE>"+
										         "<DISCOUNTREASONCODE>D001</DISCOUNTREASONCODE>"+
										         "<REDUCTIONAMOUNT>"+AmountReturn+"</REDUCTIONAMOUNT>"+
										         "<STOREFINANCIALLEDGERACCOUNTID/>"+
										         "<DISCOUNTID/>"+
										         "<DISCOUNTIDQUALIFIER/>"+
										         "<BONUSBUYID>"+PROMOTIONID+"</BONUSBUYID>"+
										         "<OFFERID/>"+
										      "</item>";
																						endResult_itemDisc.append(item_Return_RML);
											}// end of return retail price modifier list - for loop
										}//end of return retail price modifier list - if loop
										
										
										
										
										//start of return retail line item
										itemVoidTrxTypeCode=TransactionTypeCode;
										String item_sale="<item><RETAILSTOREID>"+RetailStoreID+"</RETAILSTOREID>"+
								         "<BUSINESSDAYDATE>"+BusinessDayDate+"</BUSINESSDAYDATE>"+
								         "<TRANSACTIONTYPECODE>"+TransactionTypeCode+"</TRANSACTIONTYPECODE>"+
								         "<WORKSTATIONID>"+WorkstationID+"</WORKSTATIONID>"+
								         "<TRANSACTIONSEQUENCENUMBER>"+SequenceNumber+"</TRANSACTIONSEQUENCENUMBER>"+
								         "<RETAILSEQUENCENUMBER>"+LineSequenceNumber+"</RETAILSEQUENCENUMBER>"+
								         "<RETAILTYPECODE>"+RetailTypeCode+"</RETAILTYPECODE>"+
								         "<RETAILREASONCODE></RETAILREASONCODE>"+
								         "<ITEMIDQUALIFIER>2</ITEMIDQUALIFIER>"+
								         "<ITEMID>"+ItemID+"</ITEMID>"+
								         "<RETAILQUANTITY>-"+Quantity+"</RETAILQUANTITY>"+
								         "<SALESUNITOFMEASURE>EA</SALESUNITOFMEASURE>"+
								         "<SALESUNITOFMEASURE_ISO>EA</SALESUNITOFMEASURE_ISO>"+
								         "<SALESAMOUNT>-"+ExtendedAmount+"</SALESAMOUNT>"+
								         "<NORMALSALESAMOUNT>-"+ActualSalesUnitPrice+"</NORMALSALESAMOUNT>"+
								         "<COST></COST>"+
								         "<BATCHID></BATCHID>"+
								         "<SERIALNUMBER></SERIALNUMBER>"+
								         "<PROMOTIONID>"+PROMOTIONID+"</PROMOTIONID>"+
								         "<ITEMIDENTRYMETHODCODE>"+entryMethodValue+"</ITEMIDENTRYMETHODCODE>"+
								         "<ACTUALUNITPRICE>"+RegularSalesUnitPrice+"</ACTUALUNITPRICE>"+
								         "<UNITS>"+Units+"</UNITS>"+
								         "<SCANTIME></SCANTIME></item>";
										
										endResult_RL.append(item_sale);
										
										//end of return retail line item
										
										
										
										
									}// end of return for loop 
									
									
								}//start of return if loop
								
							}catch(Exception e){
								 //trace.addInfo("returns does not exists");
							}//end of return try 
							
							
							//start of line item void
							
							if(voidFlag.trim().length()>0){
								
								if(voidFlag.equals("true")){
									
									String item_lineItemVoid="<item>"+
       "<RETAILSTOREID>"+RetailStoreID+"</RETAILSTOREID>"+
         "<BUSINESSDAYDATE>"+BusinessDayDate+"</BUSINESSDAYDATE>"+         
           "<TRANSACTIONTYPECODE>"+TransactionTypeCode+"</TRANSACTIONTYPECODE>"+         
         "<WORKSTATIONID>"+WorkstationID+"</WORKSTATIONID>"+
         "<TRANSACTIONSEQUENCENUMBER>"+SequenceNumber+"</TRANSACTIONSEQUENCENUMBER>"+
         "<RETAILSEQUENCENUMBER>"+LineSequenceNumber+"</RETAILSEQUENCENUMBER>"+
          "<VOIDEDLINE>"+LineSequenceNumber+"</VOIDEDLINE>"+
        "<VOIDFLAG>X</VOIDFLAG>"+
      "</item>";
									endResult_itemVoid.append(item_lineItemVoid);
									
								}
								
							}
							
							//end of line item void
							
							// start of customer profile segment
							
							try{
								NodeList cUSTOMERPROFILEList=((Element)lineItemList.item(cnt_Litem)).getElementsByTagName("CUSTOMER_PROFILE");
								if(cUSTOMERPROFILEList.getLength()>0){
									
									
									
									try{
										lCustGenderAndAge=((Element)(cUSTOMERPROFILEList.item(0))).getElementsByTagName("lCustGenderAndAge").item(0).getFirstChild().getTextContent();
										lCustRace=((Element)(cUSTOMERPROFILEList.item(0))).getElementsByTagName("lCustRace").item(0).getFirstChild().getTextContent();
										
									}catch(Exception e){
										 //trace.addInfo("error in lCustGenderAndAge or lCustRace");
									}
									
									
								
								}
								
							}catch(Exception e){
								 //trace.addInfo("Sales does not exists");
							}
							
							// end of customer profile
							
							//start of  tender
							
							try
							{
							
							if(!(voidFlag.equals("true"))){
								
								
								
								NodeList tenderList=((Element)lineItemList.item(cnt_Litem)).getElementsByTagName("Tender");
								
								for(int cnt_tenderList=0;cnt_tenderList<tenderList.getLength();cnt_tenderList++)
								{
								NamedNodeMap attrTenderList=tenderList.item(cnt_tenderList).getAttributes();
														
								
								//get all attributes of  Tender
								for(int attrTLCnt=0;attrTLCnt<attrTenderList.getLength();attrTLCnt++){
									
									Node attrvalue=attrTenderList.item(attrTLCnt);
									
									
									if(attrvalue.getNodeName().equals("TypeCode")){
										tenderTypeCode=attrvalue.getNodeValue();
									}else if(attrvalue.getNodeName().equals("TenderType")){
										tenderType=attrvalue.getNodeValue();
									}else if(attrvalue.getNodeName().equals("ExternalTenderType")){
										externalTenderType=attrvalue.getNodeValue();
									}
								}
								
								
								
								
								String REFERENCEID="";
								
								try{
								TENDERAMOUNT=(((Element)tenderList.item(cnt_tenderList)).getElementsByTagName("Amount")).item(0).getTextContent();
								
								}catch(Exception e){
                                //trace.addInfo("error in fetching TENDERAMOUNT,REFERENCEID");
								}
								
								 if(varSaleOrReturn.equals("Sale")){
										
										if(tenderTypeCode.equals("Refund")){
											TENDERAMOUNT="-"+TENDERAMOUNT;
												
											}
									}else if(varSaleOrReturn.equals("Return"))
									{
									
										
										if(tenderTypeCode.equals("Refund")){
										TENDERAMOUNT="-"+TENDERAMOUNT;
											
										}
								}
								
								
								
								
								NodeList brpList=((Element)tenderList.item(cnt_tenderList)).getElementsByTagName("BCARD_REDEEM_PRINCIPAL");
								
								for(int cnt_brpList=0;cnt_brpList<brpList.getLength();cnt_brpList++){
									String bpz_szCardNo="";
									
									try
									{
										bpz_szCardNo=(((Element)brpList.item(cnt_brpList)).getElementsByTagName("szCardNo")).item(0).getTextContent();
										
										REFERENCEID=bpz_szCardNo;
									}catch(Exception e){
										//trace.addInfo(" error while fetching bpz_szCardNo");
									}
									
								}
								
								NodeList brtList=((Element)tenderList.item(cnt_tenderList)).getElementsByTagName("BCARD_REDEEM_TOKEN");
								
								for(int cnt_brtList=0;cnt_brtList<brtList.getLength();cnt_brtList++){
									String brt_szCardNo="";
									
									try
									{
										brt_szCardNo=(((Element)brtList.item(cnt_brtList)).getElementsByTagName("szCardNo")).item(0).getTextContent();
										REFERENCEID=brt_szCardNo;
									}catch(Exception e){
										//trace.addInfo(" error while fetching brt_szCardNo");
									}
									
								}
								
								
								NodeList vList=((Element)tenderList.item(cnt_tenderList)).getElementsByTagName("Voucher");
								
								for(int cnt_vList=0;cnt_vList<vList.getLength();cnt_vList++){
									String v_szCardNo="";
									
									try
									{
										v_szCardNo=(((Element)vList.item(cnt_vList)).getElementsByTagName("SerialNumber")).item(0).getTextContent();
										REFERENCEID=v_szCardNo;
									}catch(Exception e){
										//trace.addInfo(" error while fetching v_szCardNo");
									}
									
								}
								
								
								String item_tender="<item>"+
" <RETAILSTOREID>"+RetailStoreID+"</RETAILSTOREID>"+
" <BUSINESSDAYDATE>"+BusinessDayDate+"</BUSINESSDAYDATE>"+
" <TRANSACTIONTYPECODE>"+TransactionTypeCode+"</TRANSACTIONTYPECODE>"+
"<TENDERAMOUNT>"+TENDERAMOUNT+"</TENDERAMOUNT>"+
"<WORKSTATIONID>"+WorkstationID+"</WORKSTATIONID>"+
"<TRANSACTIONSEQUENCENUMBER>"+SequenceNumber+"</TRANSACTIONSEQUENCENUMBER>"+
" <TENDERSEQUENCENUMBER>"+LineSequenceNumber+"</TENDERSEQUENCENUMBER>"+
" <TENDERTYPECODE>"+externalTenderType+"</TENDERTYPECODE>"+        
" <TENDERCURRENCY>"+CurrencyCode+"</TENDERCURRENCY>"+
" <TENDERCURRENCY_ISO>"+CurrencyCode+"</TENDERCURRENCY_ISO>"+
" <TENDERID>"+tenderType+"</TENDERID>"+
" <ACCOUNTNUMBER></ACCOUNTNUMBER>"+        
"  <REFERENCEID>"+REFERENCEID+"</REFERENCEID>"+
" </item>";
								
								endResult_Tender.append(item_tender);
								
							}//end of tender list for loop
							}//end of tender list if loop
							}//end of tender try
							catch(Exception e){
								//trace.addInfo("error in tender block");
							}
							//end of sales tender
							
							//start of STICKERISSUANCE
							try{
								
								NodeList stickerIssuanceList=((Element)lineItemList.item(cnt_Litem)).getElementsByTagName("STICKERISSUANCE");
								
								for(int cnt_stickerIssuanceList=0;cnt_stickerIssuanceList<stickerIssuanceList.getLength();cnt_stickerIssuanceList++){
									
                                  
									
									try
									{
										TotalIssueSticker=(((Element)stickerIssuanceList.item(cnt_stickerIssuanceList)).getElementsByTagName("TotalIssueSticker")).item(0).getTextContent();
										
									}catch(Exception e){
										//trace.addInfo(" error while fetching v_szCardNo");
									}
									
									try
									{
										ActualIssuedSticker=(((Element)stickerIssuanceList.item(cnt_stickerIssuanceList)).getElementsByTagName("ActualIssuedSticker")).item(0).getTextContent();
										
									}catch(Exception e){
										//trace.addInfo(" error while fetching v_szCardNo");
									}
								}
								
							}catch(Exception e){
								
							}
							
							//end of STICKERISSUANCE
							
							//start of TOTAL_ISSUED_COUPON
							
try{
								
								NodeList totalIssuedCouponList=((Element)lineItemList.item(cnt_Litem)).getElementsByTagName("TOTAL_ISSUED_COUPON");
								
								for(int cnt_totalIssuedCouponList=0;cnt_totalIssuedCouponList<totalIssuedCouponList.getLength();cnt_totalIssuedCouponList++){
									
                                  String PromotionID="";
                                  String PromotionDescription="";
                                  String TotalCouponIssued="";
									
									try
									{
										PromotionID=(((Element)totalIssuedCouponList.item(cnt_totalIssuedCouponList)).getElementsByTagName("PromotionID")).item(0).getTextContent();
										
									}catch(Exception e){
										//trace.addInfo(" error while fetching v_szCardNo");
									}
									
									try
									{
										PromotionDescription=(((Element)totalIssuedCouponList.item(cnt_totalIssuedCouponList)).getElementsByTagName("PromotionDescription")).item(0).getTextContent();
										
									}catch(Exception e){
										//trace.addInfo(" error while fetching v_szCardNo");
									}
									
									try
									{
										TotalCouponIssued=(((Element)totalIssuedCouponList.item(cnt_totalIssuedCouponList)).getElementsByTagName("TotalCouponIssued")).item(0).getTextContent();
										
									}catch(Exception e){
										//trace.addInfo(" error while fetching v_szCardNo");
									}
									
									String itemTrxDiscnt="<item><RETAILSTOREID>"+RetailStoreID+"</RETAILSTOREID>"+
			         "<BUSINESSDAYDATE>"+BusinessDayDate+"</BUSINESSDAYDATE>"+
			        "<WORKSTATIONID>"+WorkstationID+"</WORKSTATIONID>"+
			        "<TRANSACTIONSEQUENCENUMBER>"+SequenceNumber+"</TRANSACTIONSEQUENCENUMBER>"+
			        "<TRANSACTIONTYPECODE>"+TransactionTypeCode+"</TRANSACTIONTYPECODE>"+
			        "<DISCOUNTSEQUENCENUMBER>"+LineSequenceNumber+"</DISCOUNTSEQUENCENUMBER>"+
			        "<DISCOUNTTYPECODE>ZBCP</DISCOUNTTYPECODE>"+
			        "<DISCOUNTREASONCODE>D001</DISCOUNTREASONCODE>"+
			        "<STOREFINANCIALLEDGERACCOUNTID>"+TotalCouponIssued+"</STOREFINANCIALLEDGERACCOUNTID><BONUSBUYID>"+PromotionID+"</BONUSBUYID></item>";
									
									endResult_TDEXT.append(itemTrxDiscnt);
								}
								
							}catch(Exception e){
								
							}
							
							
							//end of TOTAL_ISSUED_COUPON
							
						}//end if Line tem for loop
						
					}// end of retail transaction for loop
					
				//start of TRANSACTIONEXT segment
					item = "<item><RETAILSTOREID>"+RetailStoreID+"</RETAILSTOREID>"+
			         "<BUSINESSDAYDATE>"+BusinessDayDate+"</BUSINESSDAYDATE>"+
			        "<WORKSTATIONID>"+WorkstationID+"</WORKSTATIONID>"+
			        "<TRANSACTIONSEQUENCENUMBER>"+SequenceNumber+"</TRANSACTIONSEQUENCENUMBER>"+         
			         "<FIELDGROUP>POSHDR</FIELDGROUP><FIELDNAME>SHIFTNO</FIELDNAME>"+
			         "<FIELDVALUE>"+ShiftNo+"</FIELDVALUE>"+
			           "<TRANSACTIONTYPECODE>"+TransactionTypeCode+"</TRANSACTIONTYPECODE></item>";
					
					String item_fname = "<item><RETAILSTOREID>"+RetailStoreID+"</RETAILSTOREID>"+
			         "<BUSINESSDAYDATE>"+BusinessDayDate+"</BUSINESSDAYDATE>"+
			        "<WORKSTATIONID>"+WorkstationID+"</WORKSTATIONID>"+
			        "<TRANSACTIONSEQUENCENUMBER>"+SequenceNumber+"</TRANSACTIONSEQUENCENUMBER>"+         
			         "<FIELDGROUP>POSLOG</FIELDGROUP><FIELDNAME>FNAME</FIELDNAME>"+
			         "<FIELDVALUE>"+fileName+"</FIELDVALUE>"+
			           "<TRANSACTIONTYPECODE>"+TransactionTypeCode+"</TRANSACTIONTYPECODE></item>";
					endResult_trxext.append(item);
					endResult_trxext.append(item_fname);
					
					String cust_item = "<item><RETAILSTOREID>"+RetailStoreID+"</RETAILSTOREID>"+
			         "<BUSINESSDAYDATE>"+BusinessDayDate+"</BUSINESSDAYDATE>"+
			        "<WORKSTATIONID>"+WorkstationID+"</WORKSTATIONID>"+
			        "<TRANSACTIONSEQUENCENUMBER>"+SequenceNumber+"</TRANSACTIONSEQUENCENUMBER>"+         
			         "<FIELDGROUP>POSHDR</FIELDGROUP><FIELDNAME>CUSTAGE</FIELDNAME>"+
			         "<FIELDVALUE>"+lCustGenderAndAge+"</FIELDVALUE>"+
			           "<TRANSACTIONTYPECODE>"+TransactionTypeCode+"</TRANSACTIONTYPECODE></item>" +
			           		"<item><RETAILSTOREID>"+RetailStoreID+"</RETAILSTOREID>"+
			         "<BUSINESSDAYDATE>"+BusinessDayDate+"</BUSINESSDAYDATE>"+
			        "<WORKSTATIONID>"+WorkstationID+"</WORKSTATIONID>"+
			        "<TRANSACTIONSEQUENCENUMBER>"+SequenceNumber+"</TRANSACTIONSEQUENCENUMBER>"+         
			         "<FIELDGROUP>POSHDR</FIELDGROUP><FIELDNAME>CUSTRACE</FIELDNAME>"+
			         "<FIELDVALUE>"+lCustRace+"</FIELDVALUE>"+
			           "<TRANSACTIONTYPECODE>"+TransactionTypeCode+"</TRANSACTIONTYPECODE></item>";
					
					
					
					endResult_trxext.append(cust_item);
					
					String sticker_issuance_item="<item><RETAILSTOREID>"+RetailStoreID+"</RETAILSTOREID>"+
			         "<BUSINESSDAYDATE>"+BusinessDayDate+"</BUSINESSDAYDATE>"+
				        "<WORKSTATIONID>"+WorkstationID+"</WORKSTATIONID>"+
				        "<TRANSACTIONSEQUENCENUMBER>"+SequenceNumber+"</TRANSACTIONSEQUENCENUMBER>"+         
				         "<FIELDGROUP>STICKER</FIELDGROUP><FIELDNAME>EXPSTCKR</FIELDNAME>"+
				         "<FIELDVALUE>"+TotalIssueSticker+"</FIELDVALUE>"+
				           "<TRANSACTIONTYPECODE>"+TransactionTypeCode+"</TRANSACTIONTYPECODE></item>" +
				           		"<item><RETAILSTOREID>"+RetailStoreID+"</RETAILSTOREID>"+
				         "<BUSINESSDAYDATE>"+BusinessDayDate+"</BUSINESSDAYDATE>"+
				        "<WORKSTATIONID>"+WorkstationID+"</WORKSTATIONID>"+
				        "<TRANSACTIONSEQUENCENUMBER>"+SequenceNumber+"</TRANSACTIONSEQUENCENUMBER>"+         
				         "<FIELDGROUP>STICKER</FIELDGROUP><FIELDNAME>ACTSTCKR</FIELDNAME>"+
				         "<FIELDVALUE>"+ActualIssuedSticker+"</FIELDVALUE>"+
				           "<TRANSACTIONTYPECODE>"+TransactionTypeCode+"</TRANSACTIONTYPECODE></item>";
					
					endResult_trxext.append(sticker_issuance_item);
					//end of TRANSACTIONEXT segment
					
					
					//start of TRANSACTION segment
					 item_trx ="<item><RETAILSTOREID>"+RetailStoreID+"</RETAILSTOREID>"+
			         "<BUSINESSDAYDATE>"+BusinessDayDate+"</BUSINESSDAYDATE>"+
			          "<TRANSACTIONTYPECODE>"+TransactionTypeCode+"</TRANSACTIONTYPECODE>"+           
			        "<WORKSTATIONID>"+WorkstationID+"</WORKSTATIONID>"+
			         "<TRANSACTIONSEQUENCENUMBER>"+SequenceNumber+"</TRANSACTIONSEQUENCENUMBER>"+
			         "<BEGINDATETIMESTAMP>"+BeginDateTime+"</BEGINDATETIMESTAMP>"+
			         "<ENDDATETIMESTAMP>"+EndDateTime+"</ENDDATETIMESTAMP>"+
			         "<DEPARTMENT></DEPARTMENT>"+
			         "<OPERATORQUALIFIER></OPERATORQUALIFIER>"+
			         "<OPERATORID>"+OperatorID+"</OPERATORID>"+
			         "<TRANSACTIONCURRENCY>"+CurrencyCode+"</TRANSACTIONCURRENCY>"+
			         "<TRANSACTIONCURRENCY_ISO>"+CurrencyCode+"</TRANSACTIONCURRENCY_ISO>"+
			         "<PARTNERQUALIFIER/><PARTNERID/></item>";  
					endResult_trx.append(item_trx);
					
					//end of TRANSACTION segment
				
					salesTotalCount=SequenceNumber;
			   }// end of else loop
			
			
			
			//Start of SalesTotal
			NodeList salesTotalList=((Element)transactionList.item(cntTrx)).getElementsByTagName("SalesTotal");
			
			String SalesValue="";
			String POSTerminal="";
			
			
			for(int cnt_salesTotalList=0;cnt_salesTotalList<salesTotalList.getLength();cnt_salesTotalList++){
				
				try{
					SalesValue=(((Element)salesTotalList.item(cnt_salesTotalList)).getElementsByTagName("SalesValue")).item(0).getTextContent();
					
				}catch(Exception e){
					
				}
				try{
					
					POSTerminal=(((Element)salesTotalList.item(cnt_salesTotalList)).getElementsByTagName("POSTerminal")).item(0).getTextContent();
				}catch(Exception e){
					
				}
			
			String itemSL = "<item><RETAILSTOREID>"+RetailStoreID+"</RETAILSTOREID>"+
	         "<BUSINESSDAYDATE>"+BusinessDayDate+"</BUSINESSDAYDATE>"+
	        "<WORKSTATIONID>"+POSTerminal+"</WORKSTATIONID>"+
	        "<TRANSACTIONSEQUENCENUMBER>"+salesTotalCount+"_"+(cnt_salesTotalList+1)+"</TRANSACTIONSEQUENCENUMBER>"+         
	         "<FIELDGROUP>POSTT</FIELDGROUP><FIELDNAME>SALESTOTAL</FIELDNAME>"+
	         "<FIELDVALUE>"+SalesValue+"</FIELDVALUE>"+
	           "<TRANSACTIONTYPECODE>1191</TRANSACTIONTYPECODE></item>";
			endResult_trxext.append(itemSL);
			
			String itemFN = "<item><RETAILSTOREID>"+RetailStoreID+"</RETAILSTOREID>"+
	         "<BUSINESSDAYDATE>"+BusinessDayDate+"</BUSINESSDAYDATE>"+
	        "<WORKSTATIONID>"+POSTerminal+"</WORKSTATIONID>"+
	        "<TRANSACTIONSEQUENCENUMBER>"+salesTotalCount+"_"+(cnt_salesTotalList+1)+"</TRANSACTIONSEQUENCENUMBER>"+         
	         "<FIELDGROUP>POSLOG</FIELDGROUP><FIELDNAME>FNAME</FIELDNAME>"+
	         "<FIELDVALUE>"+fileName+"</FIELDVALUE>"+
	           "<TRANSACTIONTYPECODE>1191</TRANSACTIONTYPECODE></item>";
			endResult_trxext.append(itemFN);
		
			
			//end of TRANSACTIONEXT segment
			
			
			//start of TRANSACTION segment
			 String item_trxSL ="<item><RETAILSTOREID>"+RetailStoreID+"</RETAILSTOREID>"+
	         "<BUSINESSDAYDATE>"+BusinessDayDate+"</BUSINESSDAYDATE>"+
	          "<TRANSACTIONTYPECODE>1191</TRANSACTIONTYPECODE>"+           
	        "<WORKSTATIONID>"+POSTerminal+"</WORKSTATIONID>"+
	         "<TRANSACTIONSEQUENCENUMBER>"+salesTotalCount+"_"+(cnt_salesTotalList+1)+"</TRANSACTIONSEQUENCENUMBER>"+
	         "<BEGINDATETIMESTAMP>"+BeginDateTime+"</BEGINDATETIMESTAMP>"+
	         "<ENDDATETIMESTAMP>"+EndDateTime+"</ENDDATETIMESTAMP>"+
	         "<DEPARTMENT></DEPARTMENT>"+
	         "<OPERATORQUALIFIER></OPERATORQUALIFIER>"+
	         "<OPERATORID>"+OperatorID+"</OPERATORID>"+
	         "<TRANSACTIONCURRENCY>"+CurrencyCode+"</TRANSACTIONCURRENCY>"+
	         "<TRANSACTIONCURRENCY_ISO>"+CurrencyCode+"</TRANSACTIONCURRENCY_ISO>"+
	         "<PARTNERQUALIFIER/><PARTNERID/></item>";  
			endResult_trx.append(item_trxSL);
			
			
			
			}
			
			//end of SalesTotal
			
			//Start of CouponDeclaration
			NodeList cdList=((Element)transactionList.item(cntTrx)).getElementsByTagName("CouponDeclaration");
			int cdCount=0;
			String szCouponArticleID="";
			String szCouponID="";
			String szCouponDesc="";
			String lSystemCount="";
			String lPhysicalCount="";
			String lVariantCount="";
			for(int cnt_couponDeclarationList=0;cnt_couponDeclarationList<cdList.getLength();cnt_couponDeclarationList++){
				
				try{
					szCouponArticleID=(((Element)cdList.item(cnt_couponDeclarationList)).getElementsByTagName("szCouponArticleID")).item(0).getTextContent();
					
				}catch(Exception e){
					
				}
				try{
					szCouponID=(((Element)cdList.item(cnt_couponDeclarationList)).getElementsByTagName("szCouponID")).item(0).getTextContent();
					
				}catch(Exception e){
					
				}
				try{
					szCouponDesc=(((Element)cdList.item(cnt_couponDeclarationList)).getElementsByTagName("szCouponDesc")).item(0).getTextContent();
					
				}catch(Exception e){
					
				}
				try{
					lSystemCount=(((Element)cdList.item(cnt_couponDeclarationList)).getElementsByTagName("lSystemCount")).item(0).getTextContent();
					
				}catch(Exception e){
					
				}
				try{
					lPhysicalCount=(((Element)cdList.item(cnt_couponDeclarationList)).getElementsByTagName("lPhysicalCount")).item(0).getTextContent();
					
				}catch(Exception e){
					
				}
				try{
					lVariantCount=(((Element)cdList.item(cnt_couponDeclarationList)).getElementsByTagName("lVariantCount")).item(0).getTextContent();
					
				}catch(Exception e){
					
				}
				
				cdCount=cdCount+1;
				String item ="<item><RETAILSTOREID>"+RetailStoreID+"</RETAILSTOREID>"+
"<BUSINESSDAYDATE>"+BusinessDayDate+"</BUSINESSDAYDATE>"+
"<WORKSTATIONID>"+WorkstationID+"</WORKSTATIONID>"+
"<TRANSACTIONSEQUENCENUMBER>"+SequenceNumber+"_2_"+(cdCount)+"</TRANSACTIONSEQUENCENUMBER>"+         
"<FIELDGROUP>POSHDR</FIELDGROUP><FIELDNAME>SHIFTNO</FIELDNAME>"+
"<FIELDVALUE>"+ShiftNo+"</FIELDVALUE>"+
"<TRANSACTIONTYPECODE>1302</TRANSACTIONTYPECODE></item>";
				
				endResult_trxext.append(item);
				String item1 ="<item><RETAILSTOREID>"+RetailStoreID+"</RETAILSTOREID>"+
		         "<BUSINESSDAYDATE>"+BusinessDayDate+"</BUSINESSDAYDATE>"+
		        "<WORKSTATIONID>"+WorkstationID+"</WORKSTATIONID>"+
		        "<TRANSACTIONSEQUENCENUMBER>"+SequenceNumber+"_2_"+(cdCount)+"</TRANSACTIONSEQUENCENUMBER>"+         
		         "<FIELDGROUP>POSLOG</FIELDGROUP><FIELDNAME>FNAME</FIELDNAME>"+
		         "<FIELDVALUE>"+fileName+"</FIELDVALUE>"+
		           "<TRANSACTIONTYPECODE>1302</TRANSACTIONTYPECODE></item>";
									
									endResult_trxext.append(item1);
									
				
				String item_trx ="<item><RETAILSTOREID>"+RetailStoreID+"</RETAILSTOREID>"+
"<BUSINESSDAYDATE>"+BusinessDayDate+"</BUSINESSDAYDATE>"+
"<TRANSACTIONTYPECODE>1302</TRANSACTIONTYPECODE>"+           
"<WORKSTATIONID>"+WorkstationID+"</WORKSTATIONID>"+
"<TRANSACTIONSEQUENCENUMBER>"+SequenceNumber+"_2_"+(cdCount)+"</TRANSACTIONSEQUENCENUMBER>"+
"<BEGINDATETIMESTAMP>"+BeginDateTime+"</BEGINDATETIMESTAMP>"+
"<ENDDATETIMESTAMP>"+EndDateTime+"</ENDDATETIMESTAMP>"+
"<DEPARTMENT></DEPARTMENT>"+
"<OPERATORQUALIFIER></OPERATORQUALIFIER>"+
"<OPERATORID>"+OperatorID+"</OPERATORID>"+
"<TRANSACTIONCURRENCY>"+CurrencyCode+"</TRANSACTIONCURRENCY>"+
"<TRANSACTIONCURRENCY_ISO>"+CurrencyCode+"</TRANSACTIONCURRENCY_ISO>"+
"<PARTNERQUALIFIER/><PARTNERID/></item>";  
				
				endResult_trx.append(item_trx);
				
				String item_fmext ="";
				
				item_fmext ="<item>"+
"<RETAILSTOREID>"+RetailStoreID+"</RETAILSTOREID>"+
"<BUSINESSDAYDATE>"+BusinessDayDate+"</BUSINESSDAYDATE>"+
"<TRANSACTIONTYPECODE>1302</TRANSACTIONTYPECODE>"+         
"<WORKSTATIONID>"+WorkstationID+"</WORKSTATIONID>"+
"<TRANSACTIONSEQUENCENUMBER>"+SequenceNumber+"_2_"+(cdCount)+"</TRANSACTIONSEQUENCENUMBER>"+
"<FINANCIALSEQUENCENUMBER>1</FINANCIALSEQUENCENUMBER>"+        
"<FIELDGROUP>FINTRANS</FIELDGROUP>"+
" <FIELDNAME>szCouponArticleID</FIELDNAME>"+
"<FIELDVALUE>"+szCouponArticleID+"</FIELDVALUE>"+

"</item>";
									
									endResult_FMEXT.append(item_fmext);
									
									item_fmext ="<item>"+
									"<RETAILSTOREID>"+RetailStoreID+"</RETAILSTOREID>"+
									"<BUSINESSDAYDATE>"+BusinessDayDate+"</BUSINESSDAYDATE>"+
									"<TRANSACTIONTYPECODE>1302</TRANSACTIONTYPECODE>"+         
									"<WORKSTATIONID>"+WorkstationID+"</WORKSTATIONID>"+
									"<TRANSACTIONSEQUENCENUMBER>"+SequenceNumber+"_2_"+(cdCount)+"</TRANSACTIONSEQUENCENUMBER>"+
									"<FINANCIALSEQUENCENUMBER>1</FINANCIALSEQUENCENUMBER>"+        
									"<FIELDGROUP>FINTRANS</FIELDGROUP>"+
									" <FIELDNAME>szCouponID</FIELDNAME>"+
									"<FIELDVALUE>"+szCouponID+"</FIELDVALUE>"+

									"</item>";
																		
																		endResult_FMEXT.append(item_fmext);
																		
																	item_fmext ="<item>"+
																		"<RETAILSTOREID>"+RetailStoreID+"</RETAILSTOREID>"+
																		"<BUSINESSDAYDATE>"+BusinessDayDate+"</BUSINESSDAYDATE>"+
																		"<TRANSACTIONTYPECODE>1302</TRANSACTIONTYPECODE>"+         
																		"<WORKSTATIONID>"+WorkstationID+"</WORKSTATIONID>"+
																		"<TRANSACTIONSEQUENCENUMBER>"+SequenceNumber+"_2_"+(cdCount)+"</TRANSACTIONSEQUENCENUMBER>"+
																		"<FINANCIALSEQUENCENUMBER>1</FINANCIALSEQUENCENUMBER>"+        
																		"<FIELDGROUP>FINTRANS</FIELDGROUP>"+
																		" <FIELDNAME>szCouponDesc</FIELDNAME>"+
																		"<FIELDVALUE>"+szCouponDesc+"</FIELDVALUE>"+

																		"</item>";
																											
																											endResult_FMEXT.append(item_fmext);
																											
																		item_fmext ="<item>"+
																		"<RETAILSTOREID>"+RetailStoreID+"</RETAILSTOREID>"+
																											"<BUSINESSDAYDATE>"+BusinessDayDate+"</BUSINESSDAYDATE>"+
																											"<TRANSACTIONTYPECODE>1302</TRANSACTIONTYPECODE>"+         
																											"<WORKSTATIONID>"+WorkstationID+"</WORKSTATIONID>"+
																											"<TRANSACTIONSEQUENCENUMBER>"+SequenceNumber+"_2_"+(cdCount)+"</TRANSACTIONSEQUENCENUMBER>"+
																											"<FINANCIALSEQUENCENUMBER>1</FINANCIALSEQUENCENUMBER>"+        
																											"<FIELDGROUP>FINTRANS</FIELDGROUP>"+
																											" <FIELDNAME>lSystemCount</FIELDNAME>"+
																											"<FIELDVALUE>"+lSystemCount+"</FIELDVALUE>"+

																											"</item>";
																																				
																																				endResult_FMEXT.append(item_fmext);
																																				
																																				item_fmext ="<item>"+
																																				"<RETAILSTOREID>"+RetailStoreID+"</RETAILSTOREID>"+
																																				"<BUSINESSDAYDATE>"+BusinessDayDate+"</BUSINESSDAYDATE>"+
																																				"<TRANSACTIONTYPECODE>1302</TRANSACTIONTYPECODE>"+         
																																				"<WORKSTATIONID>"+WorkstationID+"</WORKSTATIONID>"+
																																				"<TRANSACTIONSEQUENCENUMBER>"+SequenceNumber+"_2_"+(cdCount)+"</TRANSACTIONSEQUENCENUMBER>"+
																																				"<FINANCIALSEQUENCENUMBER>1</FINANCIALSEQUENCENUMBER>"+        
																																				"<FIELDGROUP>FINTRANS</FIELDGROUP>"+
																																				" <FIELDNAME>lPhysicalCount</FIELDNAME>"+
																																				"<FIELDVALUE>"+lPhysicalCount+"</FIELDVALUE>"+

																																				"</item>";
																																													
																																													endResult_FMEXT.append(item_fmext);
																																													
																																				item_fmext ="<item>"+
																																							"<RETAILSTOREID>"+RetailStoreID+"</RETAILSTOREID>"+
																																													"<BUSINESSDAYDATE>"+BusinessDayDate+"</BUSINESSDAYDATE>"+
																																													"<TRANSACTIONTYPECODE>1302</TRANSACTIONTYPECODE>"+         
																																													"<WORKSTATIONID>"+WorkstationID+"</WORKSTATIONID>"+
																																													"<TRANSACTIONSEQUENCENUMBER>"+SequenceNumber+"_2_"+(cdCount)+"</TRANSACTIONSEQUENCENUMBER>"+
																																													"<FINANCIALSEQUENCENUMBER>1</FINANCIALSEQUENCENUMBER>"+        
																																													"<FIELDGROUP>FINTRANS</FIELDGROUP>"+
																																													" <FIELDNAME>lVariantCount</FIELDNAME>"+
																																													"<FIELDVALUE>"+lVariantCount+"</FIELDVALUE>"+

																																													"</item>";
																																																						
																																																						endResult_FMEXT.append(item_fmext);
				
									String item_fm=" <item>"+
								       "<RETAILSTOREID>"+RetailStoreID+"</RETAILSTOREID>"+
								        " <BUSINESSDAYDATE>"+BusinessDayDate+"</BUSINESSDAYDATE>"+
								         "<TRANSACTIONTYPECODE>1302</TRANSACTIONTYPECODE> "+        
								         "<WORKSTATIONID>"+WorkstationID+"</WORKSTATIONID>"+
								         "<TRANSACTIONSEQUENCENUMBER>"+SequenceNumber+"_2_"+(cdCount)+"</TRANSACTIONSEQUENCENUMBER>"+
								         "<FINANCIALSEQUENCENUMBER>1</FINANCIALSEQUENCENUMBER>"+
								         "<FINANCIALTYPECODE>CouponDeclaration</FINANCIALTYPECODE>"+
								        " <ACCOUNTID>"+RetailStoreID+"</ACCOUNTID>"+
								         "<ACCOUNTASSIGNMENTOBJECT></ACCOUNTASSIGNMENTOBJECT>"+
								         "<AMOUNT></AMOUNT>"+
								         "<FINANCIALCURRENCY>"+CurrencyCode+"</FINANCIALCURRENCY>"+
								         "<FINANCIALCURRENCY_ISO>"+CurrencyCode+"</FINANCIALCURRENCY_ISO>"+
								         "<REFERERENCEID></REFERERENCEID>"+
								     " </item>";
									endResult_FM.append(item_fm);
									
									
			}
			
			//end of CouponDeclaration
		
		}// end of transction for loop
		
		
		
		endResult_trxext.append("</TRANSACTIONEXT>");
		endResult_trx.append("</TRANSACTION>");
		endResult_RL.append("</RETAILLINEITEM>");
		endResult_LIL.append("</LINEITEMLOYALTY>");
		endResult_TrxLylt.append("</TRANSACTIONLOYALTY>");
		endResult_itemDisc.append("</LINEITEMDISCOUNT>");
		endResult_itemVoid.append("</LINEITEMVOID>");
		endResult_itemTax.append("</LINEITEMTAX>");
		endResult_Tender.append("</TENDER>");
		endResult_FM.append("</FINANCIALMOVEMENT>");
		endResult_FMEXT.append("</FINANCIALMOVEMENTEXT>");
		endResult_TDEXT.append("</TRANSACTIONDISCOUNT>");
		endResult.append(endResult_trxext);	
		endResult.append(endResult_trx);
		endResult.append(endResult_RL);
		endResult.append(endResult_LIL);
		endResult.append(endResult_TrxLylt);
		endResult.append(endResult_itemDisc);
		endResult.append(endResult_itemVoid);
		endResult.append(endResult_itemTax);
		endResult.append(endResult_Tender);
		endResult.append(endResult_FM);
		endResult.append(endResult_FMEXT);
		endResult.append(endResult_TDEXT);
		endResult.append("</ns0:_-POSDW_-BAPI_POSTR_CREATE>");
		
		String tempResult=endResult.toString();
		tempResult=tempResult.replaceAll("&", "&amp;");
	     // System.out.println(tempResult.toString());
		arg1.getOutputPayload().getOutputStream().write(tempResult.getBytes());
		
		}catch(Exception e){
			//trace.addInfo("exception in main branch"+e.getMessage());
		}// end of main try

	}

}
