importPackage(Packages.com.ten60.netkernel.urii.aspect);
utilsURI = "ffcpl:/com/mindalliance/channels/support/scripts/utils.js";
context.importLibrary(utilsURI);

reloadDatabase("test");
var res = request("active:crud_document", {id:asData("event1"), nameReferenced:"true", database:asData("test")}, "source");
respond(res,"text/xml");
