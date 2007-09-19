context.importLibrary("ffcpl:/libs/channels_data.js");


var req=context.createSubRequest("active:channels_data_openModel");
context.issueSubRequest(req);

var resp = context.createResponseFrom(new BooleanAspect(true));
context.setResponse(resp);
