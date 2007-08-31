utilsURI = "ffcpl:/test/utils.js";
context.importLibrary(utilsURI);

importPackage(Packages.org.ten60.netkernel.xml.representation);
importPackage(Packages.com.ten60.netkernel.urii.aspect);
importPackage(Packages.org.apache.xmlbeans);
importPackage(Packages.java.lang);

// Delete and re-open model loading testDB.xml
try {
	// Delete model
	var req = context.createSubRequest("active:channels_data_deleteModel");
	context.issueSubRequest(req);
	
	// Open model from db:testDB.xml
	req = context.createSubRequest("active:channels_data_openModel");
	req.addArgument("init", "db:testTemplateDB.xml");
	context.issueSubRequest(req);
	
	
	// Get element event1's information template
	req = context.createSubRequest("active:channels_data_getInformationTemplate");
	req.addArgument("id", "id:event1");
	var template = context.issueSubRequest(req);
}
catch(e) {
	log("EXCEPTION: " + e, "severe");
	throw e;
}

// Respond
var resp = context.createResponseFrom(template);
resp.setMimeType("text/xml");
context.setResponse(resp);