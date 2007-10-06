importPackage(Packages.com.ten60.netkernel.urii.aspect);
utilsURI = "ffcpl:/com/mindalliance/channels/library/scripts/utils.js";
context.importLibrary(utilsURI);


reloadModel();

var config = "<config><connectTimeout>2000</connectTimeout><timeout>600000</timeout></config>";
var doc = "<project><name>Created project</name></project>";
var res = request("active:httpPost", {url:"http://localhost:8080/channels/project",
																		  config: new StringAspect(config),
																	    arg: new StringAspect(doc)} );
respond(res, "text/xml");