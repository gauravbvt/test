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

log(">> START DELETE", "info");
var id = new XML(context.sourceAspect("this:param:" + "id", IAspectXmlObject).getXmlObject()).text();
var list = <ids>
							<deleted/>
							<updated/>
					 </ids>;
try {
	beginWrite("DELETE");
	var ids = deleteElement(id); // delete document and cascade delete on references
	for each (id in ids[0]) {
		list.deleted.insertChildAfter(null, <id>{id}</id>);
	}
	for each (id in ids[1]) {
		list.updated.insertChildAfter(null, <id>{id}</id>);
	}
	log("Deleted element and cascaded:\n" + list, "info");
}
finally {
	endWrite("DELETE");
}
// Cut the GoldenThread associated with this resource
cutGoldenThread("gt:element:"+ id);
// Cut the GoldenThread associated with all existing queries
cutGoldenThread("gt:channels");
log("<< END DELETE", "info");


//Return Response
var resp=context.createResponseFrom(new XmlObjectAspect(list.getXmlObject()));
resp.setExpired(); // don't cache
resp.setMimeType("text/xml");
context.setResponse(resp);
