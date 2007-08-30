// Utilities

importPackage(Packages.org.ten60.netkernel.xml.representation);
importPackage(Packages.com.ten60.netkernel.urii.aspect);
importPackage(Packages.java.lang);

// Constants

DBXML_CONFIG_URI = "ffcpl:/etc/dbxml_config.xml";
SCHEMA_URL = "http://@host@:@port@/channels/schema/"; 
LOG_URL = "ffcpl:/etc/LogConfig.xml";

function log(content, level) {
  var req=context.createSubRequest("active:application-log");
  req.addArgument("operand", new StringAspect(content) );
  req.addArgument("configuration", LOG_URL ); // Defaults to ffcpl:/etc/LogConfig.xml
  var levelXml = "<log>" + "<" + level + "/>" + "</log>";
  req.addArgument("operator", new StringAspect(levelXml) );
  context.issueAsyncSubRequest(req);
}

// Returns whether a dbxml container as described exists
function dbxml_containerExists(descriptor) {
  // Check if already exists
  var req=context.createSubRequest("active:dbxmlExistsContainer");
  req.addArgument("operator", new XmlObjectAspect(descriptor.getXmlObject()) );
  res = context.issueSubRequest(req);
  return context.transrept(res, IAspectBoolean).isTrue();
}

// Returns an E4X dbxml descriptor
function dbxml_getContainerDescriptor() {
  var configs = new XML(context.sourceAspect(DBXML_CONFIG_URI, IAspectXmlObject).getXmlObject());
  var env = configs.@env;
  var descriptor = configs.config.(@name==env).dbxml;
  // log("Using dbxml descriptor: " + descriptor, "info");
  return descriptor;
}

function dbxml_getContainerName() {
  var descriptor = dbxml_getContainerDescriptor();
  return descriptor.name.text();
}

// Return a document descriptor given id in a parameter
function getDocumentDescriptor(id) {
  var descriptor =  <dbxml>
                  		<name>{id}</name>
                  		<container>{dbxml_getContainerName()}</container>
                		</dbxml>;

  return descriptor;
}

// SHOULD PUT SUBSTITUTIONS IN A SEPARATE CONFIGURATION FILE
function filter(s) {
  // Java strings are NOT the same as JavaScript strings: 
  // make sure to convert to JS strings before using string methods!
  var fs = String(s).replace(/__MODEL__/g, String(dbxml_getContainerName())); 
  // more filters here
  return fs;
}

function getSchemaURL(kind) {
  return SCHEMA_URL + kind + ".rng";
}

function validateRNG(doc) { // doc is an E4X XML object
  var schemaURL = doc.@schema;
  if (schemaURL == null) throw("Missing schema attribute");
  var req = context.createSubRequest("active:validateRNG");
  req.addArgument("operand", new XmlObjectAspect(doc.getXmlObject()));
  req.addArgument("operator", schemaURL);
  result = context.issueSubRequest(req);
  valid = context.transrept(result, IAspectBoolean).isTrue();
  if (!valid) {
  	var problem = context.transrept(result, IAspectString).getString();
  	throw("Document is invalid: " + problem);
  }
}

// Get stored document as e4x xml object given its id as string
function getDocument(id) {
	var op =  getDocumentDescriptor(id);	
	// log("Document descriptor: " + op, "info");
	var req = context.createSubRequest("active:dbxmlGetDocument");
	req.addArgument("operator", new XmlObjectAspect(op.getXmlObject()) );
	var doc = context.transrept(context.issueSubRequest(req), IAspectXmlObject);
  log("Got document " + context.transrept(doc,IAspectString).getString(), "info");
	return new XML(doc.getXmlObject());
}

// Store e4x doc in currently opened database
function putDocument(doc, op) {
	var id = doc.id[0].text(); // document *must* have id
  var op =  getDocumentDescriptor(id);
  var req=context.createSubRequest("active:dbxmlPutDocument");
  req.addArgument("operand", new XmlObjectAspect(doc.getXmlObject()));
  req.addArgument("operator", new XmlObjectAspect(op.getXmlObject()));
  context.issueSubRequest(req);
  log("Element " + id + " put in container " + dbxml_getContainerName(), "info");	
}

// Deletes document by name. if exists. Returns whether existed.
function deleteDocument(id) {
	log("Deleting element " + id, "info");
	var deleted = false;
  var op =  getDocumentDescriptor(id);
  try {
    var req=context.createSubRequest("active:dbxmlDeleteDocument");
    req.addArgument("operator", new XmlObjectAspect(op.getXmlObject()));
    context.issueSubRequest(req);
    log("Deleted older version of " + id + " from container " + dbxml_getContainerName(), "info");
    deleted = true;
  }
  catch(e) { // RISKY: hides all other failures
    log("No older version of " + id + " deleted from container " + dbxml_getContainerName(), "warning");
  }
  return deleted;
}

// Adds id element if not there, sets schema attribute, validates and stores. Returns doc as stored.
function createElement(xml) {
	var doc = xml;
	var kind = doc.name();
	log("Creating " + kind + ":\n " + doc, "info");
	// Generate and add GUID as root attribute id if needed	
	if (doc.id.length() == 0) {
		var guid = new XML(context.sourceAspect("active:guid",IAspectXmlObject).getXmlObject()).text();
		var id = <id>{guid}</id>;
		doc.insertChildAfter(null, id);
	}
	else {
		log("ID preset "+ doc.id, "warning");
	}
	// (Re)set schema attribute
	doc.@schema = getSchemaURL(kind);
	// Document identity
	descriptor =  getDocumentDescriptor(doc.id.text());
	// Validate
	try {
		validateRNG(doc); // throws an exception if not valid
	}
	catch(e) {
		log("Document of kind " + kind + " is invalid:\n" + doc, "severe");
		throw (e);
	}
	// Store new document (exception if conflict on id)
	putDocument(doc);
	log("Created document " + doc + " in container " + dbxml_getContainerName(), "info");
	return doc;
}


