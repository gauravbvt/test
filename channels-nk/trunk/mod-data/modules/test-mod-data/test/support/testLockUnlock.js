importPackage(Packages.com.ten60.netkernel.urii.aspect);
utilsURI = "ffcpl:/com/mindalliance/channels/library/scripts/utils.js";
context.importLibrary(utilsURI);

var RUNS = 5;

var operands = [];
var requestHandles = [];

for (i=1; i< RUNS; i++) {
	operands[i] = ""+i;
}

for (i in operands) {
	var req = context.createSubRequest("js:testAccessor");
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