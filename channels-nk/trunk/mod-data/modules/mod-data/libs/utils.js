// Utilities

importPackage(Packages.org.ten60.netkernel.xml.representation);
importPackage(Packages.com.ten60.netkernel.urii.aspect);
importPackage(Packages.java.lang);

// Constants

DBXML_CONFIG_URI = "ffcpl:/etc/dbxml_config.xml";
SCHEMA_URL = "ffcpl:/schemas/";
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
function getDocumentDescriptor(param) {
  var id = new XML(context.sourceAspect("this:param:" + param, IAspectXmlObject).getXmlObject());
  var descriptor =  <dbxml>
                  <name>{id.text()}</name>
                  <container>{dbxml_getContainerName()}</container>
                </dbxml>;

  return descriptor;
}

// SHOULD PUT SUBSTITUTIONS IN A SEPARATE CONFIGURATION FILE
function filter(s) {
  // Java strings are NOT the same as JavaScript strings: 
  // make sure to convert to JS strings before using string methods!
  var fs = String(s).replace(/__CONTAINER__/g, String(dbxml_getContainerName())); 
  // more filters here
  return fs;
}

// Add variable declaration to xquery from properties document
function declareVariables(query, properties) {
  var prologuedQuery = query;
  for each (prop in properties.property) {
    var decl = "declare variable $" +  prop.key.text() + " := '" + prop.value.text() + "';\n";
    prologuedQuery = decl + prologuedQuery;
  }
  return prologuedQuery;
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
  if (!valid) throw("Document is invalid");
}
