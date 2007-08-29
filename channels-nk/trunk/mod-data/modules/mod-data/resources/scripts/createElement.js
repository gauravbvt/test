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

var kind = new XML(context.sourceAspect("this:param:kind",IAspectXmlObject).getXmlObject()).text();
log("Creating element " + kind, "info");
var doc = new XML(context.sourceAspect("this:param:doc", IAspectXmlObject).getXmlObject());
log("Creating " + kind + ":\n " + doc, "info");

// Generate and add GUID as root attribute id if needed

if (doc.id.length() == 0) {
	var guid = new XML(context.sourceAspect("active:guid",IAspectXmlObject).getXmlObject()).text();
	var id = <id>{guid}</id>;
	doc.insertChildAfter(null, id);
}
else {
	log("ID preset "+ doc.id, "warning");
}
// (Re)set schema attribute
doc.@schema = getSchemaURL(kind);

// Document identity
descriptor =  getDocumentDescriptor(doc.id.text());

try {
	validateRNG(doc); // throws an exception if not valid
}
catch(e) {
	log("Document of kind " + kind + " is invalid:\n" + doc, "severe");
	throw (e);
}

putDocument(doc);
var req=context.createSubRequest("active:dbxmlPutDocument");
log("Created document named " + id + " with " + doc + " in container " + dbxml_getContainerName(), "info");

//Return Response
var resp=context.createResponseFrom(new XmlObjectAspect(doc.getXmlObject()));
resp.setExpired(); // don't cache
resp.setMimeType("text/xml");
context.setResponse(resp);
