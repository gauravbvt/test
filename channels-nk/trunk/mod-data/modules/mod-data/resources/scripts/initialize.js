context.importLibrary("ffcpl:/libs/channels_data.js");

// As part of the module initialization we ensure there 
// is no existing concurrency state maintained from
// server run to server run.

initializeMutex("ffcpl:/mutex/read");

var req=context.createSubRequest("active:channels_data_openModel");
context.issueSubRequest(req);

var resp = context.createResponseFrom(new BooleanAspect(true));
context.setResponse(resp);
