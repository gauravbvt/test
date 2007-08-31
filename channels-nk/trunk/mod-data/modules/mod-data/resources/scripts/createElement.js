// Creates a new element

// arguments:
//    doc -- an XML resource with initial values
//    kind -- the element type, e.g. project (all lowercase, multiple words separated by '_') as a string
// returns:
//    the element as  as XML

utilsURI = "ffcpl:/libs/utils.js";
context.importLibrary(utilsURI);

importPackage(Packages.org.ten60.netkernel.xml.representation);
importPackage(Packages.com.ten60.netkernel.urii.aspect);
importPackage(Packages.java.lang);

try {
	// var kind = new XML(context.sourceAspect("this:param:kind",IAspectXmlObject).getXmlObject()).text();
	var doc = new XML(context.sourceAspect("this:param:doc", IAspectXmlObject).getXmlObject());
	doc = createElement(doc);
	
	// Cut the GoldenThread associated with all existing queries
	req=context.createSubRequest("active:cutGoldenThread");
	req.addArgument("param", "gt:channels");
	res=context.issueSubRequest(req);
}
catch(e) {
	log("Creating element failed: " + e, "severe");
	throw e;
}
	

//Return Response
var resp=context.createResponseFrom(new XmlObjectAspect(doc.getXmlObject()));
resp.setExpired(); // don't cache
resp.setMimeType("text/xml");
context.setResponse(resp);
