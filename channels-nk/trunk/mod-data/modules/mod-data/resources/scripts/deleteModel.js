// Delete container return whether the container was deleted (a boolean)
// Looks up ffcpl:/crud/dbxml_config.xml for container description

utilsURI = "ffcpl:/libs/utils.js";
context.importLibrary(utilsURI);

importPackage(Packages.org.ten60.netkernel.xml.representation);
importPackage(Packages.com.ten60.netkernel.urii.aspect);
importPackage(Packages.java.lang);

var descriptor = dbxml_getContainerDescriptor();
var exists = dbxml_containerExists(descriptor);
var maxRetries = 20;
while (dbxml_containerExists(descriptor) && (maxRetries > 0)) {
	maxRetries--;
	try {
		var exists = dbxml_containerExists(descriptor);
		if (exists) {
		  // Delete container
		  var req=context.createSubRequest("active:dbxmlDeleteContainer");
		  req.addArgument("operator", new XmlObjectAspect(descriptor.getXmlObject()) );
		  context.issueSubRequest(req);
		  // pause(3000);
		  log("Deleted DBXML container: " + dbxml_getContainerName(), "info");
		}
		else {
		  log("DBXML container not deleted (does not exist): " + dbxml_getContainerName(), "warning");
		}
	}
	catch(e) {
		log("Delete model failed: " + e, "severe");
		// pause(1000);
		// throw e;
	}
}

//Return Response
var resp=context.createResponseFrom(new BooleanAspect(exists));
resp.setExpired(); // don't cache
resp.setMimeType("text/xml");
context.setResponse(resp);
