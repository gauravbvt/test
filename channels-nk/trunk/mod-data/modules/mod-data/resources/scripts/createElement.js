// Creates a new element

// arguments:
//    doc -- an XML resource with initial values
//    kind -- the element type, e.g. project (all lowercase, multiple words separated by '_') as a string
// returns:
//    the element as  as XML

context.importLibrary("ffcpl:/libs/channels_data.js");

importPackage(Packages.org.ten60.netkernel.xml.representation);
importPackage(Packages.com.ten60.netkernel.urii.aspect);
importPackage(Packages.java.lang);

var doc;
try {
	beginWrite();
	// var kind = new XML(context.sourceAspect("this:param:kind",IAspectXmlObject).getXmlObject()).text();
	var arg = new XML(context.sourceAspect("this:param:doc", IAspectXmlObject).getXmlObject());
	doc = createElement(arg);
}
catch(e) {
	log("Creating element failed: " + e, "severe");
	throw e;
}
finally {
	endWrite();
}
	

//Return Response
var resp=context.createResponseFrom(new XmlObjectAspect(doc.getXmlObject()));
resp.setExpired(); // don't cache
resp.setMimeType("text/xml");
context.setResponse(resp);
