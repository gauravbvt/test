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

function addAllIfUnique(set, elements) {
	var added = new Array();
	for (i in elements) {
		var element = elements[i];
		if (addIfUnique(set, element)) added.push(element);
	}
	return added;
}

function getImpliedCategoryIds(categoryId) {
	// log("Getting implied categories by " + categoryId, "info");
	var category = getDocument(categoryId);
	var implied = new Array();
	for each (id in category.implies.categoryId) {
		implied.push(id.text());
	}
	// log("Categories implied by " + categoryId + " = " + implied);
	return implied;
}

function getCategoriesOf(elementId) {
	// log("Getting all categories of " + elementId, "info");
	var explicitCategories = new Array();
	var doc = getDocument(elementId);
	var set = new Array(); // in case there are duplicates
	for each (id in doc.categories.categoryId) {
		addIfUnique(set, id.text());
	}
	// log("Explicit category ids: " + set, "info");
	var added = [].concat(set);
	while (added.length > 0) { // while more is added to the transitive closure of categories
		var more = new Array();
		for (i in added) {
			var implied = getImpliedCategoryIds(added[i]);
			more = more.concat(addAllIfUnique(set, implied)); // Accumulate the new category ids added to idSet
			}
			
		added = more;
		}
	return set;
}

log(">> START TEMPLATE", "info");

var template = <information/>;
var elementId = context.getThisRequest().getArgument("id").substring(3);
log("Getting information template for " + elementId, "info");
try {
	beginRead("TEMPLATE");
	// 1- collect all distinct category IDs, explicit and implied
	var idSet = getCategoriesOf(elementId);
	// 2- Collect the topics (names only) and their EOIs (names and descriptions) for each information in each category
	// 3- Aggregate the EOIs across categories into each named topic 
	// 			- no duplicate topics by name, no duplicate EOIs by name per topic
	// 			- When collapsing EOIs, accumulate privacy and minimize confidence (TODO)
	// 4- Construct and return an aggregated information element as xml.
	// log("Building information template from " + idSet, "info");
	var templateTopic;
	var templateEoi;
	for (i in idSet) {
		var info = getDocument(idSet[i]).information;
		// for all topics
		for each (topic in info.topic) {
			var topicName = topic.name.text();
			var topicDescription = topic.description != null ? topic.description.text() : "";
			var list = template.topic.(name == topicName); // is topic already in template?
			// log(list.length() + " template topics named " + topicName + " = " + list, "info");
			if (list.length() == 0) {
				templateTopic =  <topic>
													 <name>{topicName}</name>
													 <description>{topicDescription}</description>
												 </topic>;
				template.insertChildAfter(null, templateTopic);
				templateTopic = template.topic.(name == topicName)[0];
				// log("Added topic to template: " + templateTopic, "info");
			}
			else {
				templateTopic = list[0];
			}
			for each (eoi in topic.eoi){
				var eoiName = eoi.name.text();
				var eoiDescription = eoi.description != null ? eoi.description.text() : "";
				list = templateTopic.eoi.(name == eoiName);
				// log(list.length() + " template eois in topic " + topicName + " named " + eoiName + " = " + list, "info");
				if (list.length() == 0) {
					templateEoi = <eoi>
													<name>{eoiName}</name>
													<description>{eoiDescription}</description>
												 </eoi>;
					templateTopic.insertChildAfter(null,templateEoi);
					// log("Added eoi: " + templateEoi + " to topic " + topicName + " in template", "info");
				}
				else {
					log("EOI collision with " + list[0] + " in topic " + topicName + " from category " + idSet[i], "warning");
				}
			}
		}
	}
}
catch(e) {
	log("getInformationTemplate failed: " + e, "severe");
	throw e;
}
finally {
	endRead("TEMPLATE");
}
log("Information template for " + elementId + " =\n" + template, "info");
log("<< END TEMPLATE", "info");

// Response
var resp = context.createResponseFrom(new XmlObjectAspect(template.getXmlObject()));
resp.setMimeType("text/xml");
context.setResponse(resp);
