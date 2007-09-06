utilsURI = "ffcpl:/test/utils.js";
context.importLibrary(utilsURI);

importPackage(Packages.org.ten60.netkernel.xml.representation);
importPackage(Packages.com.ten60.netkernel.urii.aspect);
importPackage(Packages.org.apache.xmlbeans);
importPackage(Packages.java.lang);

// Delete model
var req = context.createSubRequest("active:channels_data_deleteModel");
context.issueSubRequest(req);

// Open model from db:testDB.xml
req = context.createSubRequest("active:channels_data_openModel");
req.addArgument("init", "db:testQueriesDB.xml");
context.issueSubRequest(req);

req = context.createSubRequest("active:channels_data_getElement");
req.addArgument("id", "id:person1");
req.addArgument("namesListed", "1"); // value does not matter
res = context.issueSubRequest(req);
// Set response
var resp = context.createResponseFrom(res);
resp.setMimeType("text/xml");
context.setResponse(resp);
