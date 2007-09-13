utilsURI = "ffcpl:/test/utils.js";
context.importLibrary(utilsURI);

importPackage(Packages.org.ten60.netkernel.xml.representation);
importPackage(Packages.com.ten60.netkernel.urii.aspect);
importPackage(Packages.org.apache.xmlbeans);
importPackage(Packages.java.lang);

reloadModel();

req = context.createSubRequest("active:channels_data_getElement");
req.addArgument("id", "id:event1");
req.addArgument("nameReferenced", "true"); // value does not matter
res = context.issueSubRequest(req);
// Set response
var resp = context.createResponseFrom(res);
resp.setMimeType("text/xml");
context.setResponse(resp);
