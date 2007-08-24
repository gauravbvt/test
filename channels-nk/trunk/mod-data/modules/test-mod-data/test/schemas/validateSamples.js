importPackage(Packages.org.ten60.netkernel.xml.representation);
importPackage(Packages.com.ten60.netkernel.urii.aspect);
importPackage(Packages.java.lang);


utilsURI = "ffcpl:/test/utils.js";
context.importLibrary(utilsURI);

var samples = ["acquirement","artifact","category","event", "location",
               "organization","person","project","repository","role","scenario","task","user"];

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

