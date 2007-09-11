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
	var isLocking = true;
  try {
  	beginWrite();
  	updateDocument(doc, isLocking);
  }
  finally {
  	endWrite();
  }
}
else {
  throw("Can't update element with no id", "warning");
}

//Return Response
var resp=context.createResponseFrom(new BooleanAspect(true));
resp.setExpired(); // don't cache
context.setResponse(resp);
