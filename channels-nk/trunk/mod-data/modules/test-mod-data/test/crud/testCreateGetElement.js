importPackage(Packages.com.ten60.netkernel.urii.aspect);
utilsURI = "ffcpl:/com/mindalliance/channels/library/scripts/utils.js";
context.importLibrary(utilsURI);

var doc = "<project><name>My project</name></project>";
var res = request("active:channels_element", {doc:new StringAspect(doc)}, "new" );
var el = new  XML(context.transrept(res, StringAspect).getString());
var id = el.id.text();
res = request("active:channels_element", {id:"id:" + id}, "source" );
respond(res, "text/xml");