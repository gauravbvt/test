utilsURI = "ffcpl:/test/utils.js";
context.importLibrary(utilsURI);

importPackage(Packages.org.ten60.netkernel.xml.representation);
importPackage(Packages.com.ten60.netkernel.urii.aspect);
importPackage(Packages.org.apache.xmlbeans);
importPackage(Packages.java.lang);

// Delete model
var req = context.createSubRequest("active:channels_data_deleteModel");
context.issueSubRequest(req);

// Open model from db:testDB.xml
req = context.createSubRequest("active:channels_data_openModel");
req.addArgument("init", "db:testQueriesDB.xml");
context.issueSubRequest(req);

function makeProperties(obj) {
	var props = <properties/>;
	for (p in obj) {
		var prop = <property>
		             <key>{p}</key>
		             <value>{obj[p]}</value>
		           </property>;
	  props.insertChildAfter(null,prop);
	}
	var variables = <variables/>.insertChildAfter(null,props);
	// log("Variables: \n" + variables, "info");
	return variables
}

var tests = 
	[
		["allOrganizations", null, 2, ["./list/organization/id","./list/organization/name"]], // [<queryName>, <variables as object properties>, [<xpath>,...]]
		["allPersons", null, 2, ["./list/person/id","./list/person/lastName"]],
		["allProjects", null, 1, ["./list/project/id","./list/project/name"]],
		["allRepositories", null, 1, ["./list/repository/id","./list/repository/name"]],
		["allRoles", null, 2, ["./list/role/id","./list/role/name"]],
		["scenariosInProject", {projectId:"project1"}, 1, ["./list/scenario/id","./list/scenario/name"]],
		["eventsInScenario", {scenarioId:"scenario1"}, 2, ["./list/event/id","./list/event/name"]],
		["tasksInScenario", {scenarioId:"scenario1"}, 2, ["./list/task/id","./list/task/name"]],
		["taskArtifacts", {taskId:"task1"}, 1, ["./list/artifact/id","./list/artifact/name"]],
		["taskAcquirements", {taskId:"task1"}, 1, ["./list/acquirement/id","./list/acquirement/name"]],
		["taskAgents", {taskId:"task1"}, 1, ["./list/agent/id","./list/agent/name","./list/agent/role/id","./list/agent/role/name"]],
		["rolesOfPerson", {personId:"person1"}, 2, ["./list/role/id","./list/role/name"]],
		["artifactsInScenario", {scenarioId:"scenario1"}, 1, ["./list/artifact/id","./list/artifact/name"]],
		["acquirementsInScenario", {scenarioId:"scenario1"}, 1, ["./list/acquirement/id","./list/acquirement/name"]],
		["categoriesOfElement", {elementId:"event1"}, 2, ["./list/category/id","./list/category/name"]]
	]

try {
	for (i in tests)	 {
		var test = tests[i];
		var queryName = test[0];
		var variables = test[1];
		var count = test[2];
		var xpaths = test[3];
		// Build an issue request
		var req = context.createSubRequest("active:channels_data_queryModel");
		req.addArgument("xquery", "ffcpl:/com/mindalliance/channels/data/queries/" + queryName + ".xq");
		if (variables != null) {
			var properties = makeProperties(variables); // returns <properties><property><key>...</key><value>...</value></property>...</properties> e4x xml object
			req.addArgument("variables", new XmlObjectAspect(properties.getXmlObject()));
		}
		var res = context.transrept(context.issueSubRequest(req), IAspectXmlObject).getXmlObject();
		var resCount = res.selectPath("./list/*").length;
		if (resCount != count) {
			var mess = "Query returned " + resCount + " results instead of the expected " + count;
			log(mess, "severe");
			throw("Invalid query");
		}
		// Apply xpaths
		for (j in xpaths) {
			var xpath = xpaths[j];
			var r = res.selectPath(xpath);
			if (r.length == 0) {
				var mess = "Query " + queryName + " failed test " + xpath;
				log(mess, "severe");
				throw("Invalid query"); // fail early
			}
		}
	}
}
catch(e) {
	log("EXCEPTION: " + e, "severe");
	throw e;
}

// Respond
var resp = context.createResponseFrom(new BooleanAspect(true));
context.setResponse(resp);