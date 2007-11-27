importPackage(Packages.com.ten60.netkernel.urii.aspect);
utilsURI = "ffcpl:/com/mindalliance/channels/support/scripts/utils.js";
context.importLibrary(utilsURI);

reloadDatabase("test");
var xml = request("active:crud_database", {operator:asData("query"), name:asData("test"), xquery:"ffcpl:/com/mindalliance/channels/test/crud/testQuery.xq"});
respond(xml, "text/xml");