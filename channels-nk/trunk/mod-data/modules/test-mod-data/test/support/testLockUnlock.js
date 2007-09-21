utilsURI = "ffcpl:/test/utils.js";
context.importLibrary(utilsURI);

importPackage(Packages.org.ten60.netkernel.xml.representation);
importPackage(Packages.com.ten60.netkernel.urii.aspect);
importPackage(Packages.org.apache.xmlbeans);
importPackage(Packages.java.lang);

var RUNS = 5;

var operands = [];
var requestHandles = [];

for (i=1; i< RUNS; i++) {
	operands[i] = ""+i;
}

for (i in operands) {
	var req = context.createSubRequest("active:channels_data_testAccessor");
	req.addArgument("operand", new StringAspect(operands[i]));
	requestHandles[i] = context.issueAsyncSubRequest(req);
}

var success = true;

//Wait for all requests to complete
try {
	for each (handle in requestHandles) {
		handle.join(); 
	}
}
catch (e)	{
	log("All requests did not complete: " + e, "severe");
	success = false;
}

// Respond
var resp = context.createResponseFrom(new BooleanAspect(success));
context.setResponse(resp);