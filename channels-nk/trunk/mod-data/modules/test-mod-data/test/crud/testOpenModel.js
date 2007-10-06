importPackage(Packages.com.ten60.netkernel.urii.aspect);
utilsURI = "ffcpl:/com/mindalliance/channels/library/scripts/utils.js";
context.importLibrary(utilsURI);

var resDelete = request("active:channels_model", {operator:asData("delete")}).getAspect(BooleanAspect);
// var resOpen = request("active:channels_model", {operator:asData("open"), init:"db:testDB"}).getAspect(BooleanAspect);
var resOpen = request("active:channels_model", {operator:asData("open")}).getAspect(BooleanAspect);
respond(new BooleanAspect(resDelete.isTrue() && resOpen.isTrue()), "text/xml");
