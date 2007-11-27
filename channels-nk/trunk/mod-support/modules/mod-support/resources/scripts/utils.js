// Import packages needed or likely to be needed (no conflicts)
importPackage(Packages.org.ten60.netkernel.xml.representation);
importPackage(Packages.org.ten60.netkernel.layer1.nkf);
importPackage(Packages.org.ten60.netkernel.layer1.nkf.impl);
importPackage(Packages.org.apache.xmlbeans);
importPackage(Packages.java.lang);

LOG_URL = "ffcpl:/etc/LogConfig.xml";

function request(uri, args, type) {
	var reqType = (type == null)?"source":type;
	var reqArgs = (args == null)?{}:args;
	var req = context.createSubRequest(uri);
	req.setRequestType(eval("INKFRequestReadOnly.RQT_" + reqType.toUpperCase()));
	for (name in reqArgs) {
		var value = reqArgs[name];
		if (name == "SYSTEM") {
			req.addSystemArgument(value);
		}
		else {
			req.addArgument(name, value);
		}
	}
 	return context.issueSubRequest(req);
}

function respond(res, mimeType) {
	var resp = context.createResponseFrom(res);
	if (mimeType != null) resp.setMimeType(mimeType);
	context.setResponse(resp);
}

function log(content, level) {
  request("active:application-log", {operand: new StringAspect(content), 
  																	 configuration: LOG_URL, 
  																	 operator: new StringAspect("<log>" + "<" + level + "/>" + "</log>")});
}

function reloadDatabase(name) {
	// Delete model
	request("active:crud_database", {operator:asData("delete"), name:asData(name)});
	request("active:crud_database", {operator:asData("open"), name:asData(name)});
	request("active:crud_database", {operator:asData("load"), name:asData(name), init:"db:" + name});
	resetAll();
}

function resetAll() {
	log("Resetting all counters", "info");
	request("active:crud_mrew", {operator:new StringAspect("reset")});
	log("All counters reset", "info");
}

/*
function issueValidateRNGRequest(schemaURI, docURI) {
	log("Schema uri = " + schemaURI + ", docURI = " + docURI, "info");
	try {
		var res = request("active:validateRNG", {operator:schemaURI, operand:docURI});
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
*/

function asData(value) {
	return "data:text/plain," + escape(value.toString());
}
