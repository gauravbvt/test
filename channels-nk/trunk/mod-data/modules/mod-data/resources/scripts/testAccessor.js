importPackage(Packages.org.ten60.netkernel.xml.representation);
importPackage(Packages.com.ten60.netkernel.urii.aspect);
importPackage(Packages.java.lang);

LOG_URL = "ffcpl:/etc/LogConfig.xml";

function log(content, level) {
  var req=context.createSubRequest("active:application-log");
  req.addArgument("operand", new StringAspect(content) );
  req.addArgument("configuration", LOG_URL ); // Defaults to ffcpl:/etc/LogConfig.xml
  var levelXml = "<log>" + "<" + level + "/>" + "</log>";
  req.addArgument("operator", new StringAspect(levelXml) );
//  context.issueAsyncSubRequest(req);
  context.issueSubRequest(req);
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

function grabLock(lock, who) {
	log(who + ": Grab " + lock, "info");
	var req=context.createSubRequest("active:lock");
	req.addArgument("operand","lock:" + lock);
	context.issueSubRequest(req);	
	log(who + ": Grabbed " + lock, "info");
}

function releaseLock(lock, who) {
	log(who + ": Release " + lock, "info");
	var req=context.createSubRequest("active:unlock");
	req.addArgument("operand","lock:" + lock);
	context.issueSubRequest(req);
	log(who + ": Released " + lock, "info");
}

function issueSleepRequest(who) {
	log(who + ": Sleeping ", "info");
	var msecs = Math.round(Math.random() * 100);
	sleep(msecs);
	log(who + ": Waking ", "info");
}

var op = context.getThisRequest().getArgumentValue( "var:operand" );
var who = context.transrept(op, IAspectString).getString();
grabLock("lock:protected", who);
issueSleepRequest(who);
releaseLock("lock:protected", who);

var resp = context.createResponseFrom(new BooleanAspect(true));
context.setResponse(resp);