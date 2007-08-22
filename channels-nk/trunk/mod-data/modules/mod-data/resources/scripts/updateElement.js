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

var updated = false; // whether a prior version was deleted
var doc = new XML(context.sourceAspect("this:param:doc", IAspectXmlObject).getXmlObject());
validateRNG(doc); // Make sure it is valid
log("Updating with: " + doc, "info");

var id = doc.id.text();
if (id != null) {
  log("Updating element with id " + id, "info");

  // Document identity
  var op =  <dbxml>
           <name>{id}</name>
           <container>{dbxml_getContainerName()}</container>
        </dbxml>;

  log("Using descriptor " + op, "info");

  // The delete and put operations should be done in a single transaction in case something goes wrong.

  // Delete older version if exists
  try {
    var req=context.createSubRequest("active:dbxmlDeleteDocument");
    req.addArgument("operator", new XmlObjectAspect(op.getXmlObject()));
    var result=context.issueSubRequest(req);
    log("Deleted older version of " + id + " from container " + dbxml_getContainerName(), "info");
    updated = true;
  }
  catch(e) { // RISKY: hides all other failures
    log("No older version of " + id + " deleted from container " + dbxml_getContainerName(), "warning");
  }

  req=context.createSubRequest("active:dbxmlPutDocument");
  req.addArgument("operand", new XmlObjectAspect(doc.getXmlObject()));
  req.addArgument("operator", new XmlObjectAspect(op.getXmlObject()));
  context.issueSubRequest(req);
  log("Element " + id + " put in container " + dbxml_getContainerName(), "info");
}
else {
  throw("Can't update element with no id", "warning");
}

//Return Response
var resp=context.createResponseFrom(new BooleanAspect(updated));
resp.setExpired(); // don't cache
context.setResponse(resp);
