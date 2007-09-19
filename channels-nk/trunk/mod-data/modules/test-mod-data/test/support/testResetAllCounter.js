utilsURI = "ffcpl:/test/utils.js";
context.importLibrary(utilsURI);

importPackage(Packages.org.ten60.netkernel.xml.representation);
importPackage(Packages.com.ten60.netkernel.urii.aspect);
importPackage(Packages.org.apache.xmlbeans);
importPackage(Packages.java.lang);

var req = context.createSubRequest("counter:test1");
req.addArgument("operand", new StringAspect("increment"));
context.issueSubRequest(req);

req = context.createSubRequest("counter:test2");
req.addArgument("operand", new StringAspect("increment"));
context.issueSubRequest(req);

req = context.createSubRequest("counter:all");
req.addArgument("operand", new StringAspect("resetAll"));
context.issueSubRequest(req);

req = context.createSubRequest("counter:test1");
req.addArgument("operand", new StringAspect("get"));
var res = context.issueSubRequest(req);
var test1 = parseInt(context.transrept(res,IAspectString).getString());

req = context.createSubRequest("counter:test2");
req.addArgument("operand", new StringAspect("get"));
res = context.issueSubRequest(req);
var test2 = parseInt(context.transrept(res,IAspectString).getString());


// Respond
var resp = context.createResponseFrom(new BooleanAspect((test1 == test2) && (test1 == 0)));
context.setResponse(resp);