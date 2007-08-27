importPackage(Packages.com.ten60.netkernel.urii.aspect);
importPackage(Packages.java.lang);

utilsURI = "ffcpl:/test/utils.js";
context.importLibrary(utilsURI);

var rngSchemaURL = "http://relaxng.org/relaxng.rng";
var schemas = ["acquirement","agent","artifact","category","event","location",
               "organization","person","project","repository","role",
               "scenario","task","user"];

for (i in schemas) {
	var uri = "schema:" + schemas[i] + ".rng";
	issueValidateRNGRequest(rngSchemaURL, uri);
}

var resp=context.createResponseFrom(new BooleanAspect(true));
context.setResponse(resp);

