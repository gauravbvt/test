importPackage(Packages.org.ten60.netkernel.xml.representation);
importPackage(Packages.com.ten60.netkernel.urii.aspect);
importPackage(Packages.java.lang);


utilsURI = "ffcpl:/test/utils.js";
context.importLibrary(utilsURI);

var samples = ["project","scenario"];

for (i in samples) {
	// Get schema URI of xml sample
	var docURI = "ffcpl:/test/schemas/samples/" + samples[i] + ".xml";
	var doc = new XML(context.sourceAspect(docURI,IAspectXmlObject).getXmlObject());
//	log(doc, "info");
	var schemaURL = doc.@schema;
//	log("Schema URL = " + schemaURL, "info");
	var schemaName = new String(schemaURL).match(/\/(\w+\.rng)/)[1];
//	log("Schema name = " + schemaName, "info");
	issueValidateRNGRequest("schema:" + schemaName, docURI);
}

var resp=context.createResponseFrom(new BooleanAspect(true));
context.setResponse(resp);

