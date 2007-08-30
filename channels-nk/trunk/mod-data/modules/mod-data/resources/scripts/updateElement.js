// Updates an element as XML document

// arguments:
//    doc -- an XML document
// returns:
//    a boolean - whether an older version was overwritten

utilsURI = "ffcpl:/libs/utils.js";
context.importLibrary(utilsURI);

importPackage(Packages.org.ten60.netkernel.xml.representation);
importPackage(Packages.com.ten60.netkernel.urii.aspect);
importPackage(Packages.java.lang);

var doc = new XML(context.sourceAspect("this:param:doc", IAspectXmlObject).getXmlObject());
validateRNG(doc); // Make sure it is valid
log("Updating with: " + doc, "info");

var id = doc.id.text();
if (id != null) {
  log("Updating element with id " + id, "info");

  // The delete and put operations should be done in a single transaction in case something goes wrong.

  // Delete older version if exists
  var deleted = deleteDocument(id);
  // Then replace with new version
	putDocument(doc);
	
	// Cut the GoldenThread associated with this resource
	req=context.createSubRequest("active:cutGoldenThread");
	req.addArgument("param", "gt:element:"+ id);
	res=context.issueSubRequest(req);
	
	// Cut the GoldenThread associated with all existing queries
	req=context.createSubRequest("active:cutGoldenThread");
	req.addArgument("param", "gt:channels");
	res=context.issueSubRequest(req);
}
else {
  throw("Can't update element with no id", "warning");
}

//Return Response
var resp=context.createResponseFrom(new BooleanAspect(deleted));
resp.setExpired(); // don't cache
context.setResponse(resp);
