context.importLibrary("ffcpl:/libs/utils.js"); // TODO rename to filtered.js
context.importLibrary("ffcpl:/libs/support.js");
context.importLibrary("ffcpl:/libs/mutex.js");
context.importLibrary("ffcpl:/libs/database.js");

dbPluginURI = "ffcpl:/libs/dbxml.js";
context.importLibrary(dbPluginURI);

// Initialization
initializeReadCountMutex();



