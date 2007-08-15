// Delete container return whether the container was deleted (a boolean)
// Looks up ffcpl:/crud/dbxml_config.xml for container description

utilsURI = "ffcpl:/libs/utils.js";
context.importLibrary(utilsURI);

importPackage(Packages.org.ten60.netkernel.xml.representation);
importPackage(Packages.com.ten60.netkernel.urii.aspect);
importPackage(Packages.java.lang);

descriptor = dbxml_getContainerDescriptor();
exists = dbxml_containerExists(descriptor);

if (exists) {
  // Delete container
  req=context.createSubRequest("active:dbxmlDeleteContainer");
  req.addArgument("operator", new XmlObjectAspect(descriptor.getXmlObject()) );
  context.issueSubRequest(req);
  log("Deleted DBXML container: " + dbxml_getContainerName(), "info");
}
else {
  log("DBXML container not deleted (does not exist): " + dbxml_getContainerName(), "warning");
}

//Return Response
resp=context.createResponseFrom(new BooleanAspect(exists));
resp.setExpired(); // don't cache
resp.setMimeType("text/xml");
context.setResponse(resp);
