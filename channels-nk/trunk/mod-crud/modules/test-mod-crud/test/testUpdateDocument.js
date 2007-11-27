importPackage(Packages.com.ten60.netkernel.urii.aspect);
utilsURI = "ffcpl:/com/mindalliance/channels/support/scripts/utils.js";
context.importLibrary(utilsURI);

// Create, update and get an element
var xml = "<project><name>My project</name></project>";
var res = request("active:crud_document", {doc:new StringAspect(xml), database:asData("test")}, "new" );
var el = new  XML(context.transrept(res, StringAspect).getString());
var id = el.id.text();
el.name = "Updated";
request("active:crud_document", {SYSTEM:new XmlObjectAspect(el.getXmlObject()), database:asData("test")}, "sink" );
res = request("active:crud_document", {id:asData(id), database:asData("test")}, "source");
respond(res, "text/xml");