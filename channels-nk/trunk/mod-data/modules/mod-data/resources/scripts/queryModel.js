// Queries the container

// arguments:
//    xquery -- an XQuery resource
//    id -- an element's id (optional) // TBD - query a document, not the container
//    variables -- properties resource (optional) - <properties><property><key>xyz</key><value>123</value></property>...</properties>
// returns:
//    an XML document with the results (the xquery must produce a well-formed XML document)

utilsURI = "ffcpl:/libs/utils.js";
context.importLibrary(utilsURI);

importPackage(Packages.org.ten60.netkernel.xml.representation);
importPackage(Packages.com.ten60.netkernel.urii.aspect);
importPackage(Packages.java.lang);

// Add variable declaration to xquery from properties document
function declareVariables(query, properties) {
  var prologuedQuery = query;
  for each (prop in properties.property) {
    var decl = "declare variable $" +  prop.key.text() + " := '" + prop.value.text() + "';\n";
    prologuedQuery = decl + prologuedQuery;
  }
  return prologuedQuery;
}
log(">> START QUERY", "info");

// Get arguments
var query = context.sourceAspect("this:param:xquery", IAspectString).getString();
log("Query: " + query, "info");
var properties = null;
if (context.getThisRequest().argumentExists("variables")) {
  properties = new XML(context.sourceAspect("this:param:variables", IAspectXmlObject).getXmlObject());
  log("with variables " + properties, "info");
}
// Prepare the query
if (properties != null) query = declareVariables(query, properties); // Add variable declaration as prologue
query = filter(query); // Substitute place holders
var result;
try {
	beginRead("QUERY");
	result = runQuery(query);
}
finally {
	endRead("QUERY");
}
log("<< END QUERY", "info");

//Return Response
var resp=context.createResponseFrom(result);
resp.setMimeType("text/xml");
context.setResponse(resp);
