// Gets an element as XML document from the container

// arguments:
//    id -- the id of the element as a url id:<someguid>, e.g. id:98394839847634
// returns:
//    an XML document

utilsURI = "ffcpl:/libs/utils.js";
context.importLibrary(utilsURI);

importPackage(Packages.org.ten60.netkernel.xml.representation);
importPackage(Packages.com.ten60.netkernel.urii.aspect);
importPackage(Packages.org.apache.xmlbeans);
importPackage(Packages.java.lang);


// Adds names of ids in listed references
function addNamesToReferences(elem) {
	log("Adding names to IDs in " + elem, "info");
	var names = findReferencedElementNames(elem);
	for (i in names) {
			var list = elem.getXmlObject().selectPath(".//" + names[i]);
			for (j in list) {
				var el = new XML(list[j]);
				el.@name = getNameFromId(el.text());
				log("Added name to: " + el, "info");
			}
		}
}

function findReferencedElementNames(elem) {
	var list = new Array();
	for each (el in elem..*) {
		var elName = el.name();
		log("Checking name " + elName, "info");
		if ((elName != null) && new String(elName).match(/Id$/)) { // better enforce this naming pattern throughout...
			log("Found listed ID at " + elName, "info");
			list.push(elName);
		}
	}
	return list;
}

function getNameFromId(id) {
	log("Getting name from id :" + id, "info");
	return getElement(id).name.text();
}

log(">> START GET", "info");

// Access parameter
var id = context.getThisRequest().getArgument("id").substring(3);
var doc;
log("Getting element " + id  + " from container " + getContainerName(), "info");
// Get element
try {
	beginRead("GET");
	doc = getElement(id);
	if (context.getThisRequest().argumentExists("nameReferenced")) {
		addNamesToReferences(doc);
	}
}
finally {
	endRead("GET");
}
log("<< END GET", "info");

//Return Response
var resp=context.createResponseFrom(new XmlObjectAspect(doc.getXmlObject()));
resp.setMimeType("text/xml");
resp.setCacheable();
context.setResponse(resp);
