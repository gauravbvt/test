importPackage(Packages.com.ten60.netkernel.urii.aspect);
importPackage(Packages.java.lang);

LOG_URL = "ffcpl:/etc/LogConfig.xml";

function log(content, level) {
  var req=context.createSubRequest("active:application-log");
  req.addArgument("operand", new StringAspect(content) );
  req.addArgument("configuration", LOG_URL ); // Defaults to ffcpl:/etc/LogConfig.xml
  var levelXml = "<log>" + "<" + level + "/>" + "</log>";
  req.addArgument("operator", new StringAspect(levelXml) );
  context.issueAsyncSubRequest(req);
}

function issueValidateRNGRequest(schemaURI, docURI) {
	var req = context.createSubRequest("active:validateRNG");
  req.addArgument("operator", schemaURI );
  req.addArgument("operand", docURI)
  res = context.issueSubRequest(req);
  var isValid = context.transrept(res, IAspectBoolean).isTrue();
  if (!isValid) {
  	ex = <ex>
  					<id>Invalid</id>
  					<message>{context.transrept(res, IAspectString).getString()}</message>
  			 </ex>
  	req = context.createSubRequest("active:throw");
  	req.addArgument("operand",ex);
  	context.issueRequest(req);
  }
  return res;
}