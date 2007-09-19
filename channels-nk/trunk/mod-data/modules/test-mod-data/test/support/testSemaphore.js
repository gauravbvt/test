utilsURI = "ffcpl:/test/utils.js";
context.importLibrary(utilsURI);

importPackage(Packages.org.ten60.netkernel.xml.representation);
importPackage(Packages.com.ten60.netkernel.urii.aspect);
importPackage(Packages.org.apache.xmlbeans);
importPackage(Packages.java.lang);

var req = context.createSubRequest("sem:all");
req.addArgument("operand", new StringAspect("reset"));
context.issueSubRequest(req);

req = context.createSubRequest("sem:test");
req.addArgument("operand", new StringAspect("signal"));
context.issueSubRequest(req);

req = context.createSubRequest("sem:test1");
req.addArgument("operand", new StringAspect("signal"));
context.issueSubRequest(req);

req = context.createSubRequest("sem:test1");
req.addArgument("operand", new StringAspect("signal"));
context.issueSubRequest(req);

req = context.createSubRequest("sem:test");
req.addArgument("operand", new StringAspect("wait"));
context.issueSubRequest(req);

req = context.createSubRequest("sem:test1");
req.addArgument("operand", new StringAspect("wait"));
context.issueSubRequest(req);

req = context.createSubRequest("sem:test1");
req.addArgument("operand", new StringAspect("wait"));
context.issueSubRequest(req);

// Respond
var resp = context.createResponseFrom(new BooleanAspect(true));
context.setResponse(resp);