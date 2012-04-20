package com.mindalliance.configurations;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.cert.X509Certificate;
import java.util.StringTokenizer;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.ws.http.HTTPException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/** The Configurations class is a collection of methods which sends HttpURLConnection request to the Web service and gets response and retrieves test data.
 * @author AFourTech
 */

public class Configurations {
	
	/** This method creates a trust manager that does not validate certificate chains.
	 * 
	 */
	public static void validateTrustManager() {
		try {
			// Create a trust manager that does not validate certificate chains
			TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
				public X509Certificate[] getAcceptedIssuers() {
					return null;
				}

				public void checkClientTrusted(X509Certificate[] certs,String authType) {
					// Trust always
				}

				public void checkServerTrusted(X509Certificate[] certs,String authType) {
					// Trust always
				}
			} };

			// Install the all-trusting trust manager
			SSLContext sc = SSLContext.getInstance("SSL");
			// Create empty HostnameVerifier
			HostnameVerifier hv = new HostnameVerifier() {
				public boolean verify(String arg0, SSLSession arg1) {
					return true;
				}
			};

			sc.init(null, trustAllCerts, new java.security.SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
			HttpsURLConnection.setDefaultHostnameVerifier(hv);
		} catch (HTTPException e) {
			System.out.println(e.getMessage());
		} catch (GeneralSecurityException ge) {
			// TODO Auto-generated catch block
			ge.printStackTrace();
		}
	}
	
	/** This method retrieve data from CSV file and checks for the key in the CSV file which can be used through out the application
	 * @param CSV File Name
	 * @throws Exception
	 */
	
	public static String getTestData(String csvFileName) throws Exception {
		try {
			// CSV file containing data
			GlobalVariables.testDataDirectoryPath = GlobalVariables.currentDirectory.getCanonicalPath().toString() + "\\TestData\\";
			File testDataFile = new File(GlobalVariables.testDataDirectoryPath+ csvFileName);
			BufferedReader testData = new BufferedReader(new FileReader(testDataFile));
			String strLine = "", key = "", value = "";
			StringTokenizer st = null;
			while ((strLine = testData.readLine()) != null) {
				st = new StringTokenizer(strLine, ",");
				while (st.hasMoreTokens()) {
					key = st.nextToken();
					value = st.nextToken();
					GlobalVariables.testData.put(key, value);
				}
			}
		} catch (FileNotFoundException file) {
			System.out.println(file.getMessage());
		}
		return csvFileName;
	}
	
	/** Sending request using HttpURLConnection 
	 * Each HttpURLConnection instance is used to make a single request but the underlying network connection 
	 * to the HTTP server may be transparently shared by other instances
	 * @param data
	 *
	 */
	public static void sendRequest(String data) {
		try {
			URL url = new URL(GlobalVariables.testData.get("api"));		
			GlobalVariables.connection = (HttpURLConnection) url.openConnection();
			GlobalVariables.connection.setRequestMethod("GET");
			
			GlobalVariables.userCredentials=GlobalVariables.testData.get("username")+":"+GlobalVariables.testData.get("password");
			String encodedAuthorization = org.apache.commons.codec.binary.Base64.encodeBase64String(GlobalVariables.userCredentials.getBytes());
			GlobalVariables.connection.setRequestProperty("Authorization", "Basic "+encodedAuthorization);
		      
		} catch (MalformedURLException e) {
	          e.printStackTrace();
		} catch (ProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	  }
	
	/** This method gets response from web service and stores the response in response.xml file
	 * @return response
	 */
	public static String getResponse() {
		try {
			GlobalVariables.responseString = "";
			InputStream response = GlobalVariables.connection.getInputStream();
			int respNum = response.read();
			GlobalVariables.testDataDirectoryPath = GlobalVariables.currentDirectory.getCanonicalPath().toString() + "\\TestData\\";
			File responseFile = new File(GlobalVariables.testDataDirectoryPath+ "response.xml");
			FileWriter fstream = new FileWriter(responseFile);
			BufferedWriter out = new BufferedWriter(fstream);
			out.write("<");
			if (respNum != -1) {
				BufferedReader in = new BufferedReader(new InputStreamReader(response));
				String inputLine;
				while ((inputLine = in.readLine()) != null) {
					GlobalVariables.responseString = GlobalVariables.responseString+ inputLine;
					System.out.println(inputLine);
					out.write(inputLine);
					out.newLine();
				}
			}
			out.close();
			response.close();
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
		return GlobalVariables.responseString;
	}
	
	public static boolean parseResponse(String nodename,String value,String item){
		try{
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
	        DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
	        Document doc = docBuilder.parse (new File("Testdata//response.xml"));
	        
	        // normalize text representation
	        doc.getDocumentElement ().normalize ();
	        System.out.println ("Root element of the doc is " + doc.getDocumentElement().getNodeName());
	        
	        NodeList node = doc.getElementsByTagName(nodename);
            int totalPersons = node.getLength();
            System.out.println("Total no of node : " + totalPersons);
            
            for(int s=0; s<node.getLength() ; s++){
                Node firstPersonNode = node.item(s);
                if(firstPersonNode.getNodeType() == Node.ELEMENT_NODE){
                    Element firstPersonElement = (Element)firstPersonNode;
                    NodeList firstNameList = firstPersonElement.getElementsByTagName(value);
                    Element firstNameElement = (Element)firstNameList.item(0);
                    NodeList textFNList = firstNameElement.getChildNodes();
                    if(item.equals(((Node)textFNList.item(0)).getNodeValue().trim()))
                    {                    
                    	return true;
                    }
                    System.out.println("Attribute: " + ((Node)textFNList.item(0)).getNodeValue().trim());
                }
                
            }
		}catch(SAXParseException err) {
	    	System.out.println ("** Parsing error" + ", line " + err.getLineNumber () + ", uri " + err.getSystemId ());
	    	System.out.println(" " + err.getMessage ());
	    }catch (SAXException e) {
	    	Exception x = e.getException ();
	    	((x == null) ? e : x).printStackTrace ();
	    }catch (Throwable t) {
	    	t.printStackTrace ();
	    }
		return false;
	}	
}