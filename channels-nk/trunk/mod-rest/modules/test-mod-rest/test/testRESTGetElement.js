importPackage(Packages.com.ten60.netkernel.urii.aspect);

utilsURI = "ffcpl:/com/mindalliance/channels/library/scripts/utils.js";
context.importLibrary(utilsURI);

reloadModel();

var config = "<config><connectTimeout>2000</connectTimeout><timeout>600000</timeout></config>";
var res = request("active:httpGet", {url:"http://localhost:8080/channels/element?id=event1",
																		 config: new StringAspect(config)});
respond(res, "text/xml");