importPackage(Packages.com.ten60.netkernel.urii.aspect);
utilsURI = "ffcpl:/com/mindalliance/channels/library/scripts/utils.js";
context.importLibrary(utilsURI);

reloadModel();
var template = request("active:channels_information", {ids:"ids:event1"});
// Respond
respond(template, "text/xml");