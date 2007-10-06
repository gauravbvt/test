importPackage(Packages.com.ten60.netkernel.urii.aspect);
utilsURI = "ffcpl:/com/mindalliance/channels/library/scripts/utils.js";
context.importLibrary(utilsURI);

request("active:channels_model", {operator:asData("open")});
var xml = request("active:channels_model", {operator:asData("get")});
respond(xml, "text/xml");