importPackage(Packages.com.ten60.netkernel.urii.aspect);
utilsURI = "ffcpl:/com/mindalliance/channels/support/scripts/utils.js";
context.importLibrary(utilsURI);

var doc = "<project><name>My project</name></project>";
var res = request("active:crud_document", {doc:new StringAspect(doc), database:asData("test")}, "new" );
var el = new  XML(context.transrept(res, StringAspect).getString());
var id = el.id.text();
res = request("active:crud_document", {id:asData(id), database:asData("test")}, "source" );
respond(res, "text/xml");