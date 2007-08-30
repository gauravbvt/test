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
var deleted = deleteDocument(id);

// Cut the GoldenThread associated with this resource
req=context.createSubRequest("active:cutGoldenThread");
req.addArgument("param", "gt:element:"+ id);
res=context.issueSubRequest(req);

// Cut the GoldenThread associated with all existing queries
req=context.createSubRequest("active:cutGoldenThread");
req.addArgument("param", "gt:channels");
res=context.issueSubRequest(req);

//Return Response
var resp=context.createResponseFrom(new BooleanAspect(deleted));
resp.setExpired(); // don't cache
resp.setMimeType("text/xml");
context.setResponse(resp);
