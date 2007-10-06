importPackage(Packages.com.ten60.netkernel.urii.aspect);
utilsURI = "ffcpl:/com/mindalliance/channels/library/scripts/utils.js";
context.importLibrary(utilsURI);

reloadModel();
var res = request("active:channels_element", {id:"id:event1",nameReferenced:"whatever"}, "source");
respond(res,"text/xml");
