
// Utilities

importPackage(Packages.org.ten60.netkernel.xml.representation);
importPackage(Packages.com.ten60.netkernel.urii.aspect);
importPackage(Packages.java.lang);

LOG_URL = "ffcpl:/etc/LogConfig.xml";

// UTILITIES

function contains(array, object) {
	for each (el in array) {
		if (el == object) return true;
	}
	return false;
}

function log(content, level) {
  var req=context.createSubRequest("active:application-log");
  req.addArgument("operand", new StringAspect(content) );
  req.addArgument("configuration", LOG_URL ); // Defaults to ffcpl:/etc/LogConfig.xml
  var levelXml = "<log>" + "<" + level + "/>" + "</log>";
  req.addArgument("operator", new StringAspect(levelXml) );
//  context.issueAsyncSubRequest(req);
  context.issueSubRequest(req);
}

function expire(uri) {
	var req = context.createSubRequest("active:expire");
	req.addArgument("operand", uri);
	context.issueSubRequest(req);
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


// GOLDEN THREAD

function attachGoldenThread(resource, uri) {
	var req=context.createSubRequest("active:attachGoldenThread");
	req.addArgument("operand", resource);
	req.addArgument("param", uri);
	var result=context.issueSubRequest(req);
	log("Attached GT " + uri, "info");
	return result;
}

function cutGoldenThread(uri) {
	var req=context.createSubRequest("active:cutGoldenThread");
	req.addArgument("param", uri);
	context.issueSubRequest(req);
	log("Cut GT " + uri, "info");
}

function sleep(msecs) {
	log("Sleeping for " + msecs, "info");
	var req = context.createSubRequest("active:sleep");
	var time = <time>
								{msecs}
							</time>;
	req.addArgument("operator", new XmlObjectAspect(time.getXmlObject()));
	context.issueSubRequest(req);
}
