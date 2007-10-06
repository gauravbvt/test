utilsURI = "ffcpl:/com/mindalliance/channels/library/scripts/utils.js";
context.importLibrary(utilsURI);

importPackage(Packages.org.ten60.netkernel.layer1.representation);

reloadModel();

var config = "<config><connectTimeout>2000</connectTimeout><timeout>600000</timeout></config>";
var nvp = new NVPImpl();
nvp.addNVP("query", "eventsInScenario");
nvp.addNVP("scenarioId", "scenario1");
var res = request("active:httpPost", {url:"http://localhost:8080/channels/model",
																		 arg: new NVPAspect(nvp),
																		 config: new StringAspect(config)});
respond(res, "text/xml");