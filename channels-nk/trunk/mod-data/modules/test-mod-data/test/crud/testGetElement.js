utilsURI = "ffcpl:/test/utils.js";
context.importLibrary(utilsURI);

importPackage(Packages.org.ten60.netkernel.xml.representation);
importPackage(Packages.com.ten60.netkernel.urii.aspect);
importPackage(Packages.org.apache.xmlbeans);
importPackage(Packages.java.lang);

// Create element
var doc = <root>
						 <project>
           		<name>My project</name>
             </project>
           </root>;        
var req = context.createSubRequest("active:channels_data_createElement");
req.addArgument("doc", new XmlObjectAspect(doc.getXmlObject()));
var res = context.issueSubRequest(req);
var el = new  XML(context.transrept(res, IAspectString).getString());
// Get it back
var id = el.id.text();
req = context.createSubRequest("active:channels_data_getElement");
req.addArgument("id", "id:" + id);
res = context.issueSubRequest(req);
// Set response
var resp = context.createResponseFrom(res);
resp.setMimeType("text/xml");
context.setResponse(resp);
