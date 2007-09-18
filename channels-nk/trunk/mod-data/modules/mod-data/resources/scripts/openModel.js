// Opens a container
// Returns whether the container needed to be created (a boolean)
// Looks up ffcpl:/crud/dbxml_config.xml for container description

// Arguments:
//						init -- The URI of an xml file to initialize the database with when created.
//										The document has the form <db> element* </db>

context.importLibrary("ffcpl:/libs/channels_data.js");

importPackage(Packages.org.ten60.netkernel.xml.representation);
importPackage(Packages.com.ten60.netkernel.urii.aspect);
importPackage(Packages.java.lang);

function initializeFrom(uri) {
	// pause(3000); // wait a bit
  var xml = context.sourceAspect(uri, IAspectString).getString(); // get string aspect first otherwise db.* also gathers whitespace children
  var db = new XML(xml); 
  log("Initializing db with " + db.*.length() + " elements", "info");
	for each (el in db.*) { 
		createElement(el);
	}
	log("Database initialized", "info");
}

try {
	log("Opening database: " + getDatabaseName(), "info");
	var exists = databaseExists();
	if (!exists) {
	  // Create database
	  createDatabase();
  	log("Created database: " + getDatabaseName(), "info");
	
	  // Initialize from file if requested
	  if (context.getThisRequest().argumentExists("init")) {
	  	log("Initializing database from " + context.getThisRequest().getArgument("init"));
	  	initializeFrom("this:param:init");
	  }
	  else { // look for an initialization file at db:<containerName>.xml and use it if there
	  	var uri = "db:" + getDatabaseName() + ".xml";
	  	if (context.exists(uri)) {
	  		initializeFrom(uri);
	  	}
	  }
	}
}
catch(e) {
	log("Open model failed: " + e, "severe");
	throw(e);
}

//Return Response
var resp=context.createResponseFrom(new BooleanAspect(!exists));
resp.setExpired(); // don't cache
resp.setMimeType("text/xml");
context.setResponse(resp);
