utilsURI = "ffcpl:/test/utils.js";
context.importLibrary(utilsURI);

importPackage(Packages.org.ten60.netkernel.xml.representation);
importPackage(Packages.com.ten60.netkernel.urii.aspect);
importPackage(Packages.java.lang);

reloadModel();

var tests = [
							["user1", 1],
							["event1", 2]
						];

for each (test in tests) {
	var id = test[0];
	var expectedCount = test[1];
	// Send delete request
	var req = context.createSubRequest("active:channels_data_deleteElement");
	var idArg = <arg><id>{id}</id></arg>;
	req.addArgument("id", new XmlObjectAspect(idArg.getXmlObject()));   
	var res = context.issueSubRequest(req);    
	var list = new XML(context.transrept(res, IAspectString).getString());
	// log(list..id.length() + " DELETED = " + list, "info");
	// Verify that expected number of deleted elements met
	var count = list..id.length();
	if (count != expectedCount) {
		log("ERROR deleting " + id + ": expected " + expectedCount + " deletions but got " + count, "severe");
		throw("Delete failed");
	}
	// Verify that all references were deleted
	/*
	var query = "<list>\n" +
								"{\n" +
									"for $e in collection('__MODEL__')/*\n" +
									"where $e//* = '" + id + "'\n" +
									"return\n" +
									"  <id>{$e/id/text()}</id>\n" +
								"}\n" +
	            "</list>";
	req = context.createSubRequest("active:channels_data_queryModel");
	req.addArgument("xquery", new StringAspect(query));
	res = context.issueSubRequest(req);
	list = new XML(context.transrept(res, IAspectString));
	count = list..id.length();
	if (count != 0) {
		log("ERROR deleting " + id + ": not all references deleted: " + list, "severe");
		throw("Delete failed");
	}
	*/
}

// Set response
var resp = context.createResponseFrom(new BooleanAspect(true));
resp.setMimeType("text/xml");
context.setResponse(resp);
