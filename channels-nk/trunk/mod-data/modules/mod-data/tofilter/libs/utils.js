dbFunctionsURI = "ffcpl:/libs/dbxml.js";

/*
This library must import an implementation for these xml database functions:

function containerExists();
function createContainer();
function deleteContainer();
function getContainerName();
function getDocument(id);
function putDocument(doc);
function deleteDocument(id);
function queryContainer(query);
*/
context.importLibrary(dbFunctionsURI);


// Utilities

importPackage(Packages.org.ten60.netkernel.xml.representation);
importPackage(Packages.com.ten60.netkernel.urii.aspect);
importPackage(Packages.java.lang);

// Constants

SCHEMA_URL = "@protocol@://@host@:@port@/channels/schema/"; 
LOG_URL = "ffcpl:/etc/LogConfig.xml";
LOG_MUTEX = true;


// UTILITIES

function contains(array, object) {
	for each (el in array) {
		if (el == object) return true;
	}
	return false;
}

function log(content, level) {
  var req=context.createSubRequest("active:application-log");
  req.addArgument("operand", new StringAspect(content) );
  req.addArgument("configuration", LOG_URL ); // Defaults to ffcpl:/etc/LogConfig.xml
  var levelXml = "<log>" + "<" + level + "/>" + "</log>";
  req.addArgument("operator", new StringAspect(levelXml) );
//  context.issueAsyncSubRequest(req);
  context.issueSubRequest(req);
}

function expire(uri) {
	var req = context.createSubRequest("active:expire");
	req.addArgument("operand", uri);
	context.issueSubRequest(req);
}



// SHOULD PUT SUBSTITUTIONS IN A SEPARATE CONFIGURATION FILE
function filter(s) {
  // Java strings are NOT the same as JavaScript strings: 
  // make sure to convert to JS strings before using string methods!
  var fs = String(s).replace(/__MODEL__/g, String(getContainerName())); 
  // more filters here
  return fs;
}

function getSchemaURL(kind) {
  return SCHEMA_URL + kind + ".rng";
}

function validateRNG(doc) { // doc is an E4X XML object
  var schemaURL = doc.@schema;
  if (schemaURL == null) throw("Missing schema attribute");
  var req = context.createSubRequest("active:validateRNG");
  req.addArgument("operand", new XmlObjectAspect(doc.getXmlObject()));
  req.addArgument("operator", schemaURL);
  result = context.issueSubRequest(req);
  valid = context.transrept(result, IAspectBoolean).isTrue();
  if (!valid) {
  	var problem = context.transrept(result, IAspectString).getString();
  	throw("Document is invalid: " + problem);
  }
}

// XML DATABASE ACCESS

function databaseExists() {
	return containerExists();
}

function getDatabaseName() {
	return getContainerName();
}

function createDatabase() {
	createContainer();
}

function deleteDatabase() {
	deleteContainer();
	cutGoldenThread("gt:channels");
}

// Get stored element as e4x xml object given its id as string
function getElement(id) {
	var res = getDocument(id);
	// Attach golden thread to element
	res=attachGoldenThread(res, "gt:element:"+ id);
	// Return result as xml object
	var doc = context.transrept(res, IAspectXmlObject);
  	log("Got document " + context.transrept(doc,IAspectString).getString(), "info");
	return new XML(doc.getXmlObject());
}

function updateElement(doc) {
	var id = doc.id[0].text(); // document *must* have id
	log("Updating element with id " + id, "info");
	// Delete older version (must exist else exception)
	deleteDocument(id);
	// Then replace with new version
	putDocument(doc);
	log("Element " + id + " put in container " + getDatabaseName(), "info");	
	// Cut the GoldenThread associated with this resource
	cutGoldenThread("gt:element:"+ id);
	// Cut the GoldenThread associated with all existing queries
	cutGoldenThread("gt:channels");
}

// Run query and return the results as IURepresentation
function queryDatabase(query) {
	log("Processing query: " + query, "info");
	var res = queryContainer(query);
	res=attachGoldenThread(res, "gt:channels");
	log("Got results to query:\n" + context.transrept(res, IAspectString).getString(), "info");
	return res;
}

// Find the ids of all elements of given kind that evaluate refPath to id
function findReferrers(id, kind, refPath) {
	log("Finding " + kind + " referrers of " + id + " by " + refPath, "info");
	var path = new String(refPath);
	var query = "<list>\n" +
								"{\n" +
									"for $e in collection('__MODEL__')/" + kind + "\n" +
									"where $e/" + path.substr(2,path.length) + " = '" + id + "'\n" +
									"return\n" +
									"  <id>{$e/id/text()}</id>\n" +
								"}\n" +
	            "</list>";
	var result = queryContainer(filter(query));
	var list = new XML(context.transrept(result, IAspectString).getString());
	log("Referrers are: \n" + list, "info");
	var ids = [];
	for each (id in list.id) {
		ids.push(id.text());
	}
	return ids;
}

// Deletes in referrer element at cascade references to id at refPath
function deleteReference(referrer, refPath, cascade, id, updated) {
	log("Delete reference to " + id + " by " + referrer + " via " + refPath + " deleting " + cascade, "info");
	var dottedRefPath = new String(refPath).replace(/\.\/|\//g, "."); // transform ./x/y/x into dotted path .x.y.z
	var dottedCascade = new String(cascade).replace(/\.\/|\//g, "."); 
	if (dottedRefPath.indexOf(dottedCascade) != 0){ 	// Invalid cascade path
		log("Error in reference table: " + cascade + " in " + refPath, "info");
		throw("Invalid reference table");
	}
	var els = eval("referrer" + dottedRefPath);
	for (i in els) { // loop explicitly because somehow e4x filtering is broken on non-attribute values.
	  var el = els[i];
		if (el.text() == id) {
			if (dottedRefPath == dottedCascade) { // just delete the element
			  // log("Deleting immediate reference " + el.toString() + " at " + dottedRefPath, "info");
				eval ("delete referrer" + dottedRefPath + "[" + i + "]");
			  log("Deleted immediate reference in " + referrer + " at " + dottedRefPath, "info");
			}
			else { // delete ancestor of element
				// get the ancestor to delete
				var target;
				var delta = dottedRefPath.split(".").length - dottedCascade.split(".").length; // count delta in number of dots
				log("Deleting parent up " + delta + " level(s)", "info");
				for (i=0; i< delta; i++) {
					target = el.parent(); // move up lineage
				}
				els = eval("referrer" + dottedCascade);
				for (i in els) {
					if (els[i] === target) {
						eval ("delete referrer" + dottedCascade + "[" + i + "]");
						log("Delete parent " + target + "=>\n" + referrer, "info");
					}
				}
			}
			// update element
			updateElement(referrer);
			updated.push(referrer.id.text());
		}
	}
}

// Delete all references to an element based on reference table.
function deleteReferencesTo(element, deleted, updated, refTable) {
	log("Deleting references to\n" + element.name() + ":" + element.id.text() + " except for "+ deleted, "info");
	var elName = element.name();
	for each (from in refTable.*.(@to == elName).from) {
	  var refNames = new String(from.@element).split("|");
	  var refPath = from.text();
	  var cascade = (from.@cascade.length() == 0) ? refPath : from.@cascade; // none if same as refPath, if "." then referer needs to be deleted
		for each (kind in refNames) {
			var ids = findReferrers(element.id.text(), kind, refPath);
				for each (id in ids) {
					if (cascade == '.') {
						deleteElementExcept(id, deleted, updated, refTable); // delete element and cascade
					}
					else {
						var referrer = getElement(id);
						// log("Deleting in " + referrer + " reference " + cascade, "info");
						deleteReference(referrer, refPath, cascade, element.id.text(), updated); // remove reference to element from referrer
					}
			}
		}
	}
}

// Return an array containing an array of deleted element IDs and an array of updated element IDs
function deleteElement(id) {
	log("Deleting element " + id, "info");
	var deleted = [];
	var updated = [];
	var refTable = new XML(context.sourceAspect("ffcpl:/resources/schemas/referenceTable.xml", IAspectXmlObject).getXmlObject());
	deleteElementExcept(id, deleted, updated, refTable);
	var ids = [];
	ids[0] = deleted;
	ids[1] = updated;
	// Cut the GoldenThread associated with this resource
	cutGoldenThread("gt:element:"+ id);
	// Cut the GoldenThread associated with all existing queries
	cutGoldenThread("gt:channels");
	return ids;
}

// Returns the list of ids of elements deleted
function deleteElementExcept(id, deleted, updated, refTable) {
	log("Deleting " + id + " except if in " + deleted, "info");
	// delete if not already deleted
	if (!contains(deleted,id)) {
		var deletedDoc = new XML(context.transrept(deleteDocument(id), IAspectXmlObject).getXmlObject());
  	log("Deleted document " + id + " from container " + getDatabaseName(), "info");
		deleted.push(id);
		// Delete referrers to deleted document that are not already deleted
		deleteReferencesTo(deletedDoc, deleted, updated, refTable);
	}
}


// Adds id element if not there, sets schema attribute, validates and stores. Returns doc as stored.
function createElement(xml) {
	var doc = xml;
	var kind = doc.name();
	log("Creating " + kind + ":\n " + doc, "info");
	// Generate and add GUID as root attribute id if needed	
	if (doc.id.length() == 0) {
		var guid = new XML(context.sourceAspect("active:guid",IAspectXmlObject).getXmlObject()).text();
		var id = <id>{guid}</id>;
		doc.insertChildAfter(null, id);
	}
	else {
		log("ID preset "+ doc.id, "warning");
	}
	// (Re)set schema attribute
	doc.@schema = getSchemaURL(kind);
	// Validate
	try {
		validateRNG(doc); // throws an exception if not valid
	}
	catch(e) {
		log("Document of kind " + kind + " is invalid:\n" + doc, "severe");
		throw (e);
	}
	// Store new document (exception if conflict on id)	
	putDocument(doc);	
  	// Cut the GoldenThread associated with all existing queries
	cutGoldenThread("gt:channels");
	log("Created document " + doc + " in database " + getDatabaseName(), "info");
	return doc;
}

// GOLDEN THREAD

function attachGoldenThread(resource, uri) {
	var req=context.createSubRequest("active:attachGoldenThread");
	req.addArgument("operand", resource);
	req.addArgument("param", uri);
	var result=context.issueSubRequest(req);
	log("Attached GT " + uri, "info");
	return result;
}

function cutGoldenThread(uri) {
	var req=context.createSubRequest("active:cutGoldenThread");
	req.addArgument("param", uri);
	context.issueSubRequest(req);
	log("Cut GT " + uri, "info");
}

// SEMAPHORES

MUTEX = 0;

function grabLock(uri, who) {
	if (LOG_MUTEX) log(who + ": Grab " + uri, "info");
	var req=context.createSubRequest("active:lock");
	req.addArgument("operand",uri);
	context.issueSubRequest(req);	
	if (LOG_MUTEX) log(who + ": Grabbed " + uri, "info");
}

function grabReleaseLock(uri, who) {
	if (LOG_MUTEX) log(who + ": Grab & Release " + uri, "info");
	grabLock(uri, who);
	releaseLock(uri, who);
}

function releaseLock(uri, who) {
	if (LOG_MUTEX) log(who + ": Release " + uri, "info");
	var req=context.createSubRequest("active:unlock");
	req.addArgument("operand",uri);
	context.issueSubRequest(req);
	if (LOG_MUTEX) log(who + ": Released " + uri, "info");
}

function incrementMutex(uri, who) {
	var count = 1 + getMutexCount(uri);
	setMutexCount(uri, count);
	if (LOG_MUTEX) log(who + ": Incremented mutex " + uri + " to " + count, "info");
}

function decrementMutex(uri, who) {
	var count = Math.max((getMutexCount(uri) - 1), 0);
	setMutexCount(uri, count);
	if (LOG_MUTEX) log(who + ": Decremented mutex " + uri + " to " + count, "info");
}

function initializeMutex(uri) {
	setMutexCount(uri,0);
}

function setMutexCount(uri, count) {
	// var mutex = <mutex>{count}</mutex>;
	// context.sinkAspect(uri, new XmlObjectAspect(mutex.getXmlObject()));	
	MUTEX = count;
}

function getMutexCount(uri) {
	/*
	var count;
	if (context.exists(uri)) {
		var mutex = new XML(context.sourceAspect(uri, IAspectXmlObject).getXmlObject());
		expire(uri);
		count = parseInt(mutex.text());
		if (isNaN(count)) {
			log("NaN: " + mutex.text(), "severe");
			throw ("Mutex is NaN");
		}
	}
	else {
		// if (LOG_MUTEX) log("Creating mutex " + uri + " at 0", "info");
		count = 0;
		// var mutex = <mutex>{count}</mutex>;
		// context.sinkAspect(uri, new XmlObjectAspect(mutex.getXmlObject()));
	}
	if (LOG_MUTEX) log("Mutex count for " + uri + " = " + count, "info");
	return 1 * count;  // force conversion to number (redundant)
	*/

	if (LOG_MUTEX) log("Mutex count = " + MUTEX, "info");
	return MUTEX;
}

function sleep(msecs) {
	log("Sleeping for " + msecs, "info");
	var req = context.createSubRequest("active:sleep");
	var time = <time>
								{msecs}
							</time>;
	req.addArgument("operator", new XmlObjectAspect(time.getXmlObject()));
	context.issueSubRequest(req);
}


// Locking

// Wait for write lock to be released if grabbed.
// Grab read lock then increment read mutex by one, release read lock.
function beginRead(who) {
	if (LOG_MUTEX) log(who + ": Begin read", "info");
	try {
		grabReleaseLock("lock:write", who); // Can only go through when write lock not already grabbed 
		grabLock("lock:read", who);
		incrementMutex("ffcpl:/mutex/read", who);
	}
	finally {
		releaseLock("lock:read", who);
	}
}

// Grab read lock, decrement read mutex by one, release read lock
function endRead(who) {
	if (LOG_MUTEX) log(who + ": End read", "info");
	try {
		grabLock("lock:read", who);
		decrementMutex("ffcpl:/mutex/read", who);
	}
	finally {
		releaseLock("lock:read", who);
	}
}
// Grab write lock to block new read or writes.
// Grab read lock. If read mutex > 0 then release read lock. Try again (after short sleep).
// When read mutex = 0, grab read lock.
// Release write lock, keeping read lock.
function beginWrite(who) {
	if (LOG_MUTEX) log(who + ": Begin write", "info");
	try {
		grabLock("lock:write", who);
		var done = false;
		do {
			var count;
			try {
				grabLock("lock:read", who);
				count = getMutexCount("ffcpl:/mutex/read", who);
			}
			catch (e) {
				releaseLock("lock:read", who);
				throw e;
			}
			if (count == 0) {
					done = true;
			}
			else {
				releaseLock("lock:read", who);
				sleep(100);
			}
		} while (!done);
	}
	finally {
		releaseLock("lock:write", who);
	}
}

// Release read lock.
function endWrite(who) {
	if (LOG_MUTEX) log(who + ": End write", "info");
	releaseLock("lock:read", who);
}