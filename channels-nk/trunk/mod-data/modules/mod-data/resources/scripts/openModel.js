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

function initializeFrom(uri) {
  var xml = context.sourceAspect(uri, IAspectString).getString(); // get string aspect first otherwise db.* also gathers whitespace children
  var db = new XML(xml); 
  log("Initializing db with " + db.*.length() + " elements", "info");
	for each (el in db.*) { 
		createElement(el);
	}
	log("Database initialized", "info");
}

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
  	initializeFrom("this:param:init");
  }
  else { // look for an initialization file at db:<containerName>.xml and use it if there
  	var uri = "db:" + dbxml_getContainerName() + ".xml";
  	if (context.exists(uri)) {
  		initializeFrom(uri);
  	}
  }
}

//Return Response
var resp=context.createResponseFrom(new BooleanAspect(!exists));
resp.setExpired(); // don't cache
resp.setMimeType("text/xml");
context.setResponse(resp);
