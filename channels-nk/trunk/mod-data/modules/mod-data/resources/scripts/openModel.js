// Opens a container
// Returns whether the container needed to be created (a boolean)
// Looks up ffcpl:/crud/dbxml_config.xml for container description

// Arguments:
//						init -- The URI of an xml file to initialize the database with when created.
//										The document has the form <db> element* </db>

utilsURI = "ffcpl:/libs/utils.js";
context.importLibrary(utilsURI);

importPackage(Packages.org.ten60.netkernel.xml.representation);
importPackage(Packages.com.ten60.netkernel.urii.aspect);
importPackage(Packages.java.lang);

log("Opening DBXML container: " + dbxml_getContainerName(), "info");
var descriptor = dbxml_getContainerDescriptor();
var exists = dbxml_containerExists(descriptor);

if (!exists) {
  // Create container
  var req=context.createSubRequest("active:dbxmlCreateContainer");
  req.addArgument("operator", new XmlObjectAspect(descriptor.getXmlObject()) );
  context.issueSubRequest(req);
  log("Created DBXML container: " + dbxml_getContainerName(), "info");
  
  // Initialize from file if requested
  if (context.getThisRequest().argumentExists("init")) {
  	log("Initializing database from " + context.getThisRequest().getArgument("init"));
  	var xml = context.sourceAspect("this:param:init", IAspectString).getString();
  	var db = new XML(xml); // otherwise db.* also gathers whitespace children
  	log("Initializing db with " + db.*.length() + " elements", "info");
  	for each (el in db.*) { 
  		createElement(el);
  	}
  	log("Database initialized", "info");
  }
}

//Return Response
var resp=context.createResponseFrom(new BooleanAspect(!exists));
resp.setExpired(); // don't cache
resp.setMimeType("text/xml");
context.setResponse(resp);
