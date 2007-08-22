// Gets an element as XML document from the container

// arguments:
//    id -- the id of the element as canonical document <id>someguid</id>
// returns:
//    an XML document

utilsURI = "ffcpl:/libs/utils.js";
context.importLibrary(utilsURI);

importPackage(Packages.org.ten60.netkernel.xml.representation);
importPackage(Packages.com.ten60.netkernel.urii.aspect);
importPackage(Packages.org.apache.xmlbeans);
importPackage(Packages.java.lang);

function getDocument(id) {
	var op =  getDocumentDescriptor(id);	
	log("Document descriptor: " + op, "info");
	var req = context.createSubRequest("active:dbxmlGetDocument");
	req.addArgument("operator", new XmlObjectAspect(op.getXmlObject()) );
	var doc = context.transrept(context.issueSubRequest(req), IAspectXmlObject);
  log("Got document " + context.transrept(doc,IAspectString).getString(), "info");
	return new XML(doc.getXmlObject());
}

// Adds names of ids in listed references
function addNamesToListedReferences(elem) {
	log("Adding names to listed IDs in " + elem, "info");
	var names = findListedReferenceElementNames(elem);
	for (i in names) {
			var list = elem.getXmlObject().selectPath(".//" + names[i]);
			for (j in list) {
				var el = new XML(list[j]);
				var name = getNameFromId(el.text());
				el.@name = name
				log("Added name to: " + el, "info");
			}
		}
}

function findListedReferenceElementNames(elem) {
	var list = new Array();
	var uri = elem.@schema;
	var schema = new XML(context.sourceAspect(uri, IAspectXmlObject).getXmlObject());
	default xml namespace = 'http://relaxng.org/ns/structure/1.0'; // limit the scope of default ns to this functon
	for each (elDef in schema..zeroOrMore.element) {
		var elName = elDef.@name;
		if (elName.match(/Id$/)) { // better enforce this naming pattern throughout...
			log("Found listed ID at " + elName, "info");
			list.push(elName);
		}
	}
	return list;
}

// Returns a name or " NO NAME_" if none is found
function getNameFromId(id) {
	log("Getting name from id :" + id, "info");
	var name = " NO NAME"
	try {
		name = getDocument(id).name.text();
	}
	catch (e) {
		log("Can't get name from id: " + id + " (" + e + ")", "warning");
	}
	return name;
}


var id = new XML(context.sourceAspect("this:param:" + "id", IAspectXmlObject).getXmlObject()).text();
log("Getting element " + id  + " from container " + dbxml_getContainerName(), "info");
var doc = getDocument(id);

var namesListed = false;
if (context.getThisRequest().argumentExists("namesListed")) {
	namesListed = context.sourceAspect("this:param:" + "namesListed", IAspectBoolean).isTrue();
}

if (namesListed) {
	addNamesToListedReferences(doc);
}

//Return Response
var resp=context.createResponseFrom(new XmlObjectAspect(doc.getXmlObject()));
resp.setMimeType("text/xml");
context.setResponse(resp);


