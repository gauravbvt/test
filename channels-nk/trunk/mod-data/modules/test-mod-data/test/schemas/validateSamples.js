importPackage(Packages.com.ten60.netkernel.urii.aspect);
utilsURI = "ffcpl:/com/mindalliance/channels/library/scripts/utils.js";
context.importLibrary(utilsURI);

var samples = ["acquirement","agent", "artifact","category","event","interdiction","know","location","needToKnow",
               "obligation","organization","person","phase", "policy","project","repository","role",
               "scenario","sharingNeed","task","user"];

var allValid = true;
for (i in samples) {
	// Get schema URI of xml sample
	var docURI = "ffcpl:/test/schemas/samples/" + samples[i] + ".xml";
	var doc = new XML(context.sourceAspect(docURI,IAspectXmlObject).getXmlObject());
	log(doc, "info");
	var schemaName = new String(doc.@schema).match(/\/(\w+\.rng)/)[1];
	var schemaURI = "schema:" + schemaName;
	allValid = allValid && issueValidateRNGRequest(schemaURI, docURI);
}

var resp=context.createResponseFrom(new BooleanAspect(allValid));
context.setResponse(resp);

