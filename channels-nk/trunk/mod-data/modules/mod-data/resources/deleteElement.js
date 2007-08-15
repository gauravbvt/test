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

descriptor =  getDocumentDescriptor("id");

succeeded = false;
try {
  req=context.createSubRequest("active:dbxmlDeleteDocument");
  req.addArgument("operator", new XmlObjectAspect(descriptor.getXmlObject()) );
  result=context.issueSubRequest(req);
  log("Deleted element " + descriptor.name + " from container " + dbxml_getContainerName(), "info");
  succeeded = true;
}
catch(e) {
  log("Failed to delete element: " + descriptor.name + ": " + e, "severe");
}

//Return Response
resp=context.createResponseFrom(new BooleanAspect(succeeded));
resp.setExpired(); // don't cache
resp.setMimeType("text/xml");
context.setResponse(resp);
