importPackage(Packages.com.ten60.netkernel.urii.aspect);
utilsURI = "ffcpl:/com/mindalliance/channels/support/scripts/utils.js";
context.importLibrary(utilsURI);

reloadDatabase("test");

var tests = [ // ["id to delete", number deleted, number updated]
							["user1", 1, 1],
							["event1", 5, 1],
							["role1", 2, 3]
						];

for each (test in tests) {
	var id = test[0];
	var expectedDeletes = test[1];
	var expectedUpdates = test[2];
	// Send delete request
	var res = request("active:crud_document", {id:asData(id), database:asData("test"), refTable:"ffcpl:/com/mindalliance/channels/test/crud/refTable.xml"}, "delete"); 
	var list = new XML(context.transrept(res, StringAspect).getString());
	// Verify that expected number of deleted elements met
	var count = list.deleted.id.length();
	if (count != expectedDeletes) {
		log("ERROR deleting " + id + ": expected " + expectedDeletes + " deletions but got " + count, "severe");
		throw("Delete failed");
	}
	var count = list.updated.id.length();
	if (count != expectedUpdates) {
		log("ERROR deleting " + id + ": expected " + expectedUpdates + " updates but got " + count, "severe");
		throw("Delete failed");
	}
}

respond(new BooleanAspect(true),"text/xml");
