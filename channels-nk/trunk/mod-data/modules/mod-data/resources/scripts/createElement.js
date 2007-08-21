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

kind = new XML(context.sourceAspect("this:param:kind",IAspectXmlObject).getXmlObject()).text();
log("Creating element " + kind, "info");
doc = new XML(context.sourceAspect("this:param:doc", IAspectXmlObject).getXmlObject());
log("Creating " + kind + ":\n " + doc, "info");

// Generate and add GUID as root attribute id

guid = new XML(context.sourceAspect("active:guid",IAspectXmlObject).getXmlObject());
guid = guid.text();
id = <id>{guid}</id>;
doc.insertChildAfter(null, id);
doc.@schema = getSchemaURL(kind);

// Document identity
op =  <dbxml>
          <name>{guid}</name>
          <container>{dbxml_getContainerName()}</container>
      </dbxml>;
descriptor = new XmlObjectAspect(op.getXmlObject());

validateRNG(doc); // throws an exception if not valid
req=context.createSubRequest("active:dbxmlPutDocument");
req.addArgument("operand", new XmlObjectAspect(doc.getXmlObject()) );
req.addArgument("operator", descriptor);
result=context.issueSubRequest(req);
log("Put document named " + id + " with " + doc + " in container " + dbxml_getContainerName(), "info");


//Return Response
//result = <root/>;
//result.insertChildAfter(null, doc); // otherwise the root of doc is stripped and replaced by <xml-fragment>. Go figure.
resp=context.createResponseFrom(new XmlObjectAspect(doc.getXmlObject()));
resp.setExpired(); // don't cache
resp.setMimeType("text/xml");
context.setResponse(resp);
