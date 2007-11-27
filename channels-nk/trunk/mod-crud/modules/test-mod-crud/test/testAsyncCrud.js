importPackage(Packages.com.ten60.netkernel.urii.aspect);
utilsURI = "ffcpl:/com/mindalliance/channels/support/scripts/utils.js";
context.importLibrary(utilsURI);

reloadDatabase("test");

var success = true;
var requests = [];

// Request 0 - CREATE
var req = context.createSubRequest("active:crud_document");
req.setRequestType(eval("INKFRequestReadOnly.RQT_NEW"));
req.addArgument("database", "data:text/plain,test");
var doc = "<project><name>My project</name></project>";
req.addArgument("doc", new StringAspect(doc));
requests[0] = req;

// Request 1 - DELETE
req = context.createSubRequest("active:crud_document");
req.setRequestType(eval("INKFRequestReadOnly.RQT_DELETE"));
req.addArgument("database", "data:text/plain,test");
req.addArgument("id", "data:text/plain,person1");
req.addArgument("refTable", "ffcpl:/com/mindalliance/channels/test/crud/refTable.xml");
requests[1] = req;

// Request 2 - UPDATE
req = context.createSubRequest("active:crud_document");
req.setRequestType(eval("INKFRequestReadOnly.RQT_SINK"));
req.addArgument("database", "data:text/plain,test");
doc = "<project>" +
    			"<id>project1</id>" +
    			"<name>My project</name>" +
    			"<description>An updated project description</description>" +
    			"<managedByPersonId>person1</managedByPersonId>" +
  			 "</project>";
req.addSystemArgument(new StringAspect(doc));
requests[2] = req;

// Request 3 - GET
req = context.createSubRequest("active:crud_document");
req.setRequestType(eval("INKFRequestReadOnly.RQT_SOURCE"));
req.addArgument("database", "data:text/plain,test");
req.addArgument("id", "data:text/plain,event1");
requests[3] = req;

// Request 4 - QUERY
req = context.createSubRequest("active:crud_database");
req.addArgument("name", "data:text/plain,test");
req.addArgument("operator", "data:text/plain,query");
req.addArgument("xquery", "ffcpl:/com/mindalliance/channels/test/crud/testQuery.xq");
requests[4] = req;


var requestHandles = [];
for (i in requests) {
	requestHandles[i] = context.issueAsyncSubRequest(requests[i]);
}

//Wait for all requests to complete
// try {
	for each (handle in requestHandles) {
		handle.join(); 
	}
/*

}
catch (e)	{
	log("All requests did not complete: " + e, "severe");
	success = false;
}
*/

// Respond
var resp = context.createResponseFrom(new BooleanAspect(success));
context.setResponse(resp);