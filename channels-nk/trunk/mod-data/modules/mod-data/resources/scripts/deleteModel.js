// Delete container return whether the container was deleted (a boolean)
// Looks up ffcpl:/crud/dbxml_config.xml for container description

context.importLibrary("ffcpl:/libs/channels_data.js");

importPackage(Packages.org.ten60.netkernel.xml.representation);
importPackage(Packages.com.ten60.netkernel.urii.aspect);
importPackage(Packages.java.lang);

var exists = databaseExists();
var maxRetries = 20;
while (databaseExists() && (maxRetries > 0)) {
	maxRetries--;
	try {
		var exists = databaseExists();
		if (exists) {
			deleteDatabase();
  		log("Deleted database: " + getDatabaseName(), "info");
		}
		else {
		  log("Database not deleted (does not exist): " + getDatabaseName(), "warning");
		}
	}
	catch(e) { // Deleting fails temporarily and spurriously
		log("Delete model failed: " + e, "warning");
		sleep(100);
	}
}

//Return Response
var resp=context.createResponseFrom(new BooleanAspect(exists));
resp.setExpired(); // don't cache
resp.setMimeType("text/xml");
context.setResponse(resp);
