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

log(">> START UPDATE", "info");
var doc = new XML(context.sourceAspect("this:param:doc", IAspectXmlObject).getXmlObject());
try {
	validateRNG(doc); // Make sure it is valid
}
catch (e) {
	log("Update invalid: \n" + doc + "\n " + e, "severe");
	throw(e);
}
log("Updating with: " + doc, "info");
var id = doc.id.text();
if (id != null) {
  try {
  	beginWrite("UPDATE");
  	updateDocument(doc);
  }
  finally {
  	endWrite("UPDATE");
  }
}
else {
  throw("Can't update element with no id", "warning");
}
log("<< END UPDATE", "info");

//Return Response
var resp=context.createResponseFrom(new BooleanAspect(true));
resp.setExpired(); // don't cache
context.setResponse(resp);
