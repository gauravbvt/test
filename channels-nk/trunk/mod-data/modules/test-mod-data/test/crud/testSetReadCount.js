utilsURI = "ffcpl:/test/utils.js";
context.importLibrary(utilsURI);

importPackage(Packages.org.ten60.netkernel.xml.representation);
importPackage(Packages.com.ten60.netkernel.urii.aspect);
importPackage(Packages.org.apache.xmlbeans);
importPackage(Packages.java.lang);

context.sinkAspect("transient:readCount", new StringAspect("0"));
var count = parseInt(context.sourceAspect("transient:readCount", IAspectString).getString());

// Respond
var resp = context.createResponseFrom(new BooleanAspect(count == 0));
context.setResponse(resp);