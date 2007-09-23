utilsURI = "ffcpl:/test/utils.js";
context.importLibrary(utilsURI);

importPackage(Packages.org.ten60.netkernel.xml.representation);
importPackage(Packages.com.ten60.netkernel.urii.aspect);
importPackage(Packages.org.apache.xmlbeans);
importPackage(Packages.org.ten60.netkernel.layer1.nkf);
importPackage(Packages.org.ten60.netkernel.layer1.nkf.impl);
importPackage(Packages.java.lang);

reloadModel();

var success = true;
var requests = [];

// Request 0
var req = context.createSubRequest("active:channels_data_createElement");
var doc = <root>
        		<project>
          	<name>My project</name>
        		</project>
      		</root>;
req.addArgument("doc", new XmlObjectAspect(doc.getXmlObject()));
requests[0] = req;

// Request 1
req = context.createSubRequest("active:channels_data_deleteElement");
doc = <arg><id>person1</id></arg>;
req.addArgument("id", new XmlObjectAspect(doc.getXmlObject()));
requests[1] = req;

// Request 2
req = context.createSubRequest("active:channels_data_updateElement");
doc = <root>
				 <project schema="http://localhost:8080/channels/schema/project.rng">
    			<id>project1</id>
    			<name>My project</name>
    			<description>An updated project description</description>
    			<managedByPersonId>person1</managedByPersonId>
  			 </project>;
  		</root>;
req.addArgument("doc", new XmlObjectAspect(doc.getXmlObject()));
requests[2] = req;

// Request 3
req = context.createSubRequest("active:channels_data_getElement");
req.addArgument("id", "id:event1");
requests[3] = req;

// Request 4
req = context.createSubRequest("active:channels_data_queryModel");
req.addArgument("xquery", "ffcpl:/com/mindalliance/channels/data/queries/allOrganizations.xq");
requests[4] = req;


var requestHandles = [];
for (i in requests) {
	requestHandles[i] = context.issueAsyncSubRequest(requests[i]);
}

//Wait for all requests to complete
try {
	for each (handle in requestHandles) {
		handle.join(); 
	}
}
catch (e)	{
	log("All requests did not complete: " + e, "severe");
	success = false;
}


// Respond
var resp = context.createResponseFrom(new BooleanAspect(success));
context.setResponse(resp);