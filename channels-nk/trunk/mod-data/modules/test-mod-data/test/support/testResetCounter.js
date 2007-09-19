utilsURI = "ffcpl:/test/utils.js";
context.importLibrary(utilsURI);

importPackage(Packages.org.ten60.netkernel.xml.representation);
importPackage(Packages.com.ten60.netkernel.urii.aspect);
importPackage(Packages.org.apache.xmlbeans);
importPackage(Packages.java.lang);

var req = context.createSubRequest("counter:test");
req.addArgument("operand", new StringAspect("reset"));
var res = context.issueSubRequest(req);
var count = parseInt(context.transrept(res, IAspectString).getString());

// Respond
var resp = context.createResponseFrom(new BooleanAspect(count == 0));
context.setResponse(resp);