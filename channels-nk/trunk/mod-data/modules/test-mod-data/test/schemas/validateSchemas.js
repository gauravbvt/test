importPackage(Packages.com.ten60.netkernel.urii.aspect);
utilsURI = "ffcpl:/com/mindalliance/channels/library/scripts/utils.js";
context.importLibrary(utilsURI);

var rngSchemaURL = "http://relaxng.org/relaxng.rng";
var schemas = ["acquirement","agent","artifact","category","event","interdiction","know","location",
               "needToKnow","obligation","organization","person","phase","policy","project","repository","role",
               "scenario","sharingNeed","task","user"];

var success = true;
var uri;
for (i in schemas) {
	uri = "schema:" + schemas[i] + ".rng";
	var schema = context.sourceAspect(uri,IAspectString).getString();
	log(schema, "info");
	try {
		issueValidateRNGRequest(rngSchemaURL, uri);
	}
	catch (e) {
		log("Schema invalid: " + uri + "(" + e  + ")", "severe");
		success = false;
	}
}

var resp=context.createResponseFrom(new BooleanAspect(success));
context.setResponse(resp);

