utilsURI = "ffcpl:/libs/utils.js";
context.importLibrary(utilsURI);

// As part of the module initialization we ensure there 
// is no existing concurrency state maintained from
// server run to server run.

initializeMutex("ffcpl:/mutex/read");

var resp = context.createResponseFrom(new BooleanAspect(true));
context.setResponse(resp);