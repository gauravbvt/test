//package com.mindalliance.testscripts;
//
//import java.io.BufferedReader;
//import java.io.FileNotFoundException;
//import java.io.FileReader;
//import java.io.IOException;
//import java.util.StringTokenizer;
//
//public class ReadAndPrintXMLFile {
//	public static boolean parse(String item){
//	    
//	    	// TODO Auto-generated method stub
//	    	  String fileName="D:\\channels\\Mind-AllianceApiTesting\\TestData\\REST001_ISPPlans.csv";
//	    	  boolean isItemPresent=false;
//	    	  try {
//	    	   BufferedReader br = new BufferedReader( new FileReader(fileName));
//	    	   String strLine = null;
//	    	   StringTokenizer st = null;
//	    	   int lineNumber = 0, tokenNumber = 0;  
//	    	   while( (fileName = br.readLine()) != null){
//	    	    lineNumber++;
//	    	    //break comma separated line using ","
//	    	    st = new StringTokenizer(fileName, ",");
//	    	    while(st.hasMoreTokens()){
//	    	     //display csv values
//	    	    	
//	    	   // System.out.println("Line-"+lineNumber+",Token -"+ st.nextToken());
//	    	    tokenNumber++;
//	     	    //System.out.println("Line-"+lineNumber+",Token value-"+ st.nextToken());
//	     	    if(item.equals(st.nextToken()))
//    	    	{
//    	    		isItemPresent=true;
//    	    		break;
//    	    	}
//	    	    }
//	    	    //reset token number
//	    	    tokenNumber = 0;
//	    	   }
//	    	  
//	    	  } 
//	    	  catch (FileNotFoundException e) {
//	    	   // TODO Auto-generated catch block
//	    	   e.printStackTrace();
//	    	  } catch (IOException e) {
//	    	   // TODO Auto-generated catch block
//	    	   e.printStackTrace();
//	    	  }
//			return isItemPresent;
//			
//	    }
//	
//}
