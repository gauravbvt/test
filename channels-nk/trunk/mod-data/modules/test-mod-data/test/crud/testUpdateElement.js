importPackage(Packages.com.ten60.netkernel.urii.aspect);
utilsURI = "ffcpl:/com/mindalliance/channels/library/scripts/utils.js";
context.importLibrary(utilsURI);

// Create, update and get an element
var xml = "<project><name>My project</name></project>";
var res = request("active:channels_element", {doc:new StringAspect(xml)}, "new" );
var el = new  XML(context.transrept(res, StringAspect).getString());
var id = el.id.text();
el.name = "Updated";
request("active:channels_element", {SYSTEM:new XmlObjectAspect(el.getXmlObject())}, "sink" );
res = request("active:channels_element", {id:"id:"+id}, "source");
respond(res, "text/xml");