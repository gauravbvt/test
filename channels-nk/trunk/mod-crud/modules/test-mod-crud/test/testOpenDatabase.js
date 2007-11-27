importPackage(Packages.com.ten60.netkernel.urii.aspect);
utilsURI = "ffcpl:/com/mindalliance/channels/support/scripts/utils.js";
context.importLibrary(utilsURI);

request("active:crud_database", {operator:asData("delete"), name:asData("test")}).getAspect(BooleanAspect);
var resOpen = request("active:crud_database", {operator:asData("open"), name:asData("test")}).getAspect(BooleanAspect);
respond(resOpen, "text/xml");
