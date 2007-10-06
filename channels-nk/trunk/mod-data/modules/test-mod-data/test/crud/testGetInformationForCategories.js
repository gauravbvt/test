importPackage(Packages.com.ten60.netkernel.urii.aspect);
utilsURI = "ffcpl:/com/mindalliance/channels/library/scripts/utils.js";
context.importLibrary(utilsURI);

reloadModel("db:testTemplateDB");
var template = request("active:channels_information", {ids:"ids:cat-car-accident,cat-traffic"});
respond(template, "text/xml");