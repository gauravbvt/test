importPackage(Packages.com.ten60.netkernel.urii.aspect);
importPackage(Packages.java.lang);

doc = context.sourceAspect("this:param:param", IAspectXmlObject).getXmlObject();
log("param = " + doc, "info");
resp = context.createResponseFrom(new BooleanAspect(true));
resp.setExpired();
context.setResponse(resp);

