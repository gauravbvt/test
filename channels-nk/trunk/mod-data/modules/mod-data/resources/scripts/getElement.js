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
function addNamesToListedReferences(elem) {
	log("Adding names to listed IDs in " + elem, "info");
	var names = findListedReferenceElementNames(elem);
	for (i in names) {
			var list = elem.getXmlObject().selectPath(".//" + names[i]);
			for (j in list) {
				var el = new XML(list[j]);
				el.@name = getNameFromId(el.text());
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

function getNameFromId(id) {
	log("Getting name from id :" + id, "info");
	return getDocument(id).name.text();
}


var id = context.getThisRequest().getArgument("id").substring(3);
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
var xoa=new XmlObjectAspect(doc.getXmlObject());

req=context.createSubRequest("active:attachGoldenThread");
req.addArgument("operand", xoa);
req.addArgument("param", "gt:element:"+ id);
res=context.issueSubRequest(req);

var resp=context.createResponseFrom(res);
resp.setMimeType("text/xml");
resp.setCacheable();
context.setResponse(resp);
