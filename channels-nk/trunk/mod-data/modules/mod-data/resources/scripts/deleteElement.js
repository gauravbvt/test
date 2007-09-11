// Deletes an element from the container

// arguments:
//    id -- the id of the element as canonical document <id>someguid</id>
// returns:
//    a boolean - whether successful

utilsURI = "ffcpl:/libs/utils.js";
context.importLibrary(utilsURI);

importPackage(Packages.org.ten60.netkernel.xml.representation);
importPackage(Packages.com.ten60.netkernel.urii.aspect);
importPackage(Packages.java.lang);

var id = new XML(context.sourceAspect("this:param:" + "id", IAspectXmlObject).getXmlObject()).text();
var list = <ids/>;
try {
	beginWrite();
	var ids = deleteElement(id); // delete document and cascade delete on references
	for each (deleted in ids) {
		list.insertChildAfter(null, <id>{deleted}</id>);
	}
}
finally {
	endWrite();
}
// Cut the GoldenThread associated with this resource
cutGoldenThread("gt:element:"+ id);
// Cut the GoldenThread associated with all existing queries
req=context.createSubRequest("active:cutGoldenThread");
req.addArgument("param", "gt:channels");
res=context.issueSubRequest(req);

//Return Response
var resp=context.createResponseFrom(new XmlObjectAspect(list.getXmlObject()));
resp.setExpired(); // don't cache
resp.setMimeType("text/xml");
context.setResponse(resp);
