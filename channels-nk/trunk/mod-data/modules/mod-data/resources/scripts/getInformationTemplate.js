// Gets an information document with a transitive closure of all the topic-grouped EOIs
// about an element given its categories and the categories each one implies.

// arguments:
//    id -- the id of the element as canonical document <id>someguid</id>
// returns:
//    an XML document validated by def_information.rng

utilsURI = "ffcpl:/libs/utils.js";
context.importLibrary(utilsURI);

importPackage(Packages.org.ten60.netkernel.xml.representation);
importPackage(Packages.com.ten60.netkernel.urii.aspect);
importPackage(Packages.org.apache.xmlbeans);
importPackage(Packages.java.lang);

/*
 * 1- Assemble all category ids applied explicitly and implicitly
 * 2- Collect the topics and their EOIs for each information in each category
 * 3- Aggregate the EOIs across categories into each named topic 
 * 			- no duplicate topics by name, no duplicate EOIs by name per topic
 * 			- When collapsing EOIs, accumulate privacy and minimize confidence
 * 4- Construct and return an aggregated information element as xml.
*/

function contains(array, element) {
	for (i in array) {
		if (array[i] == element) return true;
	}
	return false;
}

function addIfUnique(array, element) {
	if (contains(array, element)) {
		return false;
	}
	else {
		array.push(element);
		return true;
	}
}

function addAllIfUnique(array, elements) {
	var added = new Array();
	for (i in elements) {
		var element = elements[i];
		if (addIfUnique(array, element)) added.push(element);
	}
	return added;
}

function addImpliedCategoryIds(set, categoryId) {
	var category = getDocument(categoryId);
	log("Getting implied categories by " + category, "info");
	var impliedIds = category.implies.categoryId;
	for (i in impliedIds) {
		var id = impliedIds[i].text();
		if (addlIfUnique(set, id)) {
			log("Transitive closure on " + id, "info");
			addImpliedCategoryIds(set, id);
		}
	}
}

function getCategoriesOf(elementId) {
	log("Getting ALL categories of " + id, "info");
	var idSet = new Array();
	var doc = getDocument(elementId);
	try {
		var ids = doc.categories.categoryId;
		log("Explicit category ids: " + ids, "info");
	}
	catch(e) {
		log("Element " + doc + " is not categorized: " + e, "warning");
	}
	for (i in ids) {
		var id = ids[i].text();
		if (addIfUnique(idSet,id)) { // should be unique but perhaps not
			addImpliedCategoryIds(idSet, id);
		}
	}
	return idSet;
}

// MAIN
var id = context.getThisRequest().getArgument("id").substring(3);
log("Getting information template for " + id, "info");
var doc = getDocument(id);
// 1- collect all distinct category IDs, explicit and implied
var idSet = getCategoriesOf(id);

// 2- Collect the topics (names only) and their EOIs (names and descriptions) for each information in each category
// 3- Aggregate the EOIs across categories into each named topic 
// 			- no duplicate topics by name, no duplicate EOIs by name per topic
// 			- When collapsing EOIs, accumulate privacy and minimize confidence (TODO)
// 4- Construct and return an aggregated information element as xml.

var infoXml = <info/>;
for (i in idSet) {
	var info = getDocument(idSet[i]).information;
	// for all topics
	var topics = info.topic;
	for (j in topics) {
		var topic = topics[j];
		var topicName = topic.name.text();
		var topicDescription = topic.description != null ? topic.description.text() : "";
		var topicXml = infoXml.topic.(name == topicName); 
		if (topicXML == null) {
			topicXml = <topic>
									 <name>{topicName}</name>
									 <description>{topicDescription}</description>
								 </topic>;
			infoXml.insertChildAfter(null, topicXml);
			log("Added topic: " + topicXml, "info");
		}
		var eois = topic.eoi;
		for (k in eois){
			var eoi = eois[k];
			var eoiName = eoi.name.text();
			var eoiDescription = eoi.description != null ? eoi.description.text() : "";
			var eioXml = topicXml.eoi.(name == eoiName);
			if (eoiXml == null) {
				eoiXml = <eoi>
									<name>{eoiName}</name>
									<description>{eoiDescription}</description>
								 </eoi>;
				topicXml.insertChildAfter(null,eoiXml);
				log("Added eoi: " + eoiXml, "info");
			}
		}
	}
}

var es = context.contructResponseFrom(new XmlObjectAspect(infoXml.getXmlObject()));
resp.setMimeType("text/xml");
context.setResponse(resp);
