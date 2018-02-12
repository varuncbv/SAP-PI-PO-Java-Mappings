package EDI_FlatFile_Conversion;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;

import com.sap.aii.mapping.api.AbstractTransformation;
import com.sap.aii.mapping.api.StreamTransformationException;
import com.sap.aii.mapping.api.TransformationInput;
import com.sap.aii.mapping.api.TransformationOutput;

public class JavaMap_Convert_EDIFlatFile_To_EDI_Hierarchial_XML extends AbstractTransformation {

	
	
	//static String input_edi="ISA*00*          *00*          *ZZ*               *ZZ*               *130605*1906*U*00400*000000026*0*P*~~GS*AG*202691093*Receiver*20130605*1915*1000258319*X*004010~ST*824*0001~BGN*11*999999999999999999*20130605*192907*02*000000725**U~N1*BK*Bank of Montreal~PER*CC*BOM EDI/EFT OPERATIONS*TE*312-461-6185 OR 2352~OTI*GP*FI*9999~OTI*TA*TN*6700000391*202691093**20130605*1810*000258319*0001*824~REF*TN*6700000391~AMT*OP*500~OTI*TR*TN*6700000390*2026991093**20130605*1810*000258319*0001*824~TED*848*Data error~SE*11*0001~GE*1*1000258319~IEA*1*000000026~";
	//static String input_edi="ISA*00* *00* *ZZ*EED1 NYIExxx *01*SUPPLIER01 *060707*0905*U*00401*000000064*0*T*~~GS*AG*NYIExxx*SUPPLIER01*20060707*0905*64*X*004010~ST*824*0081~BGN*44*0015130024*20040913~N1*15*Talbot*92*IBMEND~N1*II*ALICE B WONDERFULL*92*1000000157~OTI*TR*IV*0015130024~DTM*007*20060707*0905~TED*OTH*This Transaction was rejected by IBM Accounts Payable!~NTE*INV*SAP Document Number : Q001 5100000178~NTE*INV*RTV For SAP Document Number : Q001 5100000178~NTE*INV*We are unable to pay your invoice, document number 5110668446~NTE*INV*dated 09/13/2004 8000000372~NTE*INV*for the following reason(s):~NTE*INV* All invoices that include freight charges must be resubmitted with~NTE*INV*the original bill of lading, prepaid transportation bill or a~NTE*INV*comparable shipping document attached.The invoice must referencethe~NTE*INV*original invoice the goods were billed on and the Purchase Order~NTE*INV*number.~NTE*INV*Please resolve discrepancies with your Purchasing Representative~NTE*INV*or Contact person prior to re-invoicing. Payment will be scheduled~NTE*INV*from the date the corrected invoice is received.~NTE*INV*Please resubmit EDI invoices electronically.~SE*22*0081~GE*1*64~IEA*1*000000064~";
	 String input_edi="";
	StringBuffer  endResult = new StringBuffer();
	String segmentTerminator="";
	String fieldTerminator="";
	
	
	
	public void transform(TransformationInput arg0, TransformationOutput out) 
    throws StreamTransformationException 
    
    {
		
		//Following bottom to top approach to build EDI heirarchial XML
		
		
		
		try{
			
			
			
			 
			 
			InputStream inp=arg0.getInputPayload().getInputStream();
			
			input_edi = convertInputStreamToString(inp);
			int segmentTerminatorIndex=input_edi.indexOf("IEA");
			segmentTerminator=input_edi.substring((segmentTerminatorIndex-1), segmentTerminatorIndex);			
			fieldTerminator=input_edi.substring((segmentTerminatorIndex+3), (segmentTerminatorIndex+4));
			//Array List to Collect all GS & GE segments
		java.util.ArrayList GS_obj= new java.util.ArrayList();
			
			//Array List to Collect all ST & SE segments
		java.util.ArrayList ST_obj= new java.util.ArrayList();
		
		//Array List to Collect all N1 segments
		java.util.ArrayList N1_obj= new java.util.ArrayList();
		
		//Array List to Collect all N1 PER segments
		java.util.ArrayList N1_PER_obj= new java.util.ArrayList();
		
		
		//Array List to Collect all BGN segments
		java.util.ArrayList BGN_obj= new java.util.ArrayList();
		
		//Array List to Collect all OTI segments
		java.util.ArrayList OTI_obj= new java.util.ArrayList();
		
		//Array List to Collect all OTI_TED segments
		java.util.ArrayList OTI_TED_obj= new java.util.ArrayList();
		
		//Array List to Collect all OTI_TED_NTE segments segments
		java.util.ArrayList OTI_TED_NTE_obj= new java.util.ArrayList();
		
		//Array List to Collect all OTI_DTM segments segments
		java.util.ArrayList OTI_DTM_obj= new java.util.ArrayList();
		
		
		
		//int isa_index = input_edi.indexOf("~");
		int isa_index = input_edi.indexOf(segmentTerminator);
		String isa_only=input_edi.substring(0, isa_index);
		Build_ISA_XML(isa_only);
		
		GS_obj=Collect_GS_GE_Segments(input_edi);
		
		for(int j=0;j<GS_obj.size();j++)
		{
		String gs_temp=(String)GS_obj.get(j);
		
		//int st_index = gs_temp.indexOf("~");
		int st_index = gs_temp.indexOf(segmentTerminator);
		String gs_only=gs_temp.substring(0, st_index);
		Build_GS_XML(gs_only);
		
		
		ST_obj=Collect_ST_SE_Segments(gs_temp);		
		for(int k=0;k<ST_obj.size();k++){
			
			String st_temp=(String)ST_obj.get(k);
			
			//int bgn_index=st_temp.indexOf("~");
			
			int bgn_index=st_temp.indexOf(segmentTerminator);
			
			String st_only=st_temp.substring(0, bgn_index);
			////System.out.println(st_only);
			Build_ST_XML(st_only);
			
			
			//collect BGN segments
			BGN_obj= Collect_BGN_Segments(st_temp);
			for(int bgn_k=0;bgn_k<BGN_obj.size();bgn_k++){
				String bgn_temp=(String)BGN_obj.get(bgn_k);
			Build_BGN_XML(bgn_temp);
			}
			
			
			//collect N1 segments 
			N1_obj= Collect_N1_Segments(st_temp);
			for(int n1_k=0;n1_k<N1_obj.size();n1_k++){
				String n1_temp=(String)N1_obj.get(n1_k);
			Build_N1_XML(n1_temp);
			boolean n1_PER=n1_temp.contains("PER");
			if(n1_PER==true)//implies OTI  contain DTM and the logic must be implemented in below method.As of now not writing any code.
			{
				N1_PER_obj=Collect_N1_PER_Segments(n1_temp);
				for(int n1_per_k=0;n1_per_k<N1_PER_obj.size();n1_per_k++)
				{
					String n1_per_temp=(String)N1_PER_obj.get(n1_per_k);					
					Build_N1_PER_XML(n1_per_temp);//all clear
					
				}
				
			}
			endResult.append("</S_N1> </G_SN1>");
			}
			
			
			//collect OTI segments
			OTI_obj=Collect_OTI_Segments(st_temp);	
			for(int oti_k=0;oti_k<OTI_obj.size();oti_k++){
				String oti_temp=(String)OTI_obj.get(oti_k);				
				
				//////System.out.println("oti_temp is"+oti_temp);
				Build_OTI_XML(oti_temp);//all clear
				
				boolean oti_SLM=oti_temp.contains("SLM");
				if(oti_SLM==true)//implies OTI  contain SLM and the logic must be implemented in below method.As of now not writing any code.
				{
					
				}
				boolean oti_DTM=oti_temp.contains("DTM");
				if(oti_DTM==true)//implies OTI  contain DTM and the logic must be implemented in below method.As of now not writing any code.
				{
					OTI_DTM_obj=Collect_OTI_DTM_Segments(oti_temp);
					for(int oti_dtm_k=0;oti_dtm_k<OTI_DTM_obj.size();oti_dtm_k++)
					{
						String oti_dtm_temp=(String)OTI_DTM_obj.get(oti_dtm_k);	
						
						Build_OTI_DTM_XML(oti_dtm_temp);//all clear
						
					}
					
				}
				boolean oti_TED=oti_temp.contains("TED");
				//////////System.out.println("oti_TED is"+oti_TED);
				if(oti_TED==true)//implies OTI  contain SLM and the logic must be implemented in below method.As of now not writing any code.
				{
					OTI_TED_obj=Collect_OTI_TED_Segments(oti_temp);
					for(int oti_ted_k=0;oti_ted_k<OTI_TED_obj.size();oti_ted_k++)
					{
						String oti_tdi_temp=(String)OTI_TED_obj.get(oti_ted_k);	
						
						Collect_OTI_TED_NTE_Segments(oti_tdi_temp);
						
					}
					
				}
				
				
				endResult.append("</S_OTI> </G_SOTI>");
			}// end of OTI for loop
			
			
			//Step2 : Get SE segment
			//int SE_index=st_temp.indexOf("~SE");	
			int SE_index=st_temp.indexOf(segmentTerminator+"SE");
			String SE_XML = st_temp.substring(SE_index);
			Build_SE_XML(SE_XML);//all clear
			
			
		}// end of ST for loop
		
		//int GE_index=gs_temp.indexOf("~GE");
		int GE_index=gs_temp.indexOf(segmentTerminator+"GE");
		String SE_XML = gs_temp.substring(GE_index);
		Build_GE_XML(SE_XML);//all clear
		}// end of GS for loop
		
		
		//int IEA_index=input_edi.indexOf("~IEA");
		int IEA_index=input_edi.indexOf(segmentTerminator+"IEA");
		String IEA_XML=input_edi.substring(IEA_index,input_edi.length()-1);
		
		Build_IEA_XML(IEA_XML);//all clear
		
		getTrace().addInfo("xml"+endResult.toString());
		out.getOutputPayload().getOutputStream().write(endResult.toString().getBytes());
		}catch(Exception e){
			e.printStackTrace();
		}
		
		

	}
	
	
public  void Collect_OTI_TED_NTE_Segments(String input_edi){
		
	//System.out.println(input_edi);
		
		//java.util.ArrayList obj= new java.util.ArrayList();
		try{
		 
		// String str[]=input_edi.split("~NTE");
			
			 String str[]=input_edi.split(segmentTerminator+"NTE");
		 
		 
		 for(int k=0;k<str.length;k++){
			 String nte_temp=str[k];
			 //java.util.StringTokenizer stkr_element=new java.util.StringTokenizer(nte_temp,"*");
			 java.util.StringTokenizer stkr_element=new java.util.StringTokenizer(nte_temp,fieldTerminator);
			 if(k==0){
				 
				 for(int j=0;stkr_element.hasMoreTokens();j++){
					 String temp = stkr_element.nextToken();
						String result="";
						 if(j==1)
						 {
							 result="<G_STED><S_TED><D_647>"+temp+"</D_647>";
							endResult.append(result);
						 }else if(j==2){
							 result="<D_3>"+temp+"</D_3>";
							endResult.append(result);
						 }
					 
				 }
			 }
			 if(k>0){
			// ////////System.out.println("nte_temp is"+nte_temp);
			 
			// ////////System.out.println(stkr_element.countTokens());
			 for(int j=0;stkr_element.hasMoreTokens();j++){
					String temp = stkr_element.nextToken();
					String result="";
					 if(j==0)
					 {
						 result="<S_NTE><D_363>"+temp+"</D_363>";
						endResult.append(result);
					 }else if(j==1){
						 result="<D_352>"+temp+"</D_352></S_NTE>";
						endResult.append(result);
					 }
			 }
			 }
			
		 }
		 endResult.append("</S_TED></G_STED>");
		//	////////System.out.println(endResult);
			
			
		}catch(Exception e){
			e.printStackTrace();
		}
	
	}

public  ArrayList Collect_N1_PER_Segments(String input_edi){
	
	
	java.util.ArrayList obj= new java.util.ArrayList();
	try{
	 
	 //String str[]=input_edi.split("~PER");
		
		String str[]=input_edi.split(segmentTerminator+"PER");
		
		for(int k=0;k<str.length;k++){
			
			if(k>0){
				//int IEA_index=str[k].indexOf("~");
				
				
				int IEA_index=str[k].indexOf(segmentTerminator);
				
				String temp="";
				if(IEA_index>-1){
					
					
					temp="PER"+str[k].substring(0, IEA_index);
					//////////System.out.println(temp);
					obj.add(temp);
					
				}else{
					temp="PER"+str[k];
					//////////System.out.println(temp);
					obj.add(temp);
					
				}
				}
			}
		
	}catch(Exception e){
		e.printStackTrace();
	}
	return obj;

}

public  ArrayList Collect_OTI_DTM_Segments(String input_edi){
	
	
	java.util.ArrayList obj= new java.util.ArrayList();
	try{
	 
	// String str[]=input_edi.split("~DTM");
		 String str[]=input_edi.split(segmentTerminator+"DTM");
		
		for(int k=0;k<str.length;k++){
			
			if(k>0){
				//int IEA_index=str[k].indexOf("~");
				int IEA_index=str[k].indexOf(segmentTerminator);
				
				String temp="";
				if(IEA_index>-1){
					
					
					temp="DTM"+str[k].substring(0, IEA_index);
					//////////System.out.println(temp);
					obj.add(temp);
					
				}else{
					temp="DTM"+str[k];
					//////////System.out.println(temp);
					obj.add(temp);
					
				}
				}
			}
		
	}catch(Exception e){
		e.printStackTrace();
	}
	return obj;

}
public  ArrayList Collect_OTI_TED_Segments(String input_edi){
		
		//System.out.println("***"+input_edi);
		java.util.ArrayList obj= new java.util.ArrayList();
		try{
		 
		 //String str[]=input_edi.split("~TED");
			String str[]=input_edi.split(segmentTerminator+"TED");
			
			for(int k=0;k<str.length;k++){
				
				if(k>0){
					//int IEA_index=str[k].indexOf("~SE");
					int IEA_index=str[k].indexOf(segmentTerminator+"SE");
					
					String temp="";
					if(IEA_index>-1){
						
						
						temp="TED"+str[k].substring(0, IEA_index);
						////////////System.out.println(temp);
						obj.add(temp);
						
					}else{
						temp="TED"+str[k];
						////////////System.out.println(temp);
						obj.add(temp);
						
					}
					}
				}
			
		}catch(Exception e){
			e.printStackTrace();
		}
		return obj;
	
	}
public  ArrayList Collect_BGN_Segments(String input_edi){
	
	
	java.util.ArrayList obj= new java.util.ArrayList();
	try{
	// ////////System.out.println("input_edi is **"+input_edi);
	// String str[]=input_edi.split("~BGN");
		 String str[]=input_edi.split(segmentTerminator+"BGN");
		
		for(int k=0;k<str.length;k++){
			//////////System.out.println(str[k]);
			if(k>0){
				//int IEA_index=str[k].indexOf("~N1");
				int IEA_index=str[k].indexOf(segmentTerminator+"N1");
				
				String temp="";
				if(IEA_index>-1){
					
					
					temp="BGN"+str[k].substring(0, IEA_index);
					////////System.out.println(temp);
					obj.add(temp);
					
				}else{
					temp="BGN"+str[k];
					////////System.out.println(temp);
					obj.add(temp);
					
				}
				}
			}
		
	}catch(Exception e){
		e.printStackTrace();
	}
	return obj;

}
public  ArrayList Collect_N1_Segments(String input_edi){
	
	
	java.util.ArrayList obj= new java.util.ArrayList();
	try{
	// ////////System.out.println("input_edi is **"+input_edi);
	 //String str[]=input_edi.split("~N1");
		String str[]=input_edi.split(segmentTerminator+"N1");
		
		for(int k=0;k<str.length;k++){
			//////////System.out.println(str[k]);
			if(k>0){
				//int IEA_index=str[k].indexOf("~OTI");
				int IEA_index=str[k].indexOf(segmentTerminator+"OTI");
				
				String temp="";
				if(IEA_index>-1){
					
					
					temp="N1"+str[k].substring(0, IEA_index);
					////////System.out.println(temp);
					obj.add(temp);
					
				}else{
					temp="N1"+str[k];
					////////System.out.println(temp);
					obj.add(temp);
					
				}
				}
			}
		
	}catch(Exception e){
		e.printStackTrace();
	}
	return obj;

}
public  ArrayList Collect_OTI_Segments(String input_edi){
		
		
		java.util.ArrayList obj= new java.util.ArrayList();
		try{
		 
		// String str[]=input_edi.split("~OTI");
			 String str[]=input_edi.split(segmentTerminator+"OTI");
			
			for(int k=0;k<str.length;k++){
				
				if(k>0){
					//int IEA_index=str[k].indexOf("~SE");
					int IEA_index=str[k].indexOf(segmentTerminator+"SE");
					
					String temp="";
					if(IEA_index>-1){
						
						
						temp="OTI"+str[k].substring(0, IEA_index);
						//////////////System.out.println(temp);
						obj.add(temp);
						
					}else{
						temp="OTI"+str[k];
						//////////////System.out.println(temp);
						obj.add(temp);
						
					}
					}
				}
			
		}catch(Exception e){
			e.printStackTrace();
		}
		return obj;
	
	}
	
	public  ArrayList Collect_GS_GE_Segments(String input_edi){
		
		
		java.util.ArrayList obj= new java.util.ArrayList();
		try{
		 
		// String str[]=input_edi.split("~GS");
			 String str[]=input_edi.split(segmentTerminator+"GS");
			
			for(int k=0;k<str.length;k++){
				
				if(k>0){
					//int IEA_index=str[k].indexOf("~IEA");
					int IEA_index=str[k].indexOf(segmentTerminator+"IEA");
					
					String temp="";
					if(IEA_index>-1){
						
						
						temp="GS"+str[k].substring(0, IEA_index);
						
						obj.add(temp);
						
					}else{
						temp="GS"+str[k];
						obj.add(temp);
						
					}
					}
				}
			
		}catch(Exception e){
			e.printStackTrace();
		}
		return obj;
	
	}
	
	public  ArrayList Collect_ST_SE_Segments(String input_edi){
		
		java.util.ArrayList obj= new java.util.ArrayList();
		try{
		 
		// String str[]=input_edi.split("~ST");
			
			 String str[]=input_edi.split(segmentTerminator+"ST");
			
			for(int k=0;k<str.length;k++){
				if(k>0){
					//int GE_index=str[k].indexOf("~GE");
					int GE_index=str[k].indexOf(segmentTerminator+"GE");
					String temp="";
					if(GE_index>-1){
						
						temp="ST"+str[k].substring(0, GE_index);
						obj.add(temp);
						
					}else{
						temp="ST"+str[k];
						obj.add(temp);
						
					}
					}
				}
			
		}catch(Exception e){
			e.printStackTrace();
		}
		return obj;
		
	}
public  void Build_OTI_XML(String OTI_XML){
		
	 // int OTI_index=OTI_XML.indexOf("~");
	 int OTI_index=OTI_XML.indexOf(segmentTerminator);
	  String OTI_temp="";

		  if(OTI_index>-1)
			  OTI_temp=OTI_XML.substring(0, OTI_index);
		  else{
			   OTI_temp=OTI_XML;
		  }
	  ////////////System.out.println(OTI_temp);
		//java.util.StringTokenizer stkr_element=new java.util.StringTokenizer(OTI_temp,"*");
		  java.util.StringTokenizer stkr_element=new java.util.StringTokenizer(OTI_temp,fieldTerminator);
		String temp="";
		String result="";
		for(int j=0;stkr_element.hasMoreTokens();j++){
			temp = stkr_element.nextToken();			
			result="";
			 if(j==1)
			 {
				 result="<G_SOTI><S_OTI><D_110>"+temp+"</D_110>";
				 endResult.append(result);
			 }else if(j==2){
				 result="<D_128>"+temp+"</D_128>";
				 endResult.append(result);
			 }else if(j==3){
				 result="<D_127>"+temp+"</D_127>";
				 endResult.append(result);
			 }
		 }
		//////////////System.out.println(endResult);
	}
public  void Build_BGN_XML(String BGN_XML){
	
	//java.util.StringTokenizer stkr_element=new java.util.StringTokenizer(BGN_XML,"*");
	java.util.StringTokenizer stkr_element=new java.util.StringTokenizer(BGN_XML,fieldTerminator);
	String temp="";
	String result="";
	for(int j=0;stkr_element.hasMoreTokens();j++){
		temp = stkr_element.nextToken();			
		result="";
		 if(j==1)
		 {
			 result="<S_BGN><D_353>"+temp+"</D_353>";
			 endResult.append(result);
		 }else if(j==2){
			 result="<D_127>"+temp+"</D_127>";
			 endResult.append(result);
		 }else if(j==3){
			 result="<D_373>"+temp+"</D_373>";
			 endResult.append(result);
		 }else if(j==4){
			 result="<D_337>"+temp+"</D_337>";
			 endResult.append(result);
		 }else if(j==5){
			 result="<D_623>"+temp+"</D_623>";
			 endResult.append(result);
		 }else if(j==6){
			 result="<D_127_2>"+temp+"</D_127_2>";
			 endResult.append(result);
		 }else if(j==7){
			 result="<D_640>"+temp+"</D_640>";
			 endResult.append(result);
		 }
		 
		 
	 }
	endResult.append("</S_BGN>");
	//////////System.out.println(endResult);
}
public  void Build_N1_XML(String N1_XML){
	
	//java.util.StringTokenizer stkr_element=new java.util.StringTokenizer(N1_XML,"*");
	java.util.StringTokenizer stkr_element=new java.util.StringTokenizer(N1_XML,fieldTerminator);
	String temp="";
	String result="";
	for(int j=0;stkr_element.hasMoreTokens();j++){
		temp = stkr_element.nextToken();			
		result="";
		 if(j==1)
		 {
			 result="<G_SN1><S_N1><D_98>"+temp+"</D_98>";
			 endResult.append(result);
		 }else if(j==2){
			 result="<D_93>"+temp+"</D_93>";
			 endResult.append(result);
		 }else if(j==3){
			 result="<D_66>"+temp+"</D_66>";
			 endResult.append(result);
		 }else if(j==4){
			 result="<D_67>"+temp+"</D_67>";
			 endResult.append(result);
		 }
		 
		 
	 }
	
	//////////System.out.println(endResult);
}

public  void Build_N1_PER_XML(String N1_PER_XML){
	
	////////System.out.println(N1_PER_XML);
	//java.util.StringTokenizer stkr_element=new java.util.StringTokenizer(N1_PER_XML,"*");
	java.util.StringTokenizer stkr_element=new java.util.StringTokenizer(N1_PER_XML,fieldTerminator);
	String temp="";
	String result="";
	for(int j=0;stkr_element.hasMoreTokens();j++){
		temp = stkr_element.nextToken();			
		result="";
		 if(j==1)
		 {
			 result="<S_PER><D_366>"+temp+"</D_366>";
			 endResult.append(result);
		 }else  if(j==2){
			 result="<D_93>"+temp+"</D_93>";
			 endResult.append(result);
		 }else if(j==3){
			 result="<D_365>"+temp+"</D_365>";
			 endResult.append(result);
		 }else  if(j==4){
			 result="<D_364>"+temp+"</D_364>";
			 endResult.append(result);
		 }else if(j==5){
			 result="<D_365_2>"+temp+"</D_365_2>";
			 endResult.append(result);
		 }else  if(j==6){
			 result="<D_364_2>"+temp+"</D_364_2>";
			 endResult.append(result);
		 }else if(j==7){
			 result="<D_365_3>"+temp+"</D_365_3>";
			 endResult.append(result);
		 }else  if(j==8){
			 result="<D_364_3>"+temp+"</D_364_3>";
			 endResult.append(result);
		 }else if(j==9){
			 result="<D_443>"+temp+"</D_443>";
			 endResult.append(result);
		 }
		
		 
	 }
	endResult.append("</S_PER>");
	////////////////System.out.println(endResult);
}
public  void Build_OTI_DTM_XML(String SE_XML){
		
	//	java.util.StringTokenizer stkr_element=new java.util.StringTokenizer(SE_XML,"*");
	
	java.util.StringTokenizer stkr_element=new java.util.StringTokenizer(SE_XML,fieldTerminator);
		String temp="";
		String result="";
		for(int j=0;stkr_element.hasMoreTokens();j++){
			temp = stkr_element.nextToken();			
			result="";
			 if(j==1)
			 {
				 result="<S_DTM><D_374>"+temp+"</D_374>";
				 endResult.append(result);
			 }else  if(j==2){
				 result="<D_373>"+temp+"</D_373>";
				 endResult.append(result);
			 }else if(j==3){
				 result="<D_337>"+temp+"</D_337></S_DTM>";
				 endResult.append(result);
			 }
		 }
		////////////////System.out.println(endResult);
	}
	public  void Build_SE_XML(String SE_XML){
		
		//java.util.StringTokenizer stkr_element=new java.util.StringTokenizer(SE_XML,"*");
		java.util.StringTokenizer stkr_element=new java.util.StringTokenizer(SE_XML,fieldTerminator);
		String temp="";
		String result="";
		for(int j=0;stkr_element.hasMoreTokens();j++){
			temp = stkr_element.nextToken();			
			result="";
			 if(j==1)
			 {
				 result="<S_SE><D_96>"+temp+"</D_96>";
				 endResult.append(result);
			 }else if(j==2){
				 result="<D_329>"+temp+"</D_329></S_SE></S_ST>";
				 endResult.append(result);
			 }
		 }
		////////System.out.println(endResult);
	}
public  void Build_GE_XML(String GE_XML){
		
		//java.util.StringTokenizer stkr_element=new java.util.StringTokenizer(GE_XML,"*");
	
	java.util.StringTokenizer stkr_element=new java.util.StringTokenizer(GE_XML,fieldTerminator);
		String temp="";
		String result="";
		for(int j=0;stkr_element.hasMoreTokens();j++){
			temp = stkr_element.nextToken();			
			result="";
			 if(j==1)
			 {
				 result="<S_GE><D_97>"+temp+"</D_97>";
				 endResult.append(result);
			 }else if(j==2){
				 result="<D_28>"+temp+"</D_28></S_GE></S_GS>";
				 endResult.append(result);
			 }
		 }
		////////System.out.println(endResult);
	}
public  void Build_IEA_XML(String IEA_XML){
	
	//java.util.StringTokenizer stkr_element=new java.util.StringTokenizer(IEA_XML,"*");
	java.util.StringTokenizer stkr_element=new java.util.StringTokenizer(IEA_XML,fieldTerminator);
	String temp="";
	String result="";
	for(int j=0;stkr_element.hasMoreTokens();j++){
		temp = stkr_element.nextToken();			
		result="";
		 if(j==1)
		 {
			 result="<S_IEA><D_I16>"+temp+"</D_I16>";
			 endResult.append(result);
		 }else if(j==2){
			 result="<D_I12>"+temp+"</D_I12></S_IEA></S_ISA></LIST>";
			 endResult.append(result);
		 }
	 }
	System.out.println(endResult);
}
	
public  void Build_ST_XML(String SE_XML){
		
		//java.util.StringTokenizer stkr_element=new java.util.StringTokenizer(SE_XML,"*");
	java.util.StringTokenizer stkr_element=new java.util.StringTokenizer(SE_XML,fieldTerminator);
		String temp="";
		String result="";
		for(int j=0;stkr_element.hasMoreTokens();j++){
			temp = stkr_element.nextToken();			
			result="";
			 if(j==1)
			 {
				 result="<S_ST><D_143>"+temp+"</D_143>";
				 endResult.append(result);
			 }else if(j==2){
				 result="<D_329>"+temp+"</D_329>";
				 endResult.append(result);
			 }
		 }
		////////System.out.println(endResult);
	}

public  void Build_GS_XML(String GS_XML){
	
	//java.util.StringTokenizer stkr_element=new java.util.StringTokenizer(GS_XML,"*");
	java.util.StringTokenizer stkr_element=new java.util.StringTokenizer(GS_XML,fieldTerminator);
	String temp="";
	String result="";
	for(int j=0;stkr_element.hasMoreTokens();j++){
		temp = stkr_element.nextToken();			
		result="";
		 if(j==1)
		 {
			 result="<S_GS><D_479>"+temp+"</D_479>";
			 endResult.append(result);
		 }else if(j==2){
			 result="<D_142>"+temp+"</D_142>";
			 endResult.append(result);
		 }else if(j==3)
		 {
			 result="<D_124>"+temp+"</D_124>";
			 endResult.append(result);
		 }else if(j==4){
			 result="<D_373>"+temp+"</D_373>";
			 endResult.append(result);
		 }else if(j==5){
			 result="<D_337>"+temp+"</D_337>";
			 endResult.append(result);
		 }else if(j==6)
		 {
			 result="<D_28>"+temp+"</D_28>";
			 endResult.append(result);
		 }else if(j==7){
			 result="<D_455>"+temp+"</D_455>";
			 endResult.append(result);
		 }else if(j==8)
		 {
			 result="<D_480>"+temp+"</D_480>";
			 endResult.append(result);
		 }
	 }
	//////////System.out.println(endResult);
}

public  void Build_ISA_XML(String ISA_XML){
	
	//java.util.StringTokenizer stkr_element=new java.util.StringTokenizer(ISA_XML,"*");
	java.util.StringTokenizer stkr_element=new java.util.StringTokenizer(ISA_XML,fieldTerminator);
	String temp="";
	String result="";
	for(int j=0;stkr_element.hasMoreTokens();j++){
		temp = stkr_element.nextToken();			
		result="";
		 if(j==1)
		 {
			 result="<LIST><S_ISA><D_I01>"+temp+"</D_I01>";
			 endResult.append(result);
		 }else if(j==2){
			 result="<D_I02>"+temp+"</D_I02>";
			 endResult.append(result);
		 }else if(j==3)
		 {
			 result="<D_I03>"+temp+"</D_I03>";
			 endResult.append(result);
		 }else if(j==4){
			 result="<D_I04>"+temp+"</D_I04>";
			 endResult.append(result);
		 }else if(j==5)
		 {
			 result="<D_I05>"+temp+"</D_I05>";
			 endResult.append(result);
		 }else if(j==6){
			 result="<D_I06>"+temp+"</D_I06>";
			 endResult.append(result);
		 }else if(j==7)
		 {
			 result=" <D_I05_2>"+temp+" </D_I05_2>";
			 endResult.append(result);
		 }else if(j==8){
			 result="<D_I07>"+temp+"</D_I07>";
			 endResult.append(result);
		 }else if(j==9)
		 {
			 result=" <D_I08>"+temp+" </D_I08>";
			 endResult.append(result);
		 }else if(j==10){
			 result="<D_I09>"+temp+"</D_I09>";
			 endResult.append(result);
		 }else if(j==11)
		 {
			 result="<D_I10>"+temp+"</D_I10>";
			 endResult.append(result);
		 }else if(j==12){
			 result="<D_I11>"+temp+"</D_I11>";
			 endResult.append(result);
		 }else if(j==13)
		 {
			 result=" <D_I12>"+temp+" </D_I12>";
			 endResult.append(result);
		 }else if(j==14){
			 result="<D_I13>"+temp+"</D_I13>";
			 endResult.append(result);
		 }else if(j==15)
		 {
			 result="<D_I14>"+temp+"</D_I14>";
			 endResult.append(result);
		 }else if(j==16){
			 result="<D_I15>"+temp+"</D_I15>";
			 endResult.append(result);
		 }
	 }
	//////////System.out.println(endResult);
}

public String convertInputStreamToString(InputStream in) {  
    StringBuffer sb = new StringBuffer();  
    try {  
              InputStreamReader isr = new InputStreamReader(in);  
              Reader reader = new BufferedReader(isr);  
              int ch;  
              while ((ch = in.read()) > -1) {  
                        sb.append((char) ch);  
              }  
              reader.close();  
    } catch (Exception exception) {  
    }  
    return sb.toString();  
}

}
