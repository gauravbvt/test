importPackage(Packages.com.ten60.netkernel.urii.aspect);
utilsURI = "ffcpl:/com/mindalliance/channels/library/scripts/utils.js";
context.importLibrary(utilsURI);

function addVariableArguments(req, obj) {
	for (p in obj) {
		log("Adding variable argument " + p, "info");
		var encoded;
		encoded = "data:text/plain," + escape(obj[p]);
		log("Variable has encoded value: " + encoded, "info");
		req.addArgument(p, encoded);
	}
}

reloadModel();

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
		["taskAgents", {taskId:"task1"}, 1, ["./list/agent/id","./list/agent/name","./list/agent/roleId","./list/agent/taskId"]],
		["rolesOfPerson", {personId:"person1"}, 2, ["./list/role/id","./list/role/name"]],
		["artifactsInScenario", {scenarioId:"scenario1"}, 1, ["./list/artifact/id","./list/artifact/name"]],
		["acquirementsInScenario", {scenarioId:"scenario1"}, 1, ["./list/acquirement/id","./list/acquirement/name"]],
		["agentsInScenario", {scenarioId:"scenario1"}, 1, ["./list/agent/id","./list/agent/name","./list/agent/roleId","./list/agent/taskId"]],
		["categoriesOfElement", {elementId:"event1"}, 2, ["./list/category/id","./list/category/name"]],
		["personOfUser", {userId:"user1"}, 1, ["./list/person/id", "./list/person/lastName"]],
		["personOfUser", {userId:"user999"}, 0, []],
		["phasesInScenario", {scenarioId:"scenario1"}, 2, ["./list/phase/id", "./list/phase/event/id"]],
		["categoriesInTaxonomy", {taxonomy:"event"}, 2, ["./list/category/id"]],
		["disciplinesInTaxonomy", {taxonomy:"event"}, 2, ["./list/discipline/id"]],
		["categoriesInTaxonomyAndDiscipline", {taxonomy:"event",disciplineId:"disc-transportation"}, 1, ["./list/category/id"]],
		["sharingNeedsInScenario", {scenarioId:"scenario1"}, 1, ["./list/sharingNeed/id"]]
	]

try {
	for (i in tests)	 {
		var test = tests[i];
		var queryName = test[0];
		var variables = test[1];
		var count = test[2];
		var xpaths = test[3];
		// Build an issue request
		var req = context.createSubRequest("active:channels_query");
		req.addArgument("xquery", "xquery:" + queryName);
		if (variables != null) {
			addVariableArguments(req, variables); // add arguments <varName>@data:<url-encoded value>
		}
		var res = context.transrept(context.issueSubRequest(req), IAspectXmlObject).getXmlObject();
		// Verify correct number of results in list
		var resCount = res.selectPath("./list/*").length;
		if (resCount != count) {
			var mess = "Query returned " + resCount + " results in " + res + " instead of expected " + count;
			log(mess, "severe");
			throw("Invalid query");
		}
		// Apply xpaths and verify thet they all succeed
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
respond(new BooleanAspect(true));