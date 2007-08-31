importPackage(Packages.com.ten60.netkernel.urii.aspect);
importPackage(Packages.java.lang);

LOG_URL = "ffcpl:/etc/LogConfig.xml";

function log(content, level) {
  var req=context.createSubRequest("active:application-log");
  req.addArgument("operand", new StringAspect(content) );
  req.addArgument("configuration", LOG_URL ); // Defaults to ffcpl:/etc/LogConfig.xml
  var levelXml = "<log>" + "<" + level + "/>" + "</log>";
  req.addArgument("operator", new StringAspect(levelXml) );
  context.issueSubRequest(req);
}

function pause(millis) {
	java.lang.Thread.sleep(millis);
} 

function issueValidateRNGRequest(schemaURI, docURI) {
	log("Schema uri = " + schemaURI + ", docURI = " + docURI, "info");
	var req = context.createSubRequest("active:validateRNG");
  req.addArgument("operator", schemaURI );
  req.addArgument("operand", docURI);
  try {
  	var res = context.issueSubRequest(req);
  	var valid = context.transrept(res, IAspectBoolean).isTrue();
  	if (!valid) {
  		log("Validation failed for " + docURI, "severe");
  		throw("Invalid " + docURI);
  	}
  	
  	return valid;
  }
  catch (e) {
  	log("Validation exception " + new String(e), "severe");
  	throw("Validation error");
  }
}

function createElement(doc, kind) {
	var req = context.createSubRequest("active:channels_data_createElement");
	req.addArgument("doc", new XmlObjectAspect(doc.getXmlObject()));
	req.addArgument("kind", new StringAspect("<string>"+kind+"</string>"));
	var res = context.issueSubRequest(req);
	xml = new  XML(res.transrept(res, IAspectXmlObject).getXmlObject());
	return xml;
}