// Gets an element as XML document from the container

// arguments:
//    id -- the id of the element as canonical document <id>someguid</id>
// returns:
//    an XML document

utilsURI = "ffcpl:/libs/utils.js";
context.importLibrary(utilsURI);

importPackage(Packages.org.ten60.netkernel.xml.representation);
importPackage(Packages.com.ten60.netkernel.urii.aspect);
importPackage(Packages.java.lang);

op =  getDocumentDescriptor("id");

req=context.createSubRequest("active:dbxmlGetDocument");
req.addArgument("operator", new XmlObjectAspect(op.getXmlObject()) );
result=context.issueSubRequest(req);
log("Got element " + context.transrept(result,IAspectString).getString() + " from container " + dbxml_getContainerName(), "info");

//Return Response
resp=context.createResponseFrom(result);
resp.setMimeType("text/xml");
context.setResponse(resp);


