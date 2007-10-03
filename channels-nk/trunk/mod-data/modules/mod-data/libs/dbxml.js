/*
This library implements the XML database plugin functions:

function containerExists();
function createContainer();
function deleteContainer();
function getContainerName();
function getDocument(id);
function putDocument(doc);
function deleteDocument(id);
function queryContainer(query);
*/

importPackage(Packages.java.lang);
importPackage(Packages.org.ten60.netkernel.xml.representation);
importPackage(Packages.com.ten60.netkernel.urii.aspect);

DBXML_CONFIG_URI = "ffcpl:/etc/dbxml_config.xml";

// Returns an E4X dbxml descriptor
function getContainerDescriptor() {
  var configs = new XML(context.sourceAspect(DBXML_CONFIG_URI, IAspectXmlObject).getXmlObject());
  var env = configs.@env;
  var descriptor = configs.config.(@name==env).dbxml;
  // log("Using dbxml descriptor: " + descriptor, "info");
  return descriptor;
}

// Return a document descriptor given id in a parameter
function getDocumentDescriptor(id) {
  var descriptor =  <dbxml>
                  		<name>{id}</name>
                  		<container>{getContainerName()}</container>
                		</dbxml>;

  return descriptor;
}

function addDBXMLIndex(nodeName, indexType) {
	var indexDesc = <dbxml>
				<name>{getContainerName()}</name>
				<index>
					<nodeName>{nodeName}</nodeName>
					<type>{indexType}</type>
				</index>
			 </dbxml>;
			
	var req=context.createSubRequest("active:dbxmlAddIndex");
	req.addArgument("operator", new XmlObjectAspect(indexDesc.getXmlObject()));
	res=context.issueSubRequest(req);
	
	log("Added database index: " + nodeName + ", type: " + indexType, "info");	
}

function addDBXMLIndices() {
	addDBXMLIndex("/agent/id", "unique-edge-element-equality-string");
	addDBXMLIndex("/acquirement/id", "unique-edge-element-equality-string");
	addDBXMLIndex("/artifact/id", "unique-edge-element-equality-string");
	addDBXMLIndex("/category/id", "unique-edge-element-equality-string");	
	addDBXMLIndex("/event/id", "unique-edge-element-equality-string");	
	addDBXMLIndex("/know/id", "unique-edge-element-equality-string");		
	addDBXMLIndex("/needToKnow/id", "unique-edge-element-equality-string");	
	addDBXMLIndex("/organization/id", "unique-edge-element-equality-string");
	addDBXMLIndex("/person/id", "unique-edge-element-equality-string");
	addDBXMLIndex("/phase/id", "unique-edge-element-equality-string");	
	addDBXMLIndex("/project/id", "unique-edge-element-equality-string");
	addDBXMLIndex("/repository/id", "unique-edge-element-equality-string");	
	addDBXMLIndex("/role/id", "unique-edge-element-equality-string");
	addDBXMLIndex("/scenario/id", "unique-edge-element-equality-string");
	addDBXMLIndex("/task/id", "unique-edge-element-equality-string");	
	addDBXMLIndex("/user/id", "unique-edge-element-equality-string");
}

// XML DATABASE PLUGIN FUNCTIONS

// Returns whether a dbxml container as described exists
function containerExists() {
	var descriptor = getContainerDescriptor();
  // Check if already exists
  var req=context.createSubRequest("active:dbxmlExistsContainer");
  req.addArgument("operator", new XmlObjectAspect(descriptor.getXmlObject()) );
  res = context.issueSubRequest(req);
  return context.transrept(res, IAspectBoolean).isTrue();
}

function createContainer() {
	var descriptor = getContainerDescriptor();
	var req=context.createSubRequest("active:dbxmlCreateContainer");
  req.addArgument("operator", new XmlObjectAspect(descriptor.getXmlObject()) );
  context.issueSubRequest(req);
}

function deleteContainer() {
	// Delete container
  var descriptor = getContainerDescriptor();
  var req=context.createSubRequest("active:dbxmlDeleteContainer");
  req.addArgument("operator", new XmlObjectAspect(descriptor.getXmlObject()) );
  context.issueSubRequest(req);
}


function getContainerName() {
  var descriptor = getContainerDescriptor();
  return descriptor.name.text();
}

// Get stored document as representation
function getDocument(id) {
	var op =  getDocumentDescriptor(id);	
	// log("Document descriptor: " + op, "info");
	var req = context.createSubRequest("active:dbxmlGetDocument");
	req.addArgument("operator", new XmlObjectAspect(op.getXmlObject()) );
	return context.issueSubRequest(req);
}

// Store e4x doc in currently opened database
function putDocument(doc) {
	var id = doc.id[0].text(); // document *must* have id
  var op =  getDocumentDescriptor(id);
  var req=context.createSubRequest("active:dbxmlPutDocument");
  req.addArgument("operand", new XmlObjectAspect(doc.getXmlObject()));
  req.addArgument("operator", new XmlObjectAspect(op.getXmlObject()));
  context.issueSubRequest(req);
}

// Deletes document by name. if exists. Returns the deleted document's representation. Raise exception if document does not exist.
function deleteDocument(id) {
	var deleted = getDocument(id);
  var op =  getDocumentDescriptor(id);
  var req=context.createSubRequest("active:dbxmlDeleteDocument");
  req.addArgument("operator", new XmlObjectAspect(op.getXmlObject()));
  context.issueSubRequest(req);
  return deleted;
}

function queryContainer(query) {
	var op =  "<dbxml>\n" +
      		" <container>" + getContainerName() + "</container>\n" +
      		" <xquery>\n" +
      		"  <![CDATA[\n   " + query + "\n  ]]>\n" +
      		" </xquery>\n" +
      	 "</dbxml>";
	var req=context.createSubRequest("active:dbxmlQuery");
	req.addArgument("operator", new StringAspect(op));
	return context.issueSubRequest(req);
}
