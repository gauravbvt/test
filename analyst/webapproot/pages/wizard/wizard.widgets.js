wizard.widgets = {
	logoutVariable1: ["wm.LogoutVariable", {"inFlightBehavior":"executeLast"}, {}, {
		input: ["wm.ServiceInput", {"type":"logoutInputs"}, {}]
	}],
	projectLiveVariable1: ["wm.LiveVariable", {"autoUpdate":false,"type":"com.analystdb.data.Project"}, {}, {
		liveView: ["wm.LiveView", {"dataType":"com.analystdb.data.Project","view":[{"caption":"TenantId","sortable":true,"dataIndex":"tenantId","type":"java.lang.Integer","displayType":"Number","required":true,"readonly":false,"includeLists":true,"includeForms":true,"order":4,"subType":null},{"caption":"Id","sortable":true,"dataIndex":"id","type":"java.lang.Long","displayType":"Number","required":true,"readonly":true,"includeLists":true,"includeForms":true,"order":4000,"subType":null,"widthUnits":"px"},{"caption":"Name","sortable":true,"dataIndex":"name","type":"java.lang.String","displayType":"Text","required":true,"readonly":false,"includeLists":true,"includeForms":true,"order":4001,"subType":null,"widthUnits":"px"},{"caption":"Description","sortable":true,"dataIndex":"description","type":"java.lang.String","displayType":"Text","required":false,"readonly":false,"includeLists":true,"includeForms":true,"order":4002,"subType":null,"widthUnits":"px"},{"caption":"Version","sortable":true,"dataIndex":"version","type":"java.lang.Integer","displayType":"Number","required":false,"readonly":false,"includeLists":true,"includeForms":true,"order":4003,"subType":null,"widthUnits":"px"}]}, {}]
	}],
	plansVariable1: ["wm.ServiceVariable", {"inFlightBehavior":"executeLast","operation":"projectPlans","service":"analystDB"}, {}, {
		input: ["wm.ServiceInput", {"type":"projectPlansInputs"}, {}, {
			binding: ["wm.Binding", {}, {}, {
				wire: ["wm.Wire", {"expression":undefined,"source":"projectDojoGrid.selectedItem.id","targetProperty":"project"}, {}]
			}]
		}]
	}],
	issueLiveVariable1: ["wm.LiveVariable", {"autoUpdate":false,"orderBy":"asc: sequence","startUpdate":false,"type":"com.analystdb.data.Issue"}, {}, {
		binding: ["wm.Binding", {}, {}, {
			wire: ["wm.Wire", {"expression":undefined,"source":"projectDojoGrid.selectedItem","targetProperty":"filter.project"}, {}]
		}],
		liveView: ["wm.LiveView", {"dataType":"com.analystdb.data.Issue","related":["project"],"view":[{"caption":"Name","sortable":true,"dataIndex":"name","type":"java.lang.String","displayType":"Text","required":true,"readonly":false,"includeLists":true,"includeForms":true,"order":2,"subType":null},{"caption":"Version","sortable":true,"dataIndex":"version","type":"java.lang.Integer","displayType":"Number","required":false,"readonly":false,"includeLists":true,"includeForms":true,"order":4,"subType":null},{"caption":"Id","sortable":true,"dataIndex":"id","type":"java.lang.Long","displayType":"Number","required":true,"readonly":true,"includeLists":true,"includeForms":true,"order":1000,"subType":null,"widthUnits":"px"},{"caption":"Sequence","sortable":true,"dataIndex":"sequence","type":"java.lang.Integer","displayType":"Number","required":true,"readonly":false,"includeLists":true,"includeForms":true,"order":1001,"subType":null,"widthUnits":"px"},{"caption":"Description","sortable":true,"dataIndex":"description","type":"java.lang.String","displayType":"Text","required":false,"readonly":false,"includeLists":true,"includeForms":true,"order":1003,"subType":null,"widthUnits":"px"},{"caption":"Id","sortable":true,"dataIndex":"project.id","type":"java.lang.Long","displayType":"Number","required":true,"readonly":true,"includeLists":true,"includeForms":true,"order":2000,"subType":null,"widthUnits":"px"}]}, {}]
	}],
	recentInterviewsVariable1: ["wm.ServiceVariable", {"inFlightBehavior":"executeLast","operation":"recentInterviews","service":"analystDB"}, {"onSuccess":"recentInterviewsVariable1Success"}, {
		input: ["wm.ServiceInput", {"type":"recentInterviewsInputs"}, {}, {
			binding: ["wm.Binding", {}, {}, {
				wire: ["wm.Wire", {"expression":undefined,"source":"projectDojoGrid.selectedItem.id","targetProperty":"project"}, {}]
			}]
		}]
	}],
	resourcesVariable1: ["wm.LiveVariable", {"autoUpdate":false,"inFlightBehavior":"executeLast","orderBy":"asc: name","startUpdate":false,"type":"com.analystdb.data.Resource"}, {}, {
		binding: ["wm.Binding", {}, {}, {
			wire: ["wm.Wire", {"expression":undefined,"source":"projectDojoGrid.selectedItem","targetProperty":"filter.project"}, {}]
		}],
		liveView: ["wm.LiveView", {"dataType":"com.analystdb.data.Resource","view":[{"caption":"Id","sortable":true,"dataIndex":"id","type":"java.lang.Long","displayType":"Number","required":true,"readonly":true,"includeLists":true,"includeForms":true,"order":0,"subType":null},{"caption":"Version","sortable":true,"dataIndex":"version","type":"java.lang.Integer","displayType":"Number","required":false,"readonly":false,"includeLists":true,"includeForms":true,"order":3,"subType":null},{"caption":"Description","sortable":true,"dataIndex":"description","type":"java.lang.String","displayType":"Text","required":false,"readonly":false,"includeLists":true,"includeForms":true,"order":1001,"subType":null,"widthUnits":"px"},{"caption":"Name","sortable":true,"dataIndex":"name","type":"java.lang.String","displayType":"Text","required":true,"readonly":false,"includeLists":true,"includeForms":true,"order":1002,"subType":null,"widthUnits":"px"}]}, {}]
	}],
	upcomingInterviewsVariable1: ["wm.ServiceVariable", {"inFlightBehavior":undefined,"operation":"upcomingInterviews","service":"analystDB"}, {}, {
		input: ["wm.ServiceInput", {"type":"upcomingInterviewsInputs"}, {}, {
			binding: ["wm.Binding", {}, {}, {
				wire: ["wm.Wire", {"expression":"new Date()","targetProperty":"now"}, {}],
				wire1: ["wm.Wire", {"expression":undefined,"source":"projectDojoGrid.selectedItem.id","targetProperty":"project"}, {}]
			}]
		}]
	}],
	resourceInsert1: ["wm.LiveVariable", {"autoUpdate":false,"inFlightBehavior":"executeLast","operation":"insert","startUpdate":false,"type":"com.analystdb.data.Resource"}, {"onSuccess":"resourceInsert1Result","onSuccess1":"resourcesVariable1"}, {
		binding: ["wm.Binding", {}, {}, {
			wire: ["wm.Wire", {"expression":undefined,"source":"projectDojoGrid.selectedItem","targetProperty":"sourceData.project"}, {}]
		}],
		liveView: ["wm.LiveView", {"dataType":"com.analystdb.data.Resource","view":[{"caption":"Id","sortable":true,"dataIndex":"id","type":"java.lang.Long","displayType":"Number","required":true,"readonly":true,"includeLists":true,"includeForms":true,"order":1000,"subType":null,"widthUnits":"px"},{"caption":"Name","sortable":true,"dataIndex":"name","type":"java.lang.String","displayType":"Text","required":true,"readonly":false,"includeLists":true,"includeForms":true,"order":1001,"subType":null,"widthUnits":"px"},{"caption":"Description","sortable":true,"dataIndex":"description","type":"java.lang.String","displayType":"Text","required":false,"readonly":false,"includeLists":true,"includeForms":true,"order":1002,"subType":null,"widthUnits":"px"},{"caption":"Version","sortable":true,"dataIndex":"version","type":"java.lang.Integer","displayType":"Number","required":false,"readonly":false,"includeLists":true,"includeForms":true,"order":1003,"subType":null,"widthUnits":"px"}]}, {}]
	}],
	documentcategoryLiveVariable1: ["wm.LiveVariable", {"autoUpdate":false,"orderBy":"asc: sequence","type":"com.analystdb.data.DocumentCategory"}, {}, {
		liveView: ["wm.LiveView", {"dataType":"com.analystdb.data.DocumentCategory","view":[{"caption":"TenantId","sortable":true,"dataIndex":"tenantId","type":"java.lang.Integer","displayType":"Number","required":true,"readonly":false,"includeLists":true,"includeForms":true,"order":3,"subType":null},{"caption":"Id","sortable":true,"dataIndex":"id","type":"java.lang.Integer","displayType":"Number","required":true,"readonly":true,"includeLists":true,"includeForms":true,"order":1000,"subType":null,"widthUnits":"px"},{"caption":"Name","sortable":true,"dataIndex":"name","type":"java.lang.String","displayType":"Text","required":true,"readonly":false,"includeLists":true,"includeForms":true,"order":1001,"subType":null,"widthUnits":"px"},{"caption":"Sequence","sortable":true,"dataIndex":"sequence","type":"java.lang.Short","displayType":"Number","required":false,"readonly":false,"includeLists":true,"includeForms":true,"order":1002,"subType":null,"widthUnits":"px"}]}, {}]
	}],
	issueCategoryCount: ["wm.ServiceVariable", {"inFlightBehavior":"executeLast","operation":"issueCategoryCounts","service":"analystDB"}, {}, {
		input: ["wm.ServiceInput", {"type":"issueCategoryCountsInputs"}, {}, {
			binding: ["wm.Binding", {}, {}, {
				wire: ["wm.Wire", {"expression":undefined,"source":"projectDojoGrid.selectedItem.id","targetProperty":"project"}, {}]
			}]
		}]
	}],
	docCatIssuesVariable1: ["wm.ServiceVariable", {"inFlightBehavior":"executeLast","operation":"documentCategoryIssueCounts","service":"analystDB"}, {}, {
		input: ["wm.ServiceInput", {"type":"documentCategoryIssueCountsInputs"}, {}, {
			binding: ["wm.Binding", {}, {}, {
				wire: ["wm.Wire", {"expression":undefined,"source":"projectDojoGrid.selectedItem.id","targetProperty":"project"}, {}]
			}]
		}]
	}],
	docIssuesCountsVariable1: ["wm.ServiceVariable", {"inFlightBehavior":"executeLast","operation":"documentIssueCount","service":"analystDB"}, {}, {
		input: ["wm.ServiceInput", {"type":"documentIssueCountInputs"}, {}, {
			binding: ["wm.Binding", {}, {}, {
				wire: ["wm.Wire", {"expression":undefined,"source":"projectDojoGrid.selectedItem.id","targetProperty":"project"}, {}],
				wire1: ["wm.Wire", {"expression":undefined,"source":"dojoGrid2.selectedItem.category.id","targetProperty":"category"}, {}]
			}]
		}]
	}],
	docIssuesVariable1: ["wm.ServiceVariable", {"inFlightBehavior":"executeLast","operation":"documentIssues","service":"analystDB"}, {}, {
		input: ["wm.ServiceInput", {"type":"documentIssuesInputs"}, {}, {
			binding: ["wm.Binding", {}, {}, {
				wire: ["wm.Wire", {"expression":undefined,"source":"projectDojoGrid.selectedItem.id","targetProperty":"project"}, {}],
				wire2: ["wm.Wire", {"expression":"if ( ${dojoGrid2.isRowSelected} ) { ${dojoGrid2.selectedItem.category.id}; } else { -1; }","targetProperty":"phase"}, {}],
				wire1: ["wm.Wire", {"expression":"if ( ${dojoGrid3.isRowSelected} ) { ${dojoGrid3.selectedItem.id}; } else {  -1; }","targetProperty":"document"}, {}]
			}]
		}]
	}],
	approachIssueCountVariable1: ["wm.ServiceVariable", {"inFlightBehavior":"executeLast","operation":"approachIssueCount","service":"analystDB"}, {}, {
		input: ["wm.ServiceInput", {"type":"approachIssueCountInputs"}, {}, {
			binding: ["wm.Binding", {}, {}, {
				wire: ["wm.Wire", {"expression":undefined,"source":"projectDojoGrid.selectedItem.id","targetProperty":"project"}, {}]
			}]
		}]
	}],
	approachIssuesVariable1: ["wm.ServiceVariable", {"inFlightBehavior":"executeLast","operation":"approachIssues","service":"analystDB"}, {}, {
		input: ["wm.ServiceInput", {"type":"approachIssuesInputs"}, {}, {
			binding: ["wm.Binding", {}, {}, {
				wire: ["wm.Wire", {"expression":undefined,"source":"projectDojoGrid.selectedItem.id","targetProperty":"project"}, {}],
				wire1: ["wm.Wire", {"expression":undefined,"source":"dojoGrid6.selectedItem.approach.id","targetProperty":"approach"}, {}]
			}]
		}]
	}],
	liveForm1Readonly: ["wm.Property", {"bindSource":undefined,"bindTarget":undefined,"property":"liveForm1.readonly"}, {}],
	flowsByProjectVariable1: ["wm.ServiceVariable", {"inFlightBehavior":"executeLast","operation":"flowsByProject","service":"analystDB"}, {}, {
		input: ["wm.ServiceInput", {"type":"flowsByProjectInputs"}, {}, {
			binding: ["wm.Binding", {}, {}, {
				wire: ["wm.Wire", {"expression":undefined,"source":"projectDojoGrid.selectedItem.id","targetProperty":"project"}, {}]
			}]
		}]
	}],
	interviewLiveVariable1: ["wm.LiveVariable", {"autoUpdate":false,"inFlightBehavior":"executeLast","operation":"insert","startUpdate":false,"type":"com.analystdb.data.Interview"}, {"onSuccess":"upcomingInterviewsVariable1","onSuccess2":"interviewLiveVariable1Success2"}, {
		liveView: ["wm.LiveView", {"dataType":"com.analystdb.data.Interview","related":["resource"],"view":[{"caption":"Id","sortable":true,"dataIndex":"id","type":"java.lang.Long","displayType":"Number","required":true,"readonly":true,"includeLists":true,"includeForms":true,"order":1000,"subType":null,"widthUnits":"px"},{"caption":"Scheduled","sortable":true,"dataIndex":"scheduled","type":"java.util.Date","displayType":"Date","required":true,"readonly":false,"includeLists":true,"includeForms":true,"order":1001,"subType":null,"widthUnits":"px"},{"caption":"Notes","sortable":true,"dataIndex":"notes","type":"java.lang.String","displayType":"Text","required":false,"readonly":false,"includeLists":true,"includeForms":true,"order":1002,"subType":null,"widthUnits":"px"},{"caption":"Done","sortable":true,"dataIndex":"done","type":"java.lang.Boolean","displayType":"CheckBox","required":true,"readonly":false,"includeLists":true,"includeForms":true,"order":1003,"subType":null,"widthUnits":"px"},{"caption":"Version","sortable":true,"dataIndex":"version","type":"java.lang.Integer","displayType":"Number","required":false,"readonly":false,"includeLists":true,"includeForms":true,"order":1004,"subType":null,"widthUnits":"px"},{"caption":"Name","sortable":true,"dataIndex":"resource.name","type":"java.lang.String","displayType":"Text","required":true,"widthUnits":"px","includeLists":true,"includeForms":true,"order":2001}]}, {}]
	}],
	analysisInsert: ["wm.LiveVariable", {"autoUpdate":false,"inFlightBehavior":"executeLast","operation":"insert","startUpdate":false,"type":"com.analystdb.data.Analysis"}, {}, {
		binding: ["wm.Binding", {}, {}, {
			wire: ["wm.Wire", {"expression":undefined,"source":"projectLiveForm1.dataOutput","targetProperty":"sourceData.project"}, {}],
			wire1: ["wm.Wire", {"expression":"\"Default\"","targetProperty":"sourceData.name"}, {}],
			wire2: ["wm.Wire", {"expression":"\"Created with project\"","targetProperty":"sourceData.description"}, {}]
		}],
		liveView: ["wm.LiveView", {"dataType":"com.analystdb.data.Analysis","view":[{"caption":"Id","sortable":true,"dataIndex":"id","type":"java.lang.Long","displayType":"Number","required":true,"readonly":true,"includeLists":true,"includeForms":true,"order":0,"subType":null},{"caption":"Version","sortable":true,"dataIndex":"version","type":"java.lang.Integer","displayType":"Number","required":false,"readonly":false,"includeLists":true,"includeForms":true,"order":3,"subType":null},{"caption":"Description","sortable":true,"dataIndex":"description","type":"java.lang.String","displayType":"Text","required":false,"readonly":false,"includeLists":true,"includeForms":true,"order":2001,"subType":null,"widthUnits":"px"},{"caption":"Name","sortable":true,"dataIndex":"name","type":"java.lang.String","displayType":"Text","required":true,"readonly":false,"includeLists":true,"includeForms":true,"order":2002,"subType":null,"widthUnits":"px"}]}, {}]
	}],
	approachLiveVariable1: ["wm.LiveVariable", {"autoUpdate":false,"inFlightBehavior":"executeLast","startUpdate":false,"type":"com.analystdb.data.Approach"}, {}, {
		liveView: ["wm.LiveView", {"dataType":"com.analystdb.data.Approach","related":["interview"],"view":[{"caption":"Id","sortable":true,"dataIndex":"id","type":"java.lang.Long","displayType":"Number","required":true,"readonly":true,"includeLists":true,"includeForms":true,"order":0,"subType":null},{"caption":"Id","sortable":true,"dataIndex":"interview.id","type":"java.lang.Long","displayType":"Number","required":true,"readonly":true,"includeLists":true,"includeForms":true,"order":0,"subType":null},{"caption":"Name","sortable":true,"dataIndex":"name","type":"java.lang.String","displayType":"Text","required":true,"readonly":false,"includeLists":true,"includeForms":true,"order":1,"subType":null},{"caption":"Scheduled","sortable":true,"dataIndex":"interview.scheduled","type":"java.util.Date","displayType":"Date","required":true,"readonly":false,"includeLists":true,"includeForms":true,"order":1,"subType":null},{"caption":"Description","sortable":true,"dataIndex":"description","type":"java.lang.String","displayType":"Text","required":false,"readonly":false,"includeLists":true,"includeForms":true,"order":2,"subType":null},{"caption":"Notes","sortable":true,"dataIndex":"interview.notes","type":"java.lang.String","displayType":"Text","required":false,"readonly":false,"includeLists":true,"includeForms":true,"order":2,"subType":null},{"caption":"Version","sortable":true,"dataIndex":"version","type":"java.lang.Integer","displayType":"Number","required":false,"readonly":false,"includeLists":true,"includeForms":true,"order":3,"subType":null},{"caption":"Done","sortable":true,"dataIndex":"interview.done","type":"java.lang.Boolean","displayType":"CheckBox","required":true,"readonly":false,"includeLists":true,"includeForms":true,"order":3,"subType":null},{"caption":"Version","sortable":true,"dataIndex":"interview.version","type":"java.lang.Integer","displayType":"Number","required":false,"readonly":false,"includeLists":true,"includeForms":true,"order":4,"subType":null},{"caption":"Start","sortable":true,"dataIndex":"start","type":"java.util.Date","displayType":"Date","required":false,"readonly":false,"includeLists":true,"includeForms":true,"order":4,"subType":null},{"caption":"End","sortable":true,"dataIndex":"end","type":"java.util.Date","displayType":"Date","required":false,"readonly":false,"includeLists":true,"includeForms":true,"order":5,"subType":null},{"caption":"Approved","sortable":true,"dataIndex":"approved","type":"java.lang.Boolean","displayType":"CheckBox","required":true,"readonly":false,"includeLists":true,"includeForms":true,"order":6,"subType":null},{"caption":"Cost","sortable":true,"dataIndex":"cost","type":"java.lang.Integer","displayType":"Number","required":false,"readonly":false,"includeLists":true,"includeForms":true,"order":7,"subType":null}]}, {}],
		binding: ["wm.Binding", {}, {}, {
			wire: ["wm.Wire", {"expression":undefined,"source":"interviewDojoGrid1.selectedItem.interview","targetProperty":"filter.interview"}, {}]
		}]
	}],
	approachIssuesVariable: ["wm.LiveVariable", {"autoUpdate":false,"inFlightBehavior":"executeLast","startUpdate":false,"type":"com.analystdb.data.IssueApproach"}, {}, {
		liveView: ["wm.LiveView", {"dataType":"com.analystdb.data.IssueApproach","related":["issue","approach","approach.interview"],"view":[{"caption":"Sequence","sortable":true,"dataIndex":"issue.sequence","type":"java.lang.Integer","displayType":"Number","required":true,"readonly":false,"includeLists":true,"includeForms":true,"order":6001,"subType":null,"widthUnits":"px"},{"caption":"Description","sortable":true,"dataIndex":"issue.description","type":"java.lang.String","displayType":"Text","required":false,"readonly":false,"includeLists":true,"includeForms":true,"order":6003,"subType":null,"widthUnits":"px"},{"caption":"Name","sortable":true,"dataIndex":"approach.name","type":"java.lang.String","displayType":"Text","required":true,"readonly":false,"includeLists":true,"includeForms":true,"order":7001,"subType":null,"widthUnits":"px"},{"caption":"Description","sortable":true,"dataIndex":"approach.description","type":"java.lang.String","displayType":"Text","required":false,"readonly":false,"includeLists":true,"includeForms":true,"order":7002,"subType":null,"widthUnits":"px"},{"caption":"Id","sortable":true,"dataIndex":"approach.interview.id","type":"java.lang.Long","displayType":"Number","required":true,"widthUnits":"px","includeLists":true,"includeForms":true,"order":8000}]}, {}],
		binding: ["wm.Binding", {}, {}, {
			wire: ["wm.Wire", {"expression":undefined,"source":"approachDojoGrid.selectedItem","targetProperty":"filter.approach"}, {}]
		}]
	}],
	issueApproachesVariable: ["wm.LiveVariable", {"autoUpdate":false,"inFlightBehavior":"executeLast","startUpdate":false,"type":"com.analystdb.data.Approach"}, {}, {
		binding: ["wm.Binding", {}, {}, {
			wire: ["wm.Wire", {"expression":undefined,"source":"dataGrid2.selectedItem.issue","targetProperty":"filter.issue"}, {}]
		}],
		liveView: ["wm.LiveView", {"dataType":"com.analystdb.data.Approach","related":["interview"],"view":[{"caption":"Id","sortable":true,"dataIndex":"id","type":"java.lang.Long","displayType":"Number","required":true,"readonly":true,"includeLists":true,"includeForms":true,"order":1000,"subType":null,"widthUnits":"px"},{"caption":"Name","sortable":true,"dataIndex":"name","type":"java.lang.String","displayType":"Text","required":true,"readonly":false,"includeLists":true,"includeForms":true,"order":1001,"subType":null,"widthUnits":"px"},{"caption":"Description","sortable":true,"dataIndex":"description","type":"java.lang.String","displayType":"Text","required":false,"readonly":false,"includeLists":true,"includeForms":true,"order":1002,"subType":null,"widthUnits":"px"},{"caption":"Version","sortable":true,"dataIndex":"version","type":"java.lang.Integer","displayType":"Number","required":false,"readonly":false,"includeLists":true,"includeForms":true,"order":1003,"subType":null,"widthUnits":"px"},{"caption":"Start","sortable":true,"dataIndex":"start","type":"java.util.Date","displayType":"Date","required":false,"widthUnits":"px","includeLists":true,"includeForms":true,"order":1004},{"caption":"End","sortable":true,"dataIndex":"end","type":"java.util.Date","displayType":"Date","required":false,"widthUnits":"px","includeLists":true,"includeForms":true,"order":1005},{"caption":"Approved","sortable":true,"dataIndex":"approved","type":"java.lang.Boolean","displayType":"CheckBox","required":true,"widthUnits":"px","includeLists":true,"includeForms":true,"order":1006},{"caption":"Cost","sortable":true,"dataIndex":"cost","type":"java.lang.Integer","displayType":"Number","required":false,"widthUnits":"px","includeLists":true,"includeForms":true,"order":1007},{"caption":"Id","sortable":true,"dataIndex":"interview.id","type":"java.lang.Long","displayType":"Number","required":true,"widthUnits":"px","includeLists":true,"includeForms":true,"order":2000}]}, {}]
	}],
	issueAttributesVariable: ["wm.LiveVariable", {"autoUpdate":false,"startUpdate":false,"type":"com.analystdb.data.IssueAttribute"}, {}, {
		binding: ["wm.Binding", {}, {}, {
			wire: ["wm.Wire", {"expression":undefined,"source":"dataGrid2.selectedItem","targetProperty":"filter.issueComment"}, {}]
		}],
		liveView: ["wm.LiveView", {"dataType":"com.analystdb.data.IssueAttribute","view":[{"caption":"Id","sortable":true,"dataIndex":"id","type":"java.lang.Long","displayType":"Number","required":true,"readonly":true,"includeLists":true,"includeForms":true,"order":0,"subType":null},{"caption":"Version","sortable":true,"dataIndex":"version","type":"java.lang.Integer","displayType":"Number","required":false,"readonly":false,"includeLists":true,"includeForms":true,"order":3,"subType":null},{"caption":"Name","sortable":true,"dataIndex":"name","type":"java.lang.String","displayType":"Text","required":true,"readonly":false,"includeLists":true,"includeForms":true,"order":14001,"subType":null,"widthUnits":"px"},{"caption":"Val","sortable":true,"dataIndex":"val","type":"java.lang.String","displayType":"Text","required":true,"readonly":false,"includeLists":true,"includeForms":true,"order":14002,"subType":null,"widthUnits":"px"}]}, {}]
	}],
	flowIssuesVariable: ["wm.LiveVariable", {"autoUpdate":false,"inFlightBehavior":"executeLast","startUpdate":false,"type":"com.analystdb.data.IssueCommentFlows"}, {}, {
		binding: ["wm.Binding", {}, {}, {
			wire: ["wm.Wire", {"expression":undefined,"source":"flowDojoGrid.selectedItem","targetProperty":"filter.flow"}, {}]
		}],
		liveView: ["wm.LiveView", {"dataType":"com.analystdb.data.IssueCommentFlows","related":["issueComment","issueComment.interview","issueComment.issue"],"view":[{"caption":"Description","sortable":true,"dataIndex":"issueComment.description","type":"java.lang.String","displayType":"Text","required":false,"widthUnits":"px","includeLists":true,"includeForms":true,"order":2001},{"caption":"Sequence","sortable":true,"dataIndex":"issueComment.issue.sequence","type":"java.lang.Integer","displayType":"Number","required":true,"widthUnits":"px","includeLists":true,"includeForms":true,"order":3001},{"caption":"Description","sortable":true,"dataIndex":"issueComment.issue.description","type":"java.lang.String","displayType":"Text","required":false,"widthUnits":"px","includeLists":true,"includeForms":true,"order":3003},{"caption":"Id","sortable":true,"dataIndex":"issueComment.interview.id","type":"java.lang.Long","displayType":"Number","required":true,"widthUnits":"px","includeLists":true,"includeForms":true,"order":4000}]}, {}]
	}],
	interviewIssuesVariable1: ["wm.LiveVariable", {"autoUpdate":false,"inFlightBehavior":"executeLast","orderBy":"asc: issue.sequence","startUpdate":false,"type":"com.analystdb.data.IssueComment"}, {"onSuccess":"interviewIssuesVariable1Success"}, {
		binding: ["wm.Binding", {}, {}, {
			wire: ["wm.Wire", {"expression":undefined,"source":"interviewDojoGrid1.selectedItem.interview","targetProperty":"filter.interview"}, {}]
		}],
		liveView: ["wm.LiveView", {"dataType":"com.analystdb.data.IssueComment","related":["issue","interview"],"view":[{"caption":"Id","sortable":true,"dataIndex":"id","type":"java.lang.Long","displayType":"Number","required":true,"readonly":true,"includeLists":true,"includeForms":true,"order":1000,"subType":null,"widthUnits":"px"},{"caption":"Description","sortable":true,"dataIndex":"description","type":"java.lang.String","displayType":"Text","required":false,"readonly":false,"includeLists":true,"includeForms":true,"order":1001,"subType":null,"widthUnits":"px"},{"caption":"ApplicableToMe","sortable":true,"dataIndex":"applicableToMe","type":"java.lang.Boolean","displayType":"CheckBox","required":false,"readonly":false,"includeLists":true,"includeForms":true,"order":1002,"subType":null,"widthUnits":"px"},{"caption":"Fixed","sortable":true,"dataIndex":"fixed","type":"java.lang.Boolean","displayType":"CheckBox","required":false,"readonly":false,"includeLists":true,"includeForms":true,"order":1003,"subType":null,"widthUnits":"px"},{"caption":"Version","sortable":true,"dataIndex":"version","type":"java.lang.Integer","displayType":"Number","required":false,"readonly":false,"includeLists":true,"includeForms":true,"order":1004,"subType":null,"widthUnits":"px"},{"caption":"Id","sortable":true,"dataIndex":"issue.id","type":"java.lang.Long","displayType":"Number","required":true,"widthUnits":"px","includeLists":true,"includeForms":true,"order":2000},{"caption":"Sequence","sortable":true,"dataIndex":"issue.sequence","type":"java.lang.Integer","displayType":"Number","required":true,"widthUnits":"px","includeLists":true,"includeForms":true,"order":2001},{"caption":"Id","sortable":true,"dataIndex":"interview.id","type":"java.lang.Long","displayType":"Number","required":true,"readonly":true,"includeLists":true,"includeForms":true,"order":0,"subType":null},{"caption":"Scheduled","sortable":true,"dataIndex":"interview.scheduled","type":"java.util.Date","displayType":"Date","required":true,"readonly":false,"includeLists":true,"includeForms":true,"order":1,"subType":null},{"caption":"Notes","sortable":true,"dataIndex":"interview.notes","type":"java.lang.String","displayType":"Text","required":false,"readonly":false,"includeLists":true,"includeForms":true,"order":2,"subType":null},{"caption":"Done","sortable":true,"dataIndex":"interview.done","type":"java.lang.Boolean","displayType":"CheckBox","required":true,"readonly":false,"includeLists":true,"includeForms":true,"order":3,"subType":null},{"caption":"Version","sortable":true,"dataIndex":"interview.version","type":"java.lang.Integer","displayType":"Number","required":false,"readonly":false,"includeLists":true,"includeForms":true,"order":4,"subType":null}]}, {}]
	}],
	resourceattributeLiveVariable1: ["wm.LiveVariable", {"autoUpdate":false,"inFlightBehavior":"executeLast","orderBy":"asc: name","startUpdate":false,"type":"com.analystdb.data.ResourceAttribute"}, {}, {
		binding: ["wm.Binding", {}, {}, {
			wire: ["wm.Wire", {"expression":undefined,"source":"relatedEditor1.dataOutput","targetProperty":"filter.resource"}, {}]
		}],
		liveView: ["wm.LiveView", {"dataType":"com.analystdb.data.ResourceAttribute","view":[{"caption":"Id","sortable":true,"dataIndex":"id","type":"java.lang.Integer","displayType":"Number","required":true,"readonly":true,"includeLists":true,"includeForms":true,"order":0,"subType":null},{"caption":"Version","sortable":true,"dataIndex":"version","type":"java.lang.Integer","displayType":"Number","required":false,"readonly":false,"includeLists":true,"includeForms":true,"order":3,"subType":null},{"caption":"Name","sortable":true,"dataIndex":"name","type":"java.lang.String","displayType":"Text","required":true,"readonly":false,"includeLists":true,"includeForms":true,"order":1001,"subType":null,"widthUnits":"px"},{"caption":"Val","sortable":true,"dataIndex":"val","type":"java.lang.String","displayType":"Text","required":true,"readonly":false,"includeLists":true,"includeForms":true,"order":1002,"subType":null,"widthUnits":"px"}]}, {}]
	}],
	interviewFlowsVariable1: ["wm.LiveVariable", {"autoUpdate":false,"inFlightBehavior":"executeLast","orderBy":"asc: fromActor, asc: toActor, asc: name","startUpdate":false,"type":"com.analystdb.data.Flow"}, {"onSuccess":"interviewFlowsVariable1Success","onSuccess2":"docCatIssuesVariable1","onSuccess3":"docIssuesCountsVariable1","onSuccess4":"docIssuesVariable1"}, {
		binding: ["wm.Binding", {}, {}, {
			wire: ["wm.Wire", {"expression":undefined,"source":"interviewDojoGrid1.selectedItem.interview","targetProperty":"filter.interview"}, {}]
		}],
		liveView: ["wm.LiveView", {"dataType":"com.analystdb.data.Flow","related":["interview","documents"],"view":[{"caption":"Id","sortable":true,"dataIndex":"interview.id","type":"java.lang.Long","displayType":"Number","required":true,"readonly":true,"includeLists":true,"includeForms":true,"order":0,"subType":null},{"caption":"Id","sortable":true,"dataIndex":"documents.id","type":"java.lang.Integer","displayType":"Number","required":true,"readonly":true,"includeLists":true,"includeForms":true,"order":0,"subType":null},{"caption":"Scheduled","sortable":true,"dataIndex":"interview.scheduled","type":"java.util.Date","displayType":"Date","required":true,"readonly":false,"includeLists":true,"includeForms":true,"order":1,"subType":null},{"caption":"Document","sortable":true,"dataIndex":"documents.document","type":"java.lang.String","displayType":"Text","required":false,"readonly":false,"includeLists":true,"includeForms":true,"order":1,"subType":null},{"caption":"Notes","sortable":true,"dataIndex":"interview.notes","type":"java.lang.String","displayType":"Text","required":false,"readonly":false,"includeLists":true,"includeForms":true,"order":2,"subType":null},{"caption":"Done","sortable":true,"dataIndex":"interview.done","type":"java.lang.Boolean","displayType":"CheckBox","required":true,"readonly":false,"includeLists":true,"includeForms":true,"order":3,"subType":null},{"caption":"Version","sortable":true,"dataIndex":"interview.version","type":"java.lang.Integer","displayType":"Number","required":false,"readonly":false,"includeLists":true,"includeForms":true,"order":4,"subType":null},{"caption":"Version","sortable":true,"dataIndex":"version","type":"java.lang.Integer","displayType":"Number","required":false,"readonly":false,"includeLists":true,"includeForms":true,"order":5,"subType":null},{"caption":"Id","sortable":true,"dataIndex":"id","type":"java.lang.Long","displayType":"Number","required":true,"readonly":true,"includeLists":true,"includeForms":true,"order":1000,"subType":null,"widthUnits":"px"},{"caption":"FromActor","sortable":true,"dataIndex":"fromActor","type":"java.lang.String","displayType":"Text","required":false,"readonly":false,"includeLists":true,"includeForms":true,"order":1001,"subType":null,"widthUnits":"px"},{"caption":"ToActor","sortable":true,"dataIndex":"toActor","type":"java.lang.String","displayType":"Text","required":false,"readonly":false,"includeLists":true,"includeForms":true,"order":1002,"subType":null,"widthUnits":"px"},{"caption":"Name","sortable":true,"dataIndex":"name","type":"java.lang.String","displayType":"Text","required":true,"readonly":false,"includeLists":true,"includeForms":true,"order":1003,"subType":null,"widthUnits":"px"},{"caption":"Description","sortable":true,"dataIndex":"description","type":"java.lang.String","displayType":"Text","required":false,"widthUnits":"px","includeLists":true,"includeForms":true,"order":1004}]}, {}]
	}],
	otherFlowIssuesVariable1: ["wm.ServiceVariable", {"inFlightBehavior":"executeLast","operation":"otherFlowIssues","service":"analystDB"}, {}, {
		input: ["wm.ServiceInput", {"type":"otherFlowIssuesInputs"}, {}, {
			binding: ["wm.Binding", {}, {}, {
				wire: ["wm.Wire", {"expression":undefined,"source":"flowDojoGrid.selectedItem.id","targetProperty":"flow"}, {}],
				wire1: ["wm.Wire", {"expression":undefined,"source":"projectDojoGrid.selectedItem.id","targetProperty":"project"}, {}]
			}]
		}]
	}],
	interviewApproachesVariable1: ["wm.LiveVariable", {"autoUpdate":false,"inFlightBehavior":"executeLast","orderBy":"asc: name","startUpdate":false,"type":"com.analystdb.data.Approach"}, {"onSuccess":"interviewApproachesVariable1Success"}, {
		binding: ["wm.Binding", {}, {}, {
			wire: ["wm.Wire", {"expression":undefined,"source":"interviewDojoGrid1.selectedItem.interview","targetProperty":"filter.interview"}, {}]
		}],
		liveView: ["wm.LiveView", {"dataType":"com.analystdb.data.Approach","related":["interview"],"view":[{"caption":"Id","sortable":true,"dataIndex":"id","type":"java.lang.Long","displayType":"Number","required":true,"readonly":true,"includeLists":true,"includeForms":true,"order":1000,"subType":null,"widthUnits":"px"},{"caption":"Name","sortable":true,"dataIndex":"name","type":"java.lang.String","displayType":"Text","required":true,"readonly":false,"includeLists":true,"includeForms":true,"order":1001,"subType":null,"widthUnits":"px"},{"caption":"Description","sortable":true,"dataIndex":"description","type":"java.lang.String","displayType":"Text","required":false,"readonly":false,"includeLists":true,"includeForms":true,"order":1002,"subType":null,"widthUnits":"px"},{"caption":"Version","sortable":true,"dataIndex":"version","type":"java.lang.Integer","displayType":"Number","required":false,"readonly":false,"includeLists":true,"includeForms":true,"order":1003,"subType":null,"widthUnits":"px"},{"caption":"Start","sortable":true,"dataIndex":"start","type":"java.util.Date","displayType":"Date","required":false,"readonly":false,"includeLists":true,"includeForms":true,"order":1004,"subType":null,"widthUnits":"px"},{"caption":"End","sortable":true,"dataIndex":"end","type":"java.util.Date","displayType":"Date","required":false,"readonly":false,"includeLists":true,"includeForms":true,"order":1005,"subType":null,"widthUnits":"px"},{"caption":"Approved","sortable":true,"dataIndex":"approved","type":"java.lang.Boolean","displayType":"CheckBox","required":true,"readonly":false,"includeLists":true,"includeForms":true,"order":1006,"subType":null,"widthUnits":"px"},{"caption":"Cost","sortable":true,"dataIndex":"cost","type":"java.lang.Integer","displayType":"Number","required":false,"readonly":false,"includeLists":true,"includeForms":true,"order":1007,"subType":null,"widthUnits":"px"},{"caption":"Id","sortable":true,"dataIndex":"interview.id","type":"java.lang.Long","displayType":"Number","required":true,"readonly":true,"includeLists":true,"includeForms":true,"order":2000,"subType":null,"widthUnits":"px"},{"caption":"Scheduled","sortable":true,"dataIndex":"interview.scheduled","type":"java.util.Date","displayType":"Date","required":true,"readonly":false,"includeLists":true,"includeForms":true,"order":2001,"subType":null,"widthUnits":"px"},{"caption":"Notes","sortable":true,"dataIndex":"interview.notes","type":"java.lang.String","displayType":"Text","required":false,"readonly":false,"includeLists":true,"includeForms":true,"order":2002,"subType":null,"widthUnits":"px"},{"caption":"Done","sortable":true,"dataIndex":"interview.done","type":"java.lang.Boolean","displayType":"CheckBox","required":true,"readonly":false,"includeLists":true,"includeForms":true,"order":2003,"subType":null,"widthUnits":"px"},{"caption":"Version","sortable":true,"dataIndex":"interview.version","type":"java.lang.Integer","displayType":"Number","required":false,"readonly":false,"includeLists":true,"includeForms":true,"order":2004,"subType":null,"widthUnits":"px"}]}, {}]
	}],
	otherApproachIssuesVariable1: ["wm.ServiceVariable", {"inFlightBehavior":"executeLast","operation":"otherApproachIssues","service":"analystDB"}, {}, {
		input: ["wm.ServiceInput", {"type":"otherApproachIssuesInputs"}, {}, {
			binding: ["wm.Binding", {}, {}, {
				wire: ["wm.Wire", {"expression":undefined,"source":"approachDojoGrid.selectedItem.id","targetProperty":"approach"}, {}],
				wire1: ["wm.Wire", {"expression":undefined,"source":"projectDojoGrid.selectedItem.id","targetProperty":"project"}, {}]
			}]
		}]
	}],
	otherIssueApproachesVariable1: ["wm.ServiceVariable", {"inFlightBehavior":"executeLast","operation":"otherIssueApproaches","service":"analystDB"}, {}, {
		input: ["wm.ServiceInput", {"type":"otherIssueApproachesInputs"}, {}, {
			binding: ["wm.Binding", {}, {}, {
				wire: ["wm.Wire", {"expression":undefined,"source":"projectDojoGrid.selectedItem.id","targetProperty":"project"}, {}],
				wire1: ["wm.Wire", {"expression":undefined,"source":"dataGrid2.selectedItem.issue.id","targetProperty":"issue"}, {}]
			}]
		}]
	}],
	otherIssueFlowsVariable1: ["wm.ServiceVariable", {"inFlightBehavior":"executeLast","operation":"otherIssueFlows","service":"analystDB"}, {}, {
		input: ["wm.ServiceInput", {"type":"otherIssueFlowsInputs"}, {}, {
			binding: ["wm.Binding", {}, {}, {
				wire: ["wm.Wire", {"expression":undefined,"source":"projectDojoGrid.selectedItem.id","targetProperty":"project"}, {}],
				wire1: ["wm.Wire", {"expression":undefined,"source":"dataGrid2.selectedItem.id","targetProperty":"issueComment"}, {}]
			}]
		}]
	}],
	issueInsert: ["wm.LiveVariable", {"autoUpdate":false,"inFlightBehavior":"executeLast","operation":"insert","startUpdate":false,"type":"com.analystdb.data.Issue"}, {"onSuccess":"issueInsertSuccess","onSuccess1":"maxSequence","onSuccess2":"projectIssues","onSuccess3":"Edit_Issuecomment"}, {
		binding: ["wm.Binding", {}, {}, {
			wire: ["wm.Wire", {"expression":"0","targetProperty":"sourceData.id"}, {}],
			wire2: ["wm.Wire", {"expression":undefined,"source":"projectDojoGrid.selectedItem","targetProperty":"sourceData.project"}, {}],
			wire3: ["wm.Wire", {"expression":undefined,"source":"descriptionEditor2.dataValue","targetProperty":"sourceData.description"}, {}],
			wire4: ["wm.Wire", {"expression":"\"\"","targetProperty":"sourceData.name"}, {}],
			wire1: ["wm.Wire", {"expression":"${maxSequence.last} + 1","targetProperty":"sourceData.sequence"}, {}]
		}],
		liveView: ["wm.LiveView", {"dataType":"com.analystdb.data.Issue","related":["project"],"view":[{"caption":"Name","sortable":true,"dataIndex":"name","type":"java.lang.String","displayType":"Text","required":true,"readonly":false,"includeLists":true,"includeForms":true,"order":2,"subType":null},{"caption":"Version","sortable":true,"dataIndex":"version","type":"java.lang.Integer","displayType":"Number","required":false,"readonly":false,"includeLists":true,"includeForms":true,"order":4,"subType":null},{"caption":"Id","sortable":true,"dataIndex":"id","type":"java.lang.Long","displayType":"Number","required":true,"readonly":true,"includeLists":true,"includeForms":true,"order":1000,"subType":null,"widthUnits":"px"},{"caption":"Sequence","sortable":true,"dataIndex":"sequence","type":"java.lang.Integer","displayType":"Number","required":true,"readonly":false,"includeLists":true,"includeForms":true,"order":1001,"subType":null,"widthUnits":"px"},{"caption":"Description","sortable":true,"dataIndex":"description","type":"java.lang.String","displayType":"Text","required":false,"readonly":false,"includeLists":true,"includeForms":true,"order":1003,"subType":null,"widthUnits":"px"},{"caption":"Id","sortable":true,"dataIndex":"project.id","type":"java.lang.Long","displayType":"Number","required":true,"widthUnits":"px","includeLists":true,"includeForms":true,"order":2000}]}, {}]
	}],
	maxSequence: ["wm.ServiceVariable", {"inFlightBehavior":"executeLast","operation":"maxIssueSequence","service":"analystDB"}, {}, {
		input: ["wm.ServiceInput", {"type":"maxIssueSequenceInputs"}, {}, {
			binding: ["wm.Binding", {}, {}, {
				wire: ["wm.Wire", {"expression":undefined,"source":"projectDojoGrid.selectedItem.id","targetProperty":"project"}, {}]
			}]
		}]
	}],
	allIssues: ["wm.ServiceVariable", {"inFlightBehavior":"executeLast","operation":"allIssues","service":"analystDB"}, {}, {
		input: ["wm.ServiceInput", {"type":"allIssuesInputs"}, {}, {
			binding: ["wm.Binding", {}, {}, {
				wire: ["wm.Wire", {"expression":undefined,"source":"projectDojoGrid.selectedItem.id","targetProperty":"project"}, {}]
			}]
		}]
	}],
	showFlow: ["wm.NavigationCall", {}, {}, {
		input: ["wm.ServiceInput", {"type":"gotoLayerInputs"}, {}, {
			binding: ["wm.Binding", {}, {}, {
				wire: ["wm.Wire", {"expression":undefined,"source":"Edit_Flow","targetProperty":"layer"}, {}]
			}]
		}]
	}],
	showInterview: ["wm.NavigationCall", {}, {}, {
		input: ["wm.ServiceInput", {"type":"gotoLayerInputs"}, {}, {
			binding: ["wm.Binding", {}, {}, {
				wire: ["wm.Wire", {"expression":undefined,"source":"Edit_Interview","targetProperty":"layer"}, {}]
			}]
		}]
	}],
	showInterviews: ["wm.NavigationCall", {}, {}, {
		input: ["wm.ServiceInput", {"type":"gotoLayerInputs"}, {}, {
			binding: ["wm.Binding", {}, {}, {
				wire: ["wm.Wire", {"expression":undefined,"source":"Edit_Interviews","targetProperty":"layer"}, {}]
			}]
		}]
	}],
	showSummary: ["wm.NavigationCall", {}, {"onBeforeUpdate":"allDocsWithIssues","onBeforeUpdate1":"docCatIssuesVariable1","onBeforeUpdate10":"issueCategoriesVariable1","onBeforeUpdate11":"addressedIssues","onBeforeUpdate2":"flowsByProjectVariable1","onBeforeUpdate3":"approachIssueCountVariable1","onBeforeUpdate4":"issueLiveVariable1","onBeforeUpdate6":"docIssuesVariable1","onBeforeUpdate7":"allIssues","onBeforeUpdate9":"issuesByCategoryVariable1"}, {
		input: ["wm.ServiceInput", {"type":"gotoLayerInputs"}, {}, {
			binding: ["wm.Binding", {}, {}, {
				wire: ["wm.Wire", {"expression":undefined,"source":"Edit_Summary","targetProperty":"layer"}, {}]
			}]
		}]
	}],
	issuecommentLiveVariable1: ["wm.LiveVariable", {"autoUpdate":false,"startUpdate":false,"type":"com.analystdb.data.IssueComment"}, {}, {
		binding: ["wm.Binding", {}, {}, {
			wire: ["wm.Wire", {"expression":undefined,"source":"issueForm.dataOutput","targetProperty":"filter.issue"}, {}]
		}],
		liveView: ["wm.LiveView", {"dataType":"com.analystdb.data.IssueComment","related":["interview","interview.resource"],"view":[{"caption":"Version","sortable":true,"dataIndex":"version","type":"java.lang.Integer","displayType":"Number","required":false,"readonly":false,"includeLists":true,"includeForms":true,"order":4,"subType":null},{"caption":"Id","sortable":true,"dataIndex":"id","type":"java.lang.Long","displayType":"Number","required":true,"readonly":true,"includeLists":true,"includeForms":true,"order":1000,"subType":null,"widthUnits":"px"},{"caption":"Description","sortable":true,"dataIndex":"description","type":"java.lang.String","displayType":"Text","required":false,"readonly":false,"includeLists":true,"includeForms":true,"order":1001,"subType":null,"widthUnits":"px"},{"caption":"ApplicableToMe","sortable":true,"dataIndex":"applicableToMe","type":"java.lang.Boolean","displayType":"CheckBox","required":false,"readonly":false,"includeLists":true,"includeForms":true,"order":1002,"subType":null,"widthUnits":"px"},{"caption":"Fixed","sortable":true,"dataIndex":"fixed","type":"java.lang.Boolean","displayType":"CheckBox","required":false,"readonly":false,"includeLists":true,"includeForms":true,"order":1003,"subType":null,"widthUnits":"px"},{"caption":"Scheduled","sortable":true,"dataIndex":"interview.scheduled","type":"java.util.Date","displayType":"Date","required":true,"widthUnits":"px","includeLists":true,"includeForms":true,"order":2001},{"caption":"Name","sortable":true,"dataIndex":"interview.resource.name","type":"java.lang.String","displayType":"Text","required":true,"widthUnits":"px","includeLists":true,"includeForms":true,"order":3001}]}, {}]
	}],
	issueCategoriesVariable1: ["wm.LiveVariable", {"autoUpdate":false,"inFlightBehavior":"executeLast","startUpdate":false,"type":"com.analystdb.data.IssueIssueCategory"}, {"onSuccess":"issueCategoryCount"}, {
		binding: ["wm.Binding", {}, {}, {
			wire: ["wm.Wire", {"expression":undefined,"source":"issueForm.dataOutput","targetProperty":"filter.issue"}, {}]
		}],
		liveView: ["wm.LiveView", {"dataType":"com.analystdb.data.IssueIssueCategory","related":["id","issueCategory"],"view":[{"caption":"IssueId","sortable":true,"dataIndex":"id.issueId","type":"java.lang.Long","displayType":"Number","required":true,"readonly":true,"includeLists":true,"includeForms":true,"order":5000,"subType":null,"widthUnits":"px"},{"caption":"IssueCategoryId","sortable":true,"dataIndex":"id.issueCategoryId","type":"java.lang.Long","displayType":"Number","required":true,"readonly":true,"includeLists":true,"includeForms":true,"order":5001,"subType":null,"widthUnits":"px"},{"caption":"Name","sortable":true,"dataIndex":"issueCategory.name","type":"java.lang.String","displayType":"Text","required":true,"widthUnits":"px","includeLists":true,"includeForms":true,"order":6001}]}, {}]
	}],
	showIssue: ["wm.NavigationCall", {}, {"onBeforeUpdate":"issueCategoriesVariable1","onBeforeUpdate2":"issueFormApproaches","onBeforeUpdate3":"issuecommentLiveVariable1","onBeforeUpdate4":"issueFlows"}, {
		input: ["wm.ServiceInput", {"type":"gotoLayerInputs"}, {}, {
			binding: ["wm.Binding", {}, {}, {
				wire: ["wm.Wire", {"expression":undefined,"source":"Edit_Issue2","targetProperty":"layer"}, {}]
			}]
		}]
	}],
	showComment: ["wm.NavigationCall", {}, {"onBeforeUpdate":"issueApproaches","onBeforeUpdate1":"issueCommentFlowsVariable","onBeforeUpdate2":"issueAttributesVariable","onBeforeUpdate3":"issuecommentLiveForm1.beginDataUpdate"}, {
		input: ["wm.ServiceInput", {"type":"gotoLayerInputs"}, {}, {
			binding: ["wm.Binding", {}, {}, {
				wire: ["wm.Wire", {"expression":undefined,"source":"Edit_Issuecomment","targetProperty":"layer"}, {}]
			}]
		}]
	}],
	issueFormApproaches: ["wm.LiveVariable", {"inFlightBehavior":"executeLast","type":"com.analystdb.data.IssueApproach"}, {}, {
		binding: ["wm.Binding", {}, {}, {
			wire: ["wm.Wire", {"expression":undefined,"source":"issueForm.dataOutput","targetProperty":"filter.issue"}, {}]
		}],
		liveView: ["wm.LiveView", {"dataType":"com.analystdb.data.IssueApproach","related":["id","approach","approach.interview","approach.interview.resource"],"view":[{"caption":"IssueId","sortable":true,"dataIndex":"id.issueId","type":"java.lang.Long","displayType":"Number","required":true,"readonly":true,"includeLists":true,"includeForms":true,"order":8000,"subType":null,"widthUnits":"px"},{"caption":"ApproachId","sortable":true,"dataIndex":"id.approachId","type":"java.lang.Long","displayType":"Number","required":true,"readonly":true,"includeLists":true,"includeForms":true,"order":8001,"subType":null,"widthUnits":"px"},{"caption":"Name","sortable":true,"dataIndex":"approach.name","type":"java.lang.String","displayType":"Text","required":true,"widthUnits":"px","includeLists":true,"includeForms":true,"order":9001},{"caption":"Description","sortable":true,"dataIndex":"approach.description","type":"java.lang.String","displayType":"Text","required":false,"widthUnits":"px","includeLists":true,"includeForms":true,"order":9002},{"caption":"Id","sortable":true,"dataIndex":"approach.interview.id","type":"java.lang.Long","displayType":"Number","required":true,"widthUnits":"px","includeLists":true,"includeForms":true,"order":10000},{"caption":"Id","sortable":true,"dataIndex":"approach.interview.resource.id","type":"java.lang.Long","displayType":"Number","required":true,"widthUnits":"px","includeLists":true,"includeForms":true,"order":11000}]}, {}]
	}],
	projectIssues: ["wm.LiveVariable", {"autoUpdate":false,"inFlightBehavior":"executeLast","startUpdate":false,"type":"com.analystdb.data.Issue"}, {}, {
		binding: ["wm.Binding", {}, {}, {
			wire: ["wm.Wire", {"expression":undefined,"source":"projectDojoGrid.selectedItem","targetProperty":"filter.project"}, {}]
		}],
		liveView: ["wm.LiveView", {"dataType":"com.analystdb.data.Issue","related":["project"],"view":[{"caption":"Name","sortable":true,"dataIndex":"name","type":"java.lang.String","displayType":"Text","required":true,"readonly":false,"includeLists":true,"includeForms":true,"order":2,"subType":null},{"caption":"Version","sortable":true,"dataIndex":"version","type":"java.lang.Integer","displayType":"Number","required":false,"readonly":false,"includeLists":true,"includeForms":true,"order":4,"subType":null},{"caption":"Id","sortable":true,"dataIndex":"id","type":"java.lang.Long","displayType":"Number","required":true,"readonly":true,"includeLists":true,"includeForms":true,"order":1000,"subType":null,"widthUnits":"px"},{"caption":"Sequence","sortable":true,"dataIndex":"sequence","type":"java.lang.Integer","displayType":"Number","required":true,"readonly":false,"includeLists":true,"includeForms":true,"order":1001,"subType":null,"widthUnits":"px"},{"caption":"Description","sortable":true,"dataIndex":"description","type":"java.lang.String","displayType":"Text","required":false,"readonly":false,"includeLists":true,"includeForms":true,"order":1003,"subType":null,"widthUnits":"px"},{"caption":"Id","sortable":true,"dataIndex":"project.id","type":"java.lang.Long","displayType":"Number","required":true,"widthUnits":"px","includeLists":true,"includeForms":true,"order":2000}]}, {}]
	}],
	otherCategoriesVariable1: ["wm.ServiceVariable", {"inFlightBehavior":"executeLast","operation":"otherCategories","service":"analystDB"}, {}, {
		input: ["wm.ServiceInput", {"type":"otherCategoriesInputs"}, {}, {
			binding: ["wm.Binding", {}, {}, {
				wire: ["wm.Wire", {"expression":undefined,"source":"projectDojoGrid.selectedItem.id","targetProperty":"project"}, {}],
				wire1: ["wm.Wire", {"expression":undefined,"source":"issueForm.dataOutput.id","targetProperty":"issue"}, {}]
			}]
		}]
	}],
	issuesByCategoryVariable1: ["wm.ServiceVariable", {"inFlightBehavior":"executeLast","operation":"issuesByCategory","service":"analystDB"}, {}, {
		input: ["wm.ServiceInput", {"type":"issuesByCategoryInputs"}, {}, {
			binding: ["wm.Binding", {}, {}, {
				wire: ["wm.Wire", {"expression":undefined,"source":"projectDojoGrid.selectedItem.id","targetProperty":"project"}, {}],
				wire1: ["wm.Wire", {"expression":"if (${issueCategoryGrid1.emptySelection}) { -1; }\nelse {${issueCategoryGrid1.selectedItem.category.id};}","targetProperty":"category"}, {}]
			}]
		}]
	}],
	allDocsWithIssues: ["wm.ServiceVariable", {"inFlightBehavior":"executeLast","operation":"allDocsWithIssues","service":"analystDB"}, {}, {
		input: ["wm.ServiceInput", {"type":"allDocsWithIssuesInputs"}, {}, {
			binding: ["wm.Binding", {}, {}, {
				wire: ["wm.Wire", {"expression":undefined,"source":"projectDojoGrid.selectedItem.id","targetProperty":"project"}, {}]
			}]
		}]
	}],
	issueCommentFlowsVariable: ["wm.LiveVariable", {"autoUpdate":false,"inFlightBehavior":"executeLast","startUpdate":false,"type":"com.analystdb.data.IssueCommentFlows"}, {}, {
		binding: ["wm.Binding", {}, {}, {
			wire: ["wm.Wire", {"expression":undefined,"source":"dataGrid2.selectedItem","targetProperty":"filter.issueComment"}, {}]
		}],
		liveView: ["wm.LiveView", {"dataType":"com.analystdb.data.IssueCommentFlows","related":["flow","flow.interview"],"view":[{"caption":"FromActor","sortable":true,"dataIndex":"flow.fromActor","type":"java.lang.String","displayType":"Text","required":false,"widthUnits":"px","includeLists":true,"includeForms":true,"order":2001},{"caption":"ToActor","sortable":true,"dataIndex":"flow.toActor","type":"java.lang.String","displayType":"Text","required":false,"widthUnits":"px","includeLists":true,"includeForms":true,"order":2002},{"caption":"Name","sortable":true,"dataIndex":"flow.name","type":"java.lang.String","displayType":"Text","required":true,"widthUnits":"px","includeLists":true,"includeForms":true,"order":2003},{"caption":"Id","sortable":true,"dataIndex":"flow.interview.id","type":"java.lang.Long","displayType":"Number","required":true,"widthUnits":"px","includeLists":true,"includeForms":true,"order":3000}]}, {}]
	}],
	issueFlows: ["wm.ServiceVariable", {"inFlightBehavior":"executeLast","operation":"issueFlows","service":"analystDB"}, {}, {
		input: ["wm.ServiceInput", {"type":"issueFlowsInputs"}, {}, {
			binding: ["wm.Binding", {}, {}, {
				wire: ["wm.Wire", {"expression":undefined,"source":"issueForm.dataOutput.id","targetProperty":"issue"}, {}]
			}]
		}]
	}],
	allIssueCategories: ["wm.LiveVariable", {"inFlightBehavior":"executeLast","orderBy":"asc: name","type":"com.analystdb.data.IssueCategory"}, {"onSuccess":"otherCategoriesVariable1","onSuccess1":"issueCategoryCount"}, {
		binding: ["wm.Binding", {}, {}, {
			wire: ["wm.Wire", {"expression":undefined,"source":"projectDojoGrid.selectedItem","targetProperty":"filter.project"}, {}]
		}],
		liveView: ["wm.LiveView", {"dataType":"com.analystdb.data.IssueCategory","view":[{"caption":"Id","sortable":true,"dataIndex":"id","type":"java.lang.Long","displayType":"Number","required":true,"readonly":true,"includeLists":true,"includeForms":true,"order":0,"subType":null},{"caption":"Name","sortable":true,"dataIndex":"name","type":"java.lang.String","displayType":"Text","required":true,"readonly":false,"includeLists":true,"includeForms":true,"order":1,"subType":null},{"caption":"Version","sortable":true,"dataIndex":"version","type":"java.lang.Integer","displayType":"Number","required":false,"readonly":false,"includeLists":true,"includeForms":true,"order":2,"subType":null}]}, {}]
	}],
	addressedIssues: ["wm.ServiceVariable", {"inFlightBehavior":"executeLast","operation":"addressedIssues","service":"analystDB"}, {}, {
		input: ["wm.ServiceInput", {"type":"addressedIssuesInputs"}, {}, {
			binding: ["wm.Binding", {}, {}, {
				wire: ["wm.Wire", {"expression":undefined,"source":"projectDojoGrid.selectedItem.id","targetProperty":"project"}, {}]
			}]
		}]
	}],
	issueApproaches: ["wm.ServiceVariable", {"inFlightBehavior":"executeLast","operation":"issueApproaches","service":"analystDB"}, {}, {
		input: ["wm.ServiceInput", {"type":"issueApproachesInputs"}, {}, {
			binding: ["wm.Binding", {}, {}, {
				wire: ["wm.Wire", {"expression":undefined,"source":"dataGrid2.selectedItem.issue.id","targetProperty":"issue"}, {}]
			}]
		}]
	}],
	showApproach: ["wm.NavigationCall", {}, {}, {
		input: ["wm.ServiceInput", {"type":"gotoLayerInputs"}, {}, {
			binding: ["wm.Binding", {}, {}, {
				wire: ["wm.Wire", {"expression":undefined,"source":"Edit_Approach","targetProperty":"layer"}, {}]
			}]
		}]
	}],
	documentsLiveVariable2: ["wm.LiveVariable", {"autoUpdate":false,"inFlightBehavior":undefined,"orderBy":"asc: documentCategory.sequence, asc: document","type":"com.analystdb.data.Documents"}, {}, {
		liveView: ["wm.LiveView", {"dataType":"com.analystdb.data.Documents","related":["documentCategory"],"view":[{"caption":"TenantId","sortable":true,"dataIndex":"tenantId","type":"java.lang.Integer","displayType":"Number","required":true,"readonly":false,"includeLists":true,"includeForms":true,"order":2,"subType":null},{"caption":"Id","sortable":true,"dataIndex":"id","type":"java.lang.Integer","displayType":"Number","required":true,"readonly":true,"includeLists":true,"includeForms":true,"order":2000,"subType":null,"widthUnits":"px"},{"caption":"Document","sortable":true,"dataIndex":"document","type":"java.lang.String","displayType":"Text","required":false,"readonly":false,"includeLists":true,"includeForms":true,"order":2001,"subType":null,"widthUnits":"px"},{"caption":"Id","sortable":true,"dataIndex":"documentCategory.id","type":"java.lang.Integer","displayType":"Number","required":true,"widthUnits":"px","includeLists":true,"includeForms":true,"order":3000},{"caption":"Name","sortable":true,"dataIndex":"documentCategory.name","type":"java.lang.String","displayType":"Text","required":true,"widthUnits":"px","includeLists":true,"includeForms":true,"order":3001}]}, {}]
	}],
	tenantInfo: ["wm.LiveVariable", {"autoUpdate":false,"designMaxResults":1,"inFlightBehavior":undefined,"maxResults":1,"type":"com.analystdb.data.Tenant"}, {}, {
		liveView: ["wm.LiveView", {"dataType":"com.analystdb.data.Tenant","view":[{"caption":"TenantId","sortable":true,"dataIndex":"tenantId","type":"java.lang.Integer","displayType":"Number","required":true,"readonly":true,"includeLists":true,"includeForms":true,"order":0,"subType":null},{"caption":"Name","sortable":true,"dataIndex":"name","type":"java.lang.String","displayType":"Text","required":true,"readonly":false,"includeLists":true,"includeForms":true,"order":5001,"subType":null,"widthUnits":"px"},{"caption":"Logo","sortable":true,"dataIndex":"logo","type":"java.lang.String","displayType":"Text","required":false,"readonly":false,"includeLists":true,"includeForms":true,"order":5002,"subType":null,"widthUnits":"px"},{"caption":"Disabled","sortable":true,"dataIndex":"disabled","type":"java.lang.Boolean","displayType":"CheckBox","required":false,"readonly":false,"includeLists":true,"includeForms":true,"order":5003,"subType":null,"widthUnits":"px"},{"caption":"Expires","sortable":true,"dataIndex":"expires","type":"java.util.Date","displayType":"Date","required":false,"readonly":false,"includeLists":true,"includeForms":true,"order":5004,"subType":null,"widthUnits":"px"}]}, {}]
	}],
	EditApproachIssues: ["wm.DesignableDialog", {"buttonBarId":"buttonBar","containerWidgetId":"containerWidget","title":"Associate issues with this approach"}, {"onShow":"selectedGrid.deselectAll","onShow1":"availableGrid.deselectAll","onShow2":"addButton.disable","onShow3":"removeButton.disable"}, {
		containerWidget: ["wm.Container", {"_classes":{"domNode":["wmdialogcontainer","MainContent"]},"autoScroll":true,"height":"100%","horizontalAlign":"left","layoutKind":"left-to-right","padding":"5","verticalAlign":"top","width":"100%"}, {}, {
			selectedGrid: ["wm.DojoGrid", {"columns":[{"show":true,"field":"issue.sequence","title":"Issue","width":"50px","align":"center","formatFunc":"","editorProps":{"restrictValues":true},"expression":"\"I-\" + ${issue.sequence}","mobileColumn":false},{"show":true,"field":"issue.description","title":"Description","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"approach.name","title":"Name","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"approach.description","title":"Description","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"PHONE COLUMN","title":"-","width":"100%","align":"left","expression":"\"<div class='MobileRowTitle'>Description: \" + ${issue.description} + \"</div>\"\n","mobileColumn":true},{"show":false,"field":"approach.interview.id","title":"Approach.interview.id","width":"80px","displayType":"Number","align":"right","formatFunc":""}],"dsType":"com.analystdb.data.IssueApproach","height":"100%","localizationStructure":{},"margin":"4","minDesktopHeight":60,"minWidth":100,"singleClickEdit":true}, {"onDeselect":"removeButton.disable","onSelect":"removeButton.enable"}, {
				binding: ["wm.Binding", {}, {}, {
					wire: ["wm.Wire", {"expression":undefined,"source":"approachIssuesVariable","targetProperty":"dataSet"}, {}]
				}]
			}],
			panel2: ["wm.Panel", {"fitToContentWidth":true,"height":"100%","horizontalAlign":"left","verticalAlign":"middle","width":"82px"}, {}, {
				addButton: ["wm.Button", {"caption":"Add","imageIndex":3,"imageList":"app.silkIconList","margin":"4"}, {"onclick":"addButtonClick"}],
				removeButton: ["wm.Button", {"caption":"Remove","imageIndex":5,"imageList":"app.silkIconList","margin":"4"}, {"onclick":"removeButtonClick","onclick1":"otherApproachIssuesVariable1"}]
			}],
			availableGrid: ["wm.DojoGrid", {"columns":[{"show":false,"field":"id","title":"Available","width":"50px","align":"center","formatFunc":"","editorProps":{"restrictValues":true},"expression":"\"I-\" + ${id}","mobileColumn":false},{"show":true,"field":"sequence","title":"Issue","width":"80px","align":"right","formatFunc":"","constraints":null,"editorProps":null,"expression":"\"I-\" + ${sequence}","mobileColumn":false},{"show":true,"field":"description","title":"Description","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"version","title":"Version","width":"80px","align":"right","formatFunc":"","mobileColumn":false},{"show":false,"field":"PHONE COLUMN","title":"-","width":"100%","align":"left","expression":"\"<div class='MobileRowTitle'>Description: \" + ${description} + \"</div>\"\n","mobileColumn":true},{"show":false,"field":"name","title":"Name","width":"100%","align":"left","formatFunc":"","mobileColumn":false}],"height":"100%","margin":"4","minDesktopHeight":60,"minWidth":100,"primaryKeyFields":["id"],"singleClickEdit":true}, {"onDeselect":"addButton.disable","onSelect":"addButton.enable"}, {
				binding: ["wm.Binding", {}, {}, {
					wire: ["wm.Wire", {"expression":undefined,"source":"otherApproachIssuesVariable1","targetProperty":"dataSet"}, {}]
				}]
			}]
		}],
		buttonBar: ["wm.Panel", {"_classes":{"domNode":["dialogfooter"]},"border":"1","desktopHeight":"32px","enableTouchHeight":true,"height":"32px","horizontalAlign":"right","layoutKind":"left-to-right","mobileHeight":"40px","verticalAlign":"top","width":"100%"}, {}, {
			doneButton: ["wm.Button", {"caption":"Done","imageIndex":0,"imageList":"app.silkIconList","margin":"4"}, {"onclick":"EditApproachIssues.hide"}]
		}]
	}],
	EditIssueApproaches: ["wm.DesignableDialog", {"buttonBarId":"buttonBar","containerWidgetId":"containerWidget","title":"Associate approaches with this issue"}, {"onShow":"selectedGrid1.deselectAll","onShow1":"availableGrid1.deselectAll","onShow2":"addButton1.disable","onShow3":"removeButton1.disable"}, {
		containerWidget1: ["wm.Container", {"_classes":{"domNode":["wmdialogcontainer","MainContent"]},"autoScroll":true,"height":"100%","horizontalAlign":"left","layoutKind":"left-to-right","padding":"5","verticalAlign":"top","width":"100%"}, {}, {
			selectedGrid1: ["wm.DojoGrid", {"columns":[{"show":false,"field":"PHONE COLUMN","title":"-","width":"100%","align":"left","expression":"\"<div class='MobileRowTitle'>Selected: \" + ${approach.name} + \"</div>\"\n","mobileColumn":true},{"show":false,"field":"approach.id","title":"Approach.id","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"approach.approved","title":"Approach.approved","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"approach.interview.id","title":"Approach.interview.id","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"approach.interview.scheduled","title":"Approach.interview.scheduled","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"approach.interview.done","title":"Approach.interview.done","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"approach.interview.resource.id","title":"Approach.interview.resource.id","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"approach.interview.resource.project.id","title":"Approach.interview.resource.project.id","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"approach.interview.resource.project.description","title":"Approach.interview.resource.project.description","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"approach.interview.resource.project.name","title":"Approach.interview.resource.project.name","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"approach.interview.resource.project.version","title":"Approach.interview.resource.project.version","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"approach.interview.resource.description","title":"Approach.interview.resource.description","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"approach.interview.resource.name","title":"Approach.interview.resource.name","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"approach.interview.resource.version","title":"Approach.interview.resource.version","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"approach.interview.notes","title":"Approach.interview.notes","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"approach.interview.version","title":"Approach.interview.version","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"approach.start","title":"Approach.start","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"approach.description","title":"Approach.description","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":true,"field":"approach.name","title":"Selected","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"approach.cost","title":"Approach.cost","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"approach.end","title":"Approach.end","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"approach.version","title":"Approach.version","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"approach.interview.resource.project.tenantId","title":"Approach.interview.resource.project.tenantId","width":"100%","displayType":"Java.lang.Integer","align":"left","formatFunc":""}],"height":"100%","margin":"4","minDesktopHeight":60,"singleClickEdit":true}, {"onDeselect":"removeButton1.disable","onSelect":"removeButton1.enable"}, {
				binding: ["wm.Binding", {}, {}, {
					wire: ["wm.Wire", {"expression":undefined,"source":"issueApproaches","targetProperty":"dataSet"}, {}]
				}]
			}],
			panel3: ["wm.Panel", {"fitToContentWidth":true,"height":"100%","horizontalAlign":"left","verticalAlign":"middle","width":"82px"}, {}, {
				addButton1: ["wm.Button", {"caption":"Add","imageIndex":3,"imageList":"app.silkIconList","margin":"4"}, {"onclick":"addButton1Click"}],
				removeButton1: ["wm.Button", {"caption":"Remove","imageIndex":5,"imageList":"app.silkIconList","margin":"4"}, {"onclick":"removeButton1Click"}]
			}],
			availableGrid1: ["wm.DojoGrid", {"columns":[{"show":false,"field":"id","title":"Id","width":"80px","align":"right","formatFunc":"","mobileColumn":false},{"show":true,"field":"name","title":"Available","width":"100%","align":"left","formatFunc":"","editorProps":{"restrictValues":true},"mobileColumn":false},{"show":false,"field":"description","title":"Description","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"version","title":"Version","width":"80px","align":"right","formatFunc":"","mobileColumn":false},{"show":false,"field":"PHONE COLUMN","title":"-","width":"100%","align":"left","expression":"\"<div class='MobileRowTitle'>Available: \" + ${name} + \"</div>\"\n","mobileColumn":true},{"show":false,"field":"start","title":"Start","width":"80px","displayType":"Date","align":"left","formatFunc":"wm_date_formatter"},{"show":false,"field":"end","title":"End","width":"80px","displayType":"Date","align":"left","formatFunc":"wm_date_formatter"},{"show":false,"field":"approved","title":"Approved","width":"100%","displayType":"CheckBox","align":"left","formatFunc":""},{"show":false,"field":"cost","title":"Cost","width":"80px","displayType":"Number","align":"right","formatFunc":""}],"height":"100%","margin":"4","minDesktopHeight":60,"primaryKeyFields":["id"],"singleClickEdit":true}, {"onDeselect":"addButton1.disable","onSelect":"addButton1.enable"}, {
				binding: ["wm.Binding", {}, {}, {
					wire: ["wm.Wire", {"expression":undefined,"source":"otherIssueApproachesVariable1","targetProperty":"dataSet"}, {}]
				}]
			}]
		}],
		buttonBar1: ["wm.Panel", {"_classes":{"domNode":["dialogfooter"]},"border":"1","desktopHeight":"32px","enableTouchHeight":true,"height":"32px","horizontalAlign":"right","layoutKind":"left-to-right","mobileHeight":"40px","verticalAlign":"top","width":"100%"}, {}, {
			doneButton1: ["wm.Button", {"caption":"Done","imageIndex":0,"imageList":"app.silkIconList","margin":"4"}, {"onclick":"EditIssueApproaches.hide"}]
		}]
	}],
	EditFlowIssues: ["wm.DesignableDialog", {"buttonBarId":"buttonBar","containerWidgetId":"containerWidget","title":"Associate issues with this flow"}, {"onShow":"selectedGrid2.deselectAll","onShow1":"availableGrid2.deselectAll"}, {
		containerWidget2: ["wm.Container", {"_classes":{"domNode":["wmdialogcontainer","MainContent"]},"autoScroll":true,"height":"100%","horizontalAlign":"left","layoutKind":"left-to-right","padding":"5","verticalAlign":"top","width":"100%"}, {}, {
			selectedGrid2: ["wm.DojoGrid", {"columns":[{"show":false,"field":"PHONE COLUMN","title":"-","width":"100%","align":"left","expression":"\"<div class='MobileRowTitle'>Description: \" + ${issueComment.description} + \"</div>\"\n","mobileColumn":true},{"show":true,"field":"issueComment.issue.sequence","title":"Issue","width":"50px","align":"center","formatFunc":"","editorProps":{"restrictValues":true},"expression":"\"I-\" + ${issueComment.issue.sequence}","mobileColumn":false},{"show":true,"field":"issueComment.description","title":"Description","width":"100%","align":"left","formatFunc":"","editorProps":{"restrictValues":true},"mobileColumn":false},{"show":false,"field":"issueComment.issue.description","title":"IssueComment.issue.description","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"issueComment.interview.id","title":"IssueComment.interview.id","width":"80px","displayType":"Number","align":"right","formatFunc":""}],"height":"100%","margin":"4","minDesktopHeight":60}, {"onDeselect":"removeButton2.disable","onSelect":"removeButton2.enable"}, {
				binding: ["wm.Binding", {}, {}, {
					wire: ["wm.Wire", {"expression":undefined,"source":"flowIssuesVariable","targetProperty":"dataSet"}, {}]
				}]
			}],
			panel4: ["wm.Panel", {"fitToContentWidth":true,"height":"100%","horizontalAlign":"left","verticalAlign":"middle","width":"82px"}, {}, {
				addButton2: ["wm.Button", {"caption":"Add","disabled":true,"imageIndex":3,"imageList":"app.silkIconList","margin":"4"}, {"onclick":"addButton2Click"}],
				removeButton2: ["wm.Button", {"caption":"Remove","disabled":true,"imageIndex":5,"imageList":"app.silkIconList","margin":"4"}, {"onclick":"removeButton2Click","onclick1":"otherFlowIssuesVariable1"}]
			}],
			availableGrid2: ["wm.DojoGrid", {"columns":[{"show":false,"field":"PHONE COLUMN","title":"-","width":"100%","align":"left","expression":"\"<div class='MobileRowTitle'>Description: \" + ${description} + \"</div>\"\n","mobileColumn":true},{"show":false,"field":"id","title":"Id","width":"80px","align":"right","formatFunc":"","mobileColumn":false},{"show":true,"field":"issue.sequence","title":"Available","width":"50px","align":"center","constraints":null,"editorProps":{"restrictValues":true},"expression":"\"I-\" + ${issue.sequence}","isCustomField":true},{"show":true,"field":"description","title":"Description","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"applicableToMe","title":"ApplicableToMe","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"fixed","title":"Fixed","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"version","title":"Version","width":"80px","align":"right","formatFunc":"","mobileColumn":false}],"height":"100%","margin":"4","minDesktopHeight":60,"primaryKeyFields":["id"]}, {"onDeselect":"addButton2.disable","onSelect":"addButton2.enable"}, {
				binding: ["wm.Binding", {}, {}, {
					wire: ["wm.Wire", {"expression":undefined,"source":"otherFlowIssuesVariable1","targetProperty":"dataSet"}, {}]
				}]
			}]
		}],
		buttonBar2: ["wm.Panel", {"_classes":{"domNode":["dialogfooter"]},"border":"1","desktopHeight":"32px","enableTouchHeight":true,"height":"32px","horizontalAlign":"right","layoutKind":"left-to-right","mobileHeight":"40px","verticalAlign":"top","width":"100%"}, {}, {
			doneButton2: ["wm.Button", {"caption":"Done","imageIndex":0,"imageList":"app.silkIconList","margin":"4"}, {"onclick":"EditFlowIssues.hide"}]
		}]
	}],
	EditIssueFlows: ["wm.DesignableDialog", {"buttonBarId":"buttonBar","containerWidgetId":"containerWidget","title":"Associate flows with this issue","width":"700px"}, {"onShow":"selectedGrid3.deselectAll","onShow1":"availableGrid3.deselectAll","onShow2":"addButton3.disable","onShow3":"removeButton3.disable"}, {
		containerWidget3: ["wm.Container", {"_classes":{"domNode":["wmdialogcontainer","MainContent"]},"autoScroll":true,"height":"100%","horizontalAlign":"left","layoutKind":"left-to-right","padding":"5","verticalAlign":"top","width":"100%"}, {}, {
			selectedGrid3: ["wm.DojoGrid", {"columns":[{"show":true,"field":"flow.fromActor","title":"From","width":"100%","align":"left","formatFunc":"","editorProps":{"restrictValues":true},"mobileColumn":false},{"show":true,"field":"flow.toActor","title":"To","width":"100%","align":"left","formatFunc":"","editorProps":{"restrictValues":true},"mobileColumn":false},{"show":true,"field":"flow.name","title":"Name","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"PHONE COLUMN","title":"-","width":"100%","align":"left","expression":"\"<div class='MobileRowTitle'>From: \" + ${flow.fromActor} + \"</div>\"\n+ \"<div class='MobileRow'>To: \" + ${flow.toActor} + \"</div>\"\n+ \"<div class='MobileRow'>Name: \" + ${flow.name} + \"</div>\"\n","mobileColumn":true},{"show":false,"field":"flow.interview.id","title":"Flow.interview.id","width":"80px","displayType":"Number","align":"right","formatFunc":""}],"height":"100%","margin":"4","minDesktopHeight":60,"singleClickEdit":true}, {"onDeselect":"removeButton3.disable","onSelect":"removeButton3.enable"}, {
				binding: ["wm.Binding", {}, {}, {
					wire: ["wm.Wire", {"expression":undefined,"source":"issueCommentFlowsVariable","targetProperty":"dataSet"}, {}]
				}]
			}],
			panel5: ["wm.Panel", {"fitToContentWidth":true,"height":"100%","horizontalAlign":"left","verticalAlign":"middle","width":"82px"}, {}, {
				addButton3: ["wm.Button", {"caption":"Add","imageIndex":3,"imageList":"app.silkIconList","margin":"4"}, {"onclick":"addButton3Click"}],
				removeButton3: ["wm.Button", {"caption":"Remove","imageIndex":5,"imageList":"app.silkIconList","margin":"4"}, {"onclick":"removeButton3Click","onclick1":"otherIssueFlowsVariable1"}]
			}],
			availableGrid3: ["wm.DojoGrid", {"columns":[{"show":false,"field":"id","title":"Id","width":"80px","align":"right","formatFunc":"","mobileColumn":false},{"show":true,"field":"fromActor","title":"From","width":"100%","align":"left","formatFunc":"","editorProps":{"restrictValues":true},"mobileColumn":false},{"show":true,"field":"toActor","title":"To","width":"100%","align":"left","formatFunc":"","editorProps":{"restrictValues":true},"mobileColumn":false},{"show":true,"field":"name","title":"Name","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"description","title":"Description","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"version","title":"Version","width":"80px","align":"right","formatFunc":"","mobileColumn":false},{"show":false,"field":"PHONE COLUMN","title":"-","width":"100%","align":"left","expression":"\"<div class='MobileRowTitle'>From: \" + ${fromActor} + \"</div>\"\n+ \"<div class='MobileRow'>To: \" + ${toActor} + \"</div>\"\n+ \"<div class='MobileRow'>Name: \" + ${name} + \"</div>\"\n","mobileColumn":true}],"height":"100%","margin":"4","minDesktopHeight":60,"primaryKeyFields":["id"]}, {"onDeselect":"addButton3.disable","onSelect":"addButton3.enable"}, {
				binding: ["wm.Binding", {}, {}, {
					wire: ["wm.Wire", {"expression":undefined,"source":"otherIssueFlowsVariable1","targetProperty":"dataSet"}, {}]
				}]
			}]
		}],
		buttonBar3: ["wm.Panel", {"_classes":{"domNode":["dialogfooter"]},"border":"1","desktopHeight":"32px","enableTouchHeight":true,"height":"32px","horizontalAlign":"right","layoutKind":"left-to-right","mobileHeight":"40px","verticalAlign":"top","width":"100%"}, {}, {
			doneButton3: ["wm.Button", {"caption":"Done","imageIndex":0,"imageList":"app.silkIconList","margin":"4"}, {"onclick":"EditIssueFlows.hide"}]
		}]
	}],
	EditCategories: ["wm.DesignableDialog", {"buttonBarId":"buttonBar","containerWidgetId":"containerWidget","title":"Associate categories with this issue"}, {"onShow":"selectedGrid4.deselectAll","onShow1":"availableGrid4.deselectAll","onShow2":"addButton4.disable","onShow3":"removeButton4.disable","onShow4":"delCategoryButton6.disable"}, {
		containerWidget4: ["wm.Container", {"_classes":{"domNode":["wmdialogcontainer","MainContent"]},"autoScroll":true,"height":"100%","horizontalAlign":"left","layoutKind":"left-to-right","padding":"5","verticalAlign":"top","width":"100%"}, {}, {
			selectedGrid4: ["wm.DojoGrid", {"columns":[{"show":false,"field":"PHONE COLUMN","title":"-","width":"100%","align":"left","editorProps":{"restrictValues":true},"expression":"\"<div class='MobileRowTitle'>Selected Categories: \" + ${issueCategory.name} + \"</div>\"\n","mobileColumn":true},{"show":false,"field":"id.issueId","title":"Id.issueId","width":"80px","align":"right","formatFunc":"","mobileColumn":false},{"show":false,"field":"id.issueCategoryId","title":"Id.issueCategoryId","width":"80px","align":"right","formatFunc":"","mobileColumn":false},{"show":true,"field":"issueCategory.name","title":"Selected Categories","width":"100%","align":"left","formatFunc":"","editorProps":{"restrictValues":true},"mobileColumn":false}],"height":"100%","localizationStructure":{},"margin":"4","minDesktopHeight":60,"minWidth":100,"singleClickEdit":true}, {"onDeselect":"removeButton4.disable","onSelect":"removeButton4.enable"}, {
				binding: ["wm.Binding", {}, {}, {
					wire: ["wm.Wire", {"expression":undefined,"source":"issueCategoriesVariable1","targetProperty":"dataSet"}, {}]
				}]
			}],
			panel7: ["wm.Panel", {"fitToContentWidth":true,"height":"100%","horizontalAlign":"left","verticalAlign":"middle","width":"82px"}, {}, {
				addButton4: ["wm.Button", {"caption":"Add","imageIndex":3,"imageList":"app.silkIconList","margin":"4"}, {"onclick":"addButton4Click"}],
				removeButton4: ["wm.Button", {"caption":"Remove","imageIndex":5,"imageList":"app.silkIconList","margin":"4"}, {"onclick":"removeButton4Click","onclick1":"otherCategoriesVariable1"}]
			}],
			availableGrid4: ["wm.DojoGrid", {"columns":[{"show":false,"field":"id","title":"Available","width":"50px","align":"center","formatFunc":"","editorProps":{"restrictValues":true},"expression":"\"I-\" + ${id}","mobileColumn":false},{"show":false,"field":"version","title":"Version","width":"80px","align":"right","formatFunc":"","mobileColumn":false},{"show":false,"field":"PHONE COLUMN","title":"-","width":"100%","align":"left","expression":"\"<div class='MobileRowTitle'>Available Categories: \" + ${name} + \"</div>\"\n","mobileColumn":true},{"show":true,"field":"name","title":"Available Categories","width":"100%","align":"left","formatFunc":"","editorProps":{"restrictValues":true},"mobileColumn":false}],"height":"100%","margin":"4","minDesktopHeight":60,"minWidth":100,"primaryKeyFields":["id"],"singleClickEdit":true}, {"onDeselect":"addButton4.disable","onSelect":"addButton4.enable"}, {
				binding: ["wm.Binding", {}, {}, {
					wire: ["wm.Wire", {"expression":undefined,"source":"otherCategoriesVariable1","targetProperty":"dataSet"}, {}]
				}]
			}],
			catButtonPanel: ["wm.Panel", {"fitToContentWidth":true,"height":"100%","horizontalAlign":"left","verticalAlign":"top","width":"34px"}, {}, {
				addCategoryButton5: ["wm.Button", {"_classes":{"domNode":["iconButton"]},"caption":undefined,"hint":"Add a category","imageIndex":1,"imageList":"app.silkIconList","margin":"4","width":"32px"}, {"onclick":"genericDialog1.show"}],
				delCategoryButton6: ["wm.Button", {"_classes":{"domNode":["iconButton"]},"caption":undefined,"hint":"Remove category","imageIndex":21,"imageList":"app.silkIconList","margin":"4","width":"32px"}, {"onclick":"delCategoryButton6Click"}, {
					binding: ["wm.Binding", {}, {}, {
						wire: ["wm.Wire", {"expression":undefined,"source":"availableGrid4.emptySelection","targetProperty":"disabled"}, {}]
					}]
				}]
			}]
		}],
		buttonBar4: ["wm.Panel", {"_classes":{"domNode":["dialogfooter"]},"border":"1","desktopHeight":"32px","enableTouchHeight":true,"height":"32px","horizontalAlign":"right","layoutKind":"left-to-right","mobileHeight":"40px","verticalAlign":"top","width":"100%"}, {}, {
			doneButton4: ["wm.Button", {"caption":"Done","imageIndex":0,"imageList":"app.silkIconList","margin":"4"}, {"onclick":"EditCategories.hide"}]
		}]
	}],
	genericDialog1: ["wm.GenericDialog", {"button1Caption":"Add","button1Close":true,"button2Caption":"Cancel","button2Close":true,"desktopHeight":"117px","height":"127px","showInput":true,"title":"Add an issue category","userPrompt":"Category name:"}, {"onButton1Click":"genericDialog1Button1Click","onButton2Click":"genericDialog1.hide"}],
	layoutBox: ["wm.Layout", {"_classes":{"domNode":["back"]},"enableTouchHeight":true,"horizontalAlign":"center","verticalAlign":"top","width":"867px"}, {}, {
		container: ["wm.Panel", {"_classes":{"domNode":["container"]},"height":"100%","horizontalAlign":"left","verticalAlign":"top","width":"800px"}, {}, {
			header: ["wm.Panel", {"_classes":{"domNode":["banner"]},"height":"51px","horizontalAlign":"left","layoutKind":"left-to-right","verticalAlign":"middle","width":"100%"}, {}, {
				logo: ["wm.Picture", {"height":"51px","imageList":"app.silkIconList","source":"resources/images/logo.png","width":"328px"}, {}, {
					binding: ["wm.Binding", {}, {}, {
						wire: ["wm.Wire", {"expression":"\"Channels Analyst v\" + ${app.projectVersion} + \".\" + ${app.projectSubVersion}","targetProperty":"hint"}, {}]
					}]
				}],
				button1Panel: ["wm.Panel", {"height":"34px","horizontalAlign":"right","verticalAlign":"top","width":"100%"}, {}, {
					logoutButton: ["wm.Button", {"caption":"Logout","imageIndex":42,"imageList":"app.silkIconList","margin":"4"}, {"onclick":"logoutVariable1"}]
				}]
			}],
			projectLivePanel1: ["wm.LivePanel", {"autoScroll":false,"horizontalAlign":"left","verticalAlign":"top"}, {}, {
				binding: ["wm.Binding", {}, {}, {
					wire: ["wm.Wire", {"source":"Project_List","targetId":null,"targetProperty":"gridLayer"}, {}],
					wire1: ["wm.Wire", {"source":"Edit_Project","targetId":null,"targetProperty":"detailsLayer"}, {}],
					wire2: ["wm.Wire", {"source":"projectLiveForm1","targetId":null,"targetProperty":"liveForm"}, {}],
					wire3: ["wm.Wire", {"source":"projectDojoGrid","targetId":null,"targetProperty":"dataGrid"}, {}],
					wire4: ["wm.Wire", {"source":"projectSaveButton","targetId":null,"targetProperty":"saveButton"}, {}]
				}],
				projectLayers: ["wm.BreadcrumbLayers", {"conditionalTabButtons":false}, {}, {
					Project_List: ["wm.Layer", {"borderColor":"","caption":"Projects","horizontalAlign":"left","themeStyleType":"ContentPanel","verticalAlign":"top"}, {"onShow":"projectDojoGrid.deselectAll"}, {
						html6: ["wm.Html", {"autoSizeHeight":true,"height":"48px","html":"<div class=\"note\">Pick or create a project to work on.</div>","minDesktopHeight":15}, {}],
						projectDojoGridPanel: ["wm.Panel", {"height":"100%","horizontalAlign":"left","layoutKind":"left-to-right","verticalAlign":"top","width":"100%"}, {}, {
							projectDojoGrid: ["wm.DojoGrid", {"columns":[{"show":false,"field":"id","title":"Id","width":"80px","align":"right","formatFunc":"","editorProps":{"restrictValues":true},"mobileColumn":false},{"show":true,"field":"name","title":"Name","width":"25%","align":"left","formatFunc":"","editorProps":{"restrictValues":true},"mobileColumn":false},{"show":true,"field":"description","title":"Description","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"version","title":"Version","width":"80px","align":"right","formatFunc":"","mobileColumn":false},{"show":false,"field":"PHONE COLUMN","title":"-","width":"100%","align":"left","expression":"\"<div class='MobileRowTitle'>Name: \" + ${name} + \"</div>\"\n+ \"<div class='MobileRow'>Description: \" + ${description} + \"</div>\"\n","mobileColumn":true},{"show":false,"field":"tenantId","title":"TenantId","width":"80px","displayType":"Number","align":"right","formatFunc":""}],"dsType":"com.analystdb.data.Project","height":"100%","localizationStructure":{},"margin":"4","primaryKeyFields":["id"]}, {"onSelect":"maxSequence","onSelect2":"projectLivePanel1.popupLivePanelEdit","onSelect3":"projectIssues","onSelect4":"recentInterviewsVariable1"}, {
								binding: ["wm.Binding", {}, {}, {
									wire: ["wm.Wire", {"expression":undefined,"source":"projectLiveVariable1","targetProperty":"dataSet"}, {}]
								}]
							}],
							projectNewButton: ["wm.Button", {"_classes":{"domNode":["iconButton"]},"caption":undefined,"hint":"Add a new project","imageIndex":1,"imageList":"app.silkIconList","margin":"4","width":"32px"}, {"onclick":"projectLivePanel1.popupLivePanelInsert"}]
						}],
						button6: ["wm.Panel", {"fitToContentHeight":true,"height":"34px","horizontalAlign":"left","layoutKind":"left-to-right","verticalAlign":"top","width":"100%"}, {}, {
							button7: ["wm.Button", {"caption":"Documents","imageIndex":73,"imageList":"app.silkIconList","margin":"4","width":"100px"}, {"onclick":"Edit_Documents"}],
							html4: ["wm.Html", {"autoSizeHeight":true,"height":"22px","html":"Manage global documents referred to by all projects.","minDesktopHeight":15,"padding":"5,0,0"}, {}]
						}]
					}],
					Edit_Project: ["wm.Layer", {"autoScroll":true,"borderColor":"","horizontalAlign":"left","themeStyleType":"ContentPanel","verticalAlign":"top"}, {}, {
						binding: ["wm.Binding", {}, {}, {
							wire: ["wm.Wire", {"expression":"if ( ${projectDojoGrid.emptySelection} ) { \"(New Project)\"; } else { ${nameEditor1.dataValue}; }","targetProperty":"caption"}, {}],
							wire1: ["wm.Wire", {"expression":undefined,"source":"projectDojoGrid.isRowSelected","targetProperty":"showing"}, {}]
						}],
						projectLiveForm1: ["wm.LiveForm", {"alwaysPopulateEditors":true,"captionSize":"100px","confirmDelete":"Are you sure you want to delete this entire project?","height":"100%","horizontalAlign":"left","liveEditing":false,"margin":"4","verticalAlign":"top"}, {"onDeleteData":"Project_List","onInsertData":"analysisInsert","onSuccess":"projectLivePanel1.popupLiveFormSuccess"}, {
							binding: ["wm.Binding", {}, {}, {
								wire: ["wm.Wire", {"expression":undefined,"source":"projectDojoGrid.selectedItem","targetProperty":"dataSet"}, {}]
							}],
							nameEditor1Panel: ["wm.Panel", {"height":"34px","horizontalAlign":"left","layoutKind":"left-to-right","verticalAlign":"middle","width":"100%"}, {}, {
								nameEditor1: ["wm.Text", {"border":"0","caption":"Name","changeOnKey":true,"dataValue":"","desktopHeight":"26px","emptyValue":"emptyString","formField":"name","height":"26px","maxChars":"30","required":true,"width":"100%"}, {}],
								button17: ["wm.Button", {"caption":"Cancel","imageIndex":21,"imageList":"app.silkIconList","margin":"4"}, {"onclick":"projectLiveForm1.cancelEdit","onclick1":"Project_List"}, {
									binding: ["wm.Binding", {}, {}, {
										wire: ["wm.Wire", {"expression":undefined,"source":"projectDojoGrid.emptySelection","targetProperty":"showing"}, {}]
									}]
								}],
								projectDeleteButton: ["wm.Button", {"caption":"Delete","hint":"Delete this project","imageIndex":21,"imageList":"app.silkIconList","margin":"4"}, {"onclick":"projectLiveForm1.deleteData"}, {
									binding: ["wm.Binding", {}, {}, {
										wire: ["wm.Wire", {"expression":undefined,"source":"projectDojoGrid.isRowSelected","targetProperty":"showing"}, {}]
									}]
								}],
								projectSaveButton: ["wm.Button", {"caption":"Save","imageIndex":0,"imageList":"app.silkIconList","margin":"4"}, {"onclick":"projectLiveForm1.saveDataIfValid"}, {
									binding: ["wm.Binding", {}, {}, {
										wire: ["wm.Wire", {"expression":"${projectLiveForm1.invalid} || !${projectLiveForm1.isDirty}","targetId":null,"targetProperty":"disabled"}, {}]
									}]
								}]
							}],
							descriptionEditor1: ["wm.LargeTextArea", {"border":"0","caption":"Description","captionAlign":"right","captionPosition":"left","captionSize":"100px","changeOnKey":true,"dataValue":"","emptyValue":"emptyString","formField":"description","width":"100%"}, {}],
							panel1: ["wm.Panel", {"height":"100%","horizontalAlign":"left","margin":"15,0,0,100","verticalAlign":"top","width":"100%"}, {}, {
								button3: ["wm.Panel", {"fitToContentHeight":true,"height":"34px","horizontalAlign":"left","layoutKind":"left-to-right","verticalAlign":"top","width":"100%"}, {}, {
									button4: ["wm.Button", {"caption":"Summary","imageIndex":23,"imageList":"app.silkIconList","margin":"4","width":"100px"}, {"onclick":"showSummary"}, {
										binding: ["wm.Binding", {}, {}, {
											wire: ["wm.Wire", {"expression":undefined,"source":"projectDojoGrid.emptySelection","targetProperty":"disabled"}, {}]
										}]
									}],
									html2: ["wm.Html", {"autoSizeHeight":true,"height":"22px","html":"Make sense of the interview data.","minDesktopHeight":15,"padding":"5,0,0"}, {}]
								}],
								button1: ["wm.Panel", {"fitToContentHeight":true,"height":"34px","horizontalAlign":"left","layoutKind":"left-to-right","verticalAlign":"top","width":"100%"}, {}, {
									button2: ["wm.Button", {"caption":"Interviews","imageIndex":27,"imageList":"app.silkIconList","margin":"4","width":"100px"}, {"onclick1":"upcomingInterviewsVariable1","onclick2":"resourcesVariable1","onclick3":"Edit_Interviews"}, {
										binding: ["wm.Binding", {}, {}, {
											wire: ["wm.Wire", {"expression":undefined,"source":"projectDojoGrid.emptySelection","targetProperty":"disabled"}, {}]
										}]
									}],
									html1: ["wm.Html", {"autoSizeHeight":true,"height":"22px","html":"Schedule and do some interviews to gather issues and flows.","minDesktopHeight":15,"padding":"5,0,0"}, {}]
								}],
								button5: ["wm.Panel", {"fitToContentHeight":true,"height":"34px","horizontalAlign":"left","layoutKind":"left-to-right","showing":false,"verticalAlign":"top","width":"100%"}, {}, {
									button8: ["wm.Button", {"caption":"Solutions","imageIndex":19,"imageList":"app.silkIconList","margin":"4","width":"100px"}, {"onclick":"plansVariable1","onclick1":"Edit_Solutions"}, {
										binding: ["wm.Binding", {}, {}, {
											wire: ["wm.Wire", {"expression":undefined,"source":"projectDojoGrid.emptySelection","targetProperty":"disabled"}, {}]
										}]
									}],
									html3: ["wm.Html", {"autoSizeHeight":true,"height":"22px","html":"Manage implementation plans for selected approaches.","minDesktopHeight":15,"padding":"5,0,0"}, {}]
								}],
								calendarButtonPanel: ["wm.Panel", {"height":"100%","horizontalAlign":"right","layoutKind":"left-to-right","verticalAlign":"bottom","width":"100%"}, {}, {
									calendarButton: ["wm.Button", {"_classes":{"domNode":["iconButton"]},"caption":undefined,"hint":"Link to ICal calendarar for this project ","imageIndex":39,"imageList":"app.silkIconList","margin":"4","width":"32px"}, {}]
								}]
							}]
						}]
					}],
					Edit_Interviews: ["wm.Layer", {"borderColor":"","caption":"Interviews","horizontalAlign":"left","themeStyleType":"ContentPanel","verticalAlign":"top"}, {"onShow":"interviewDojoGrid1.deselectAll","onShow1":"Edit_InterviewsShow1"}, {
						binding: ["wm.Binding", {}, {}, {
							wire: ["wm.Wire", {"expression":undefined,"source":"projectDojoGrid.isRowSelected","targetProperty":"showing"}, {}]
						}],
						tabLayers1: ["wm.TabLayers", {}, {}, {
							layer1: ["wm.Layer", {"horizontalAlign":"left","themeStyleType":"ContentPanel","verticalAlign":"top"}, {}, {
								binding: ["wm.Binding", {}, {}, {
									wire: ["wm.Wire", {"expression":"if ( ${interviewDojoGrid1.dataSet.data} ) { \"Recent  (\" + ${interviewDojoGrid1.dataSet.data.length} + \")\"; } else { \"Recent\"; }","targetProperty":"caption"}, {}]
								}],
								html7: ["wm.Html", {"autoSizeHeight":true,"height":"48px","html":"<div class=\"note\">Most recent interviews from each resource.</div>","minDesktopHeight":15}, {}],
								interviewDojoGrid1: ["wm.DojoGrid", {"columns":[{"show":false,"field":"interview.id","title":"Id","width":"100%","align":"left","formatFunc":"","editorProps":{"restrictValues":true},"mobileColumn":false},{"show":true,"field":"interview.scheduled","title":"Date","width":"100px","align":"left","formatFunc":"wm_date_formatter","editorProps":{"restrictValues":true},"mobileColumn":false},{"show":false,"field":"interview.resource.id","title":"Id","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"interview.resource.project.id","title":"Id","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"interview.resource.project.description","title":"Description","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"interview.resource.project.name","title":"Name","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"interview.resource.project.version","title":"Version","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"interview.resource.description","title":"Description","width":"100%","align":"left","formatFunc":"","editorProps":{"restrictValues":true},"mobileColumn":false},{"show":false,"field":"interview.resource.name","title":"Name","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"interview.resource.version","title":"Version","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"interview.version","title":"Version","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"PHONE COLUMN","title":"-","width":"100%","align":"left","expression":"\"<div class='MobileRowTitle'>Date: \" + wm.List.prototype.dateFormatter({}, null,null,null,${interview.scheduled}) + \"</div>\"\n+ \"<div class='MobileRow'>Resource: \" + ${resource} + \"</div>\"\n","mobileColumn":true},{"show":false,"field":"interview.done","title":"Interview.done","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"interview.notes","title":"Interview.notes","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":true,"field":"resource","title":"Resource","width":"100%","align":"left","formatFunc":"","editorProps":{"restrictValues":true},"mobileColumn":false},{"show":true,"field":"issues","title":"Issues","width":"90px","align":"center","editorProps":{"restrictValues":true},"expression":"if ( ${issues} == 0 ) { \"\" } else { ${issues} }","mobileColumn":false},{"show":false,"field":"approaches","title":"Approaches","width":"90px","align":"center","editorProps":{"restrictValues":true},"expression":"if ( ${approaches} == 0 ) { \"\" } else { ${approaches} }","mobileColumn":false},{"show":true,"field":"flows","title":"Flows","width":"90px","align":"center","editorProps":{"restrictValues":true},"expression":"if ( ${flows} == 0 ) { \"\" } else { ${flows} }","mobileColumn":false},{"show":false,"field":"id","title":"Id","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"interview.resource.project.tenantId","title":"Interview.resource.project.tenantId","width":"100%","displayType":"Java.lang.Integer","align":"left","formatFunc":""}],"dsType":"com.analystdb.data.output.RecentInterviewsRtnType","height":"100%","margin":"4","minDesktopHeight":60,"primaryKeyFields":["id"],"singleClickEdit":true}, {"onSelect":"approachLiveVariable1","onSelect4":"interviewIssuesVariable1","onSelect5":"interviewForm1.beginDataUpdate","onSelect6":"resourceattributeLiveVariable1","onSelect7":"interviewFlowsVariable1","onSelect8":"interviewApproachesVariable1","onSelect9":"Edit_Interview"}, {
									binding: ["wm.Binding", {}, {}, {
										wire: ["wm.Wire", {"expression":undefined,"source":"recentInterviewsVariable1","targetProperty":"dataSet"}, {}]
									}]
								}]
							}],
							layer2: ["wm.Layer", {"horizontalAlign":"left","themeStyleType":"ContentPanel","verticalAlign":"top"}, {}, {
								binding: ["wm.Binding", {}, {}, {
									wire: ["wm.Wire", {"expression":"if ( ${upcomingGrid1.dataSet.data} ) { \"Scheduled  (\" + ${upcomingGrid1.dataSet.data.length} + \")\"; } else { \"Scheduled\"; }","targetProperty":"caption"}, {}]
								}],
								html8: ["wm.Html", {"autoSizeHeight":true,"height":"48px","html":"<div class=\"note\">Manage upcoming interviews.</div>","minDesktopHeight":15}, {}],
								upcomingGrid1Panel: ["wm.Panel", {"height":"100%","horizontalAlign":"left","layoutKind":"left-to-right","verticalAlign":"top","width":"100%"}, {}, {
									upcomingGrid1: ["wm.DojoGrid", {"columns":[{"show":false,"field":"PHONE COLUMN","title":"-","width":"100%","align":"left","editorProps":{"restrictValues":true},"expression":"\"<div class='MobileRowTitle'>Scheduled: \" + wm.List.prototype.dateFormatter({\"useLocalTime\":false,\"datePattern\":\"MM/dd/y\",\"timePattern\":\"hh:mm a  (EEE)\",\"formatLength\":\"short\",\"dateType\":\"date and time\"}, null,null,null,${interview.scheduled}) + \"</div>\"\n+ \"<div class='MobileRow'> Name: \" + ${name} + \"</div>\"\n","mobileColumn":true},{"show":false,"field":"interview.id","title":"Interview.id","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":true,"field":"interview.scheduled","title":"Scheduled","width":"150px","align":"left","formatFunc":"wm_date_formatter","formatProps":{"useLocalTime":false,"datePattern":"MM/dd/y","timePattern":"hh:mm a  (EEE)","formatLength":"short","dateType":"date and time"},"editorProps":{"restrictValues":true},"mobileColumn":false},{"show":false,"field":"interview.resource.id","title":"Interview.resource.id","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"interview.resource.project.id","title":"Interview.resource.project.id","width":"100%","align":"left","formatFunc":"","editorProps":{"restrictValues":true},"mobileColumn":false},{"show":false,"field":"interview.resource.project.description","title":"Interview.resource.project.description","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"interview.resource.project.name","title":"Interview.resource.project.name","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"interview.resource.project.version","title":"Interview.resource.project.version","width":"100%","align":"left","formatFunc":"","editorProps":{"restrictValues":true},"mobileColumn":false},{"show":false,"field":"interview.resource.description","title":"Interview.resource.description","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"interview.resource.name","title":"Interview.resource.name","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"interview.resource.version","title":"Interview.resource.version","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"interview.version","title":"Interview.version","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":true,"field":"name","title":" Name","width":"100%","align":"left","formatFunc":"","editorProps":{"restrictValues":true},"mobileColumn":false},{"show":false,"field":"interview.done","title":"Interview.done","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"interview.notes","title":"Interview.notes","width":"100%","displayType":"Java.lang.String","align":"left","formatFunc":""},{"show":false,"field":"scheduled","title":"Scheduled","width":"100%","displayType":"Java.util.Date","align":"left","formatFunc":""},{"show":false,"field":"interview.resource.project.tenantId","title":"Interview.resource.project.tenantId","width":"100%","displayType":"Java.lang.Integer","align":"left","formatFunc":""}],"dsType":"com.analystdb.data.output.UpcomingInterviewsRtnType","height":"100%","margin":"4","minDesktopHeight":60,"singleClickEdit":true}, {}, {
										binding: ["wm.Binding", {}, {}, {
											wire: ["wm.Wire", {"expression":undefined,"source":"upcomingInterviewsVariable1","targetProperty":"dataSet"}, {}]
										}]
									}],
									button10Panel: ["wm.Panel", {"height":"66px","horizontalAlign":"left","verticalAlign":"top","width":"100px"}, {}, {
										interviewButton: ["wm.Button", {"caption":"Interview!","imageIndex":27,"imageList":"app.silkIconList","margin":"4","width":"100%"}, {"onclick":"interviewButtonClick"}, {
											binding: ["wm.Binding", {}, {}, {
												wire: ["wm.Wire", {"expression":undefined,"source":"upcomingGrid1.emptySelection","targetProperty":"disabled"}, {}]
											}]
										}],
										deleteScheduled9: ["wm.Button", {"caption":"Delete","imageIndex":21,"imageList":"app.silkIconList","margin":"4","width":"100%"}, {"onclick":"deleteScheduled9Click","onclick1":"upcomingInterviewsVariable1"}, {
											binding: ["wm.Binding", {}, {}, {
												wire: ["wm.Wire", {"expression":undefined,"source":"upcomingGrid1.emptySelection","targetProperty":"disabled"}, {}]
											}]
										}]
									}]
								}],
								filteringLookup1Panel: ["wm.Panel", {"height":"50px","horizontalAlign":"left","layoutKind":"left-to-right","verticalAlign":"middle","width":"100%"}, {}, {
									resourceLookup1: ["wm.Lookup", {"caption":"Who","dataType":"com.analystdb.data.Resource","dataValue":undefined,"displayField":"name","displayValue":"","startUpdate":false,"width":"100%"}, {"onchange":"resourceLookup1Change"}, {
										binding: ["wm.Binding", {}, {}, {
											wire: ["wm.Wire", {"expression":undefined,"source":"resourcesVariable1","targetProperty":"dataSet"}, {}]
										}]
									}],
									scheduleDateTime1: ["wm.DateTime", {"border":"0","caption":"When","displayValue":"","width":"100%"}, {"onchange":"scheduleDateTime1Change"}, {
										binding: ["wm.Binding", {}, {}, {
											wire: ["wm.Wire", {"expression":"new Date()\n","targetProperty":"minimum"}, {}]
										}]
									}],
									scheduleButton: ["wm.Button", {"caption":"Schedule","disabled":true,"imageIndex":39,"imageList":"app.silkIconList","margin":"4","width":"100px"}, {"onclick":"scheduleButtonClick"}]
								}]
							}]
						}]
					}],
					Edit_Summary: ["wm.Layer", {"borderColor":"","caption":"Summary","horizontalAlign":"left","themeStyleType":"ContentPanel","verticalAlign":"top"}, {"onShow":"summaryIssuesGrid1.deselectAll","onShow1":"summaryFlowsGrid1.deselectAll","onShow2":"summaryAppIssuesGrid1.deselectAll","onShow3":"summaryDocIssuesGrid1.deselectAll"}, {
						binding: ["wm.Binding", {}, {}, {
							wire: ["wm.Wire", {"expression":undefined,"source":"projectDojoGrid.isRowSelected","targetProperty":"showing"}, {}]
						}],
						html18: ["wm.Html", {"autoSizeHeight":true,"height":"48px","html":"<div class=\"note\">This screen shows the combination of all data gathered in interviews. Interview some people first.</div>","minDesktopHeight":15}, {}, {
							binding: ["wm.Binding", {}, {}, {
								wire: ["wm.Wire", {"expression":undefined,"source":"recentInterviewsVariable1.isEmpty","targetProperty":"showing"}, {}]
							}]
						}],
						tabLayers2: ["wm.TabLayers", {}, {}, {
							binding: ["wm.Binding", {}, {}, {
								wire: ["wm.Wire", {"expression":"!${recentInterviewsVariable1.isEmpty}","targetProperty":"showing"}, {}]
							}],
							layer9: ["wm.Layer", {"horizontalAlign":"left","themeStyleType":"ContentPanel","verticalAlign":"top"}, {}, {
								binding: ["wm.Binding", {}, {}, {
									wire: ["wm.Wire", {"expression":"\"Documents (\" + ${allDocsWithIssues.count} + \")\"","targetProperty":"caption"}, {}]
								}],
								html14: ["wm.Html", {"autoSizeHeight":true,"height":"48px","html":"<div class=\"note\">Documents with pending issues. Select a phase and document to filter the issues list.</div>","minDesktopHeight":15}, {}],
								dojoChart2Panel: ["wm.Panel", {"height":"100%","horizontalAlign":"left","layoutKind":"left-to-right","verticalAlign":"top","width":"100%"}, {}, {
									dojoGrid2: ["wm.DojoGrid", {"columns":[{"show":true,"field":"phase","title":"Phase","width":"100%","align":"left","formatFunc":"","editorProps":{"restrictValues":true},"mobileColumn":false},{"show":true,"field":"issues","title":"Issues","width":"60px","align":"center","formatFunc":"","editorProps":{"restrictValues":true},"mobileColumn":false},{"show":false,"field":"PHONE COLUMN","title":"-","width":"100%","align":"left","expression":"\"<div class='MobileRowTitle'>Phase: \" + ${phase} + \"</div>\"\n+ \"<div class='MobileRow'>Issues: \" + ${issues} + \"</div>\"\n","mobileColumn":true},{"show":false,"field":"category.id","title":"Category.id","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"category.sequence","title":"Category.sequence","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"category.name","title":"Category.name","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"category.tenantId","title":"Category.tenantId","width":"100%","displayType":"Java.lang.Integer","align":"left","formatFunc":""}],"dsType":"com.analystdb.data.output.DocumentCategoryIssueCountsRtnType","margin":"4","minDesktopHeight":60,"singleClickEdit":true}, {"onSelect":"docIssuesCountsVariable1","onSelect1":"dojoGrid3.deselectAll","onSelect2":"docIssuesVariable1"}, {
										binding: ["wm.Binding", {}, {}, {
											wire: ["wm.Wire", {"expression":undefined,"source":"docCatIssuesVariable1","targetProperty":"dataSet"}, {}]
										}]
									}],
									dojoGrid3: ["wm.DojoGrid", {"columns":[{"show":true,"field":"doc","title":"Document","width":"100%","align":"left","formatFunc":"","editorProps":{"restrictValues":true},"mobileColumn":false},{"show":true,"field":"issues","title":"Issues","width":"60px","align":"center","formatFunc":"","editorProps":{"restrictValues":true},"mobileColumn":false},{"show":false,"field":"PHONE COLUMN","title":"-","width":"100%","align":"left","expression":"\"<div class='MobileRowTitle'>Document: \" + ${doc} + \"</div>\"\n+ \"<div class='MobileRow'>Issues: \" + ${issues} + \"</div>\"\n","mobileColumn":true},{"show":false,"field":"id","title":"Id","width":"100%","align":"left","formatFunc":"","mobileColumn":false}],"dsType":"com.analystdb.data.output.DocumentIssueCountRtnType","margin":"4","minDesktopHeight":60,"primaryKeyFields":["id"],"singleClickEdit":true}, {"onSelect":"docIssuesVariable1"}, {
										binding: ["wm.Binding", {}, {}, {
											wire: ["wm.Wire", {"expression":undefined,"source":"docIssuesCountsVariable1","targetProperty":"dataSet"}, {}],
											wire1: ["wm.Wire", {"expression":undefined,"source":"dojoGrid2.isRowSelected","targetProperty":"showing"}, {}]
										}]
									}]
								}],
								summaryDocIssuesGrid1: ["wm.DojoGrid", {"columns":[{"show":false,"field":"PHONE COLUMN","title":"-","width":"100%","align":"left","editorProps":{"restrictValues":true},"expression":"\"<div class='MobileRowTitle'>Description: \" + ${issue.description} + \"</div>\"\n+ \"<div class='MobileRow'>Approaches: \" + wm.List.prototype.numberFormatter({}, null,null,null,${approaches}) + \"</div>\"\n","mobileColumn":true},{"show":false,"field":"issue.id","title":"Issue.id","width":"100%","align":"left","formatFunc":"","editorProps":{"restrictValues":true},"mobileColumn":false},{"show":false,"field":"issue.project.id","title":"Issue.project.id","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"issue.project.description","title":"Issue.project.description","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"issue.project.name","title":"Issue.project.name","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"issue.project.version","title":"Issue.project.version","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":true,"field":"issue.sequence","title":"Issue","width":"50px","align":"center","formatFunc":"","editorProps":{"restrictValues":true},"expression":"\"I-\" + ${issue.sequence}","mobileColumn":false},{"show":true,"field":"issue.description","title":"Description","width":"100%","align":"left","formatFunc":"","editorProps":{"restrictValues":true},"mobileColumn":false},{"show":false,"field":"issue.name","title":"Issue.name","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"issue.version","title":"Issue.version","width":"100%","align":"left","formatFunc":"","expression":"","mobileColumn":false},{"show":true,"field":"approaches","title":"Approaches","width":"90px","align":"center","formatFunc":"wm_number_formatter","editorProps":{"restrictValues":true},"mobileColumn":false},{"show":false,"field":"id","title":"Id","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"issue.project.tenantId","title":"Issue.project.tenantId","width":"100%","displayType":"Java.lang.Integer","align":"left","formatFunc":""}],"dsType":"com.analystdb.data.output.DocumentIssuesRtnType","height":"100%","margin":"4","minDesktopHeight":60,"primaryKeyFields":["id"],"singleClickEdit":true}, {"onSelect":"summaryDocIssuesGrid1Select"}, {
									binding: ["wm.Binding", {}, {}, {
										wire: ["wm.Wire", {"expression":undefined,"source":"docIssuesVariable1","targetProperty":"dataSet"}, {}]
									}]
								}],
								progressBar1: ["wm.dijit.ProgressBar", {"height":"24px","hint":"Documents with issues","width":"100%"}, {}, {
									binding: ["wm.Binding", {}, {}, {
										wire: ["wm.Wire", {"expression":"${allDocsWithIssues.count} * 100 / ${documentsLiveVariable2.count}","targetProperty":"progress"}, {}]
									}]
								}]
							}],
							layer4: ["wm.Layer", {"horizontalAlign":"left","themeStyleType":"ContentPanel","verticalAlign":"top"}, {}, {
								binding: ["wm.Binding", {}, {}, {
									wire: ["wm.Wire", {"expression":"\"Issues (\" + ${allIssues.count} + \")\"","targetProperty":"caption"}, {}]
								}],
								html9: ["wm.Html", {"autoSizeHeight":true,"height":"48px","html":"<div class=\"note\">Review all issues.</div>","minDesktopHeight":15}, {}],
								dojoChart1Panel: ["wm.Panel", {"height":"200px","horizontalAlign":"left","layoutKind":"left-to-right","verticalAlign":"top","width":"100%"}, {}, {
									dojoChart1: ["wm.DojoChart", {"chartType":"Pie","hideLegend":true,"legendHeight":"0px","padding":"4","theme":"PlotKit.blue","xAxis":"name","yAxis":"name,issues","ydisplay":"Number"}, {}, {
										binding: ["wm.Binding", {}, {}, {
											wire: ["wm.Wire", {"expression":undefined,"source":"issueCategoryCount","targetProperty":"dataSet"}, {}]
										}],
										yformat: ["wm.NumberFormatter", {}, {}]
									}],
									issueCategoryGrid1: ["wm.DojoGrid", {"columns":[{"show":false,"field":"PHONE COLUMN","title":"-","width":"100%","align":"left","editorProps":{"restrictValues":true},"expression":"\"<div class='MobileRowTitle'>Category: \" + ${category.name} + \"</div>\"\n+ \"<div class='MobileRow'>Issues: \" + ${issues} + \"</div>\"\n","mobileColumn":true},{"show":false,"field":"category.id","title":"Category.id","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"category.project.id","title":"Category.project.id","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"category.project.description","title":"Category.project.description","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"category.project.name","title":"Category.project.name","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"category.project.version","title":"Category.project.version","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":true,"field":"category.name","title":"Category","width":"100%","align":"left","formatFunc":"","editorProps":{"restrictValues":true},"mobileColumn":false},{"show":false,"field":"category.version","title":"Category.version","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":true,"field":"issues","title":"Issues","width":"60px","align":"center","formatFunc":"","editorProps":{"restrictValues":true},"mobileColumn":false},{"show":false,"field":"name","title":"Name","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"category.project.tenantId","title":"Category.project.tenantId","width":"100%","displayType":"Java.lang.Integer","align":"left","formatFunc":""}],"dsType":"com.analystdb.data.output.IssueCategoryCountsRtnType","margin":"4","minDesktopHeight":60,"singleClickEdit":true}, {"onDeselect":"issuesByCategoryVariable1","onSelect":"issuesByCategoryVariable1"}, {
										binding: ["wm.Binding", {}, {}, {
											wire: ["wm.Wire", {"expression":undefined,"source":"issueCategoryCount","targetProperty":"dataSet"}, {}]
										}]
									}]
								}],
								summaryIssuesGrid1: ["wm.DojoGrid", {"columns":[{"show":false,"field":"PHONE COLUMN","title":"-","width":"100%","align":"left","editorProps":{"restrictValues":true},"expression":"\"<div class='MobileRowTitle'>Description: \" + ${issue.description} + \"</div>\"\n+ \"<div class='MobileRow'>Documents: \" + ${documents} + \"</div>\"\n+ \"<div class='MobileRow'>Approaches: \" + ${approaches} + \"</div>\"\n+ \"<div class='MobileRow'>Flows: \" + ${flows} + \"</div>\"\n","mobileColumn":true},{"show":true,"field":"issue.sequence","title":"Issue","width":"50px","align":"center","formatFunc":"","editorProps":{"restrictValues":true},"expression":"\"I-\" + ${issue.sequence}","mobileColumn":false},{"show":true,"field":"issue.description","title":"Description","width":"100%","align":"left","formatFunc":"","editorProps":{"restrictValues":true},"mobileColumn":false},{"show":false,"field":"issue.id","title":"Issue.id","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"issue.project.id","title":"Issue.project.id","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"issue.project.description","title":"Issue.project.description","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"issue.project.name","title":"Issue.project.name","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"issue.project.version","title":"Issue.project.version","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"issue.name","title":"Issue.name","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"issue.version","title":"Issue.version","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"key","title":"Key","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":true,"field":"documents","title":"Documents","width":"90px","align":"center","formatFunc":"","editorProps":{"restrictValues":true},"mobileColumn":false},{"show":true,"field":"approaches","title":"Approaches","width":"90px","align":"center","formatFunc":"","editorProps":{"restrictValues":true},"mobileColumn":false},{"show":true,"field":"flows","title":"Flows","width":"90px","align":"center","formatFunc":"","editorProps":{"restrictValues":true},"mobileColumn":false},{"show":false,"field":"issue.project.tenantId","title":"Issue.project.tenantId","width":"100%","displayType":"Java.lang.Integer","align":"left","formatFunc":""}],"height":"100%","margin":"4","minDesktopHeight":60,"minWidth":100,"primaryKeyFields":["key"],"singleClickEdit":true}, {"onSelect":"summaryIssuesGrid1Select","onShow":"issuesByCategoryVariable1","onShow1":"summaryIssuesGrid1.deselectAll"}, {
									binding: ["wm.Binding", {}, {}, {
										wire: ["wm.Wire", {"expression":undefined,"source":"issuesByCategoryVariable1","targetProperty":"dataSet"}, {}]
									}]
								}],
								progressBar2: ["wm.dijit.ProgressBar", {"height":"24px","hint":"Percentage of issues with associated approaches","width":"100%"}, {}, {
									binding: ["wm.Binding", {}, {}, {
										wire: ["wm.Wire", {"expression":"${addressedIssues.count} * 100 / ${allIssues.count}","targetProperty":"progress"}, {}]
									}]
								}]
							}],
							layer5: ["wm.Layer", {"horizontalAlign":"left","themeStyleType":"ContentPanel","verticalAlign":"top"}, {}, {
								binding: ["wm.Binding", {}, {}, {
									wire: ["wm.Wire", {"expression":"\"Flows (\" + ${flowsByProjectVariable1.count} + \")\"","targetProperty":"caption"}, {}]
								}],
								html10: ["wm.Html", {"autoSizeHeight":true,"height":"48px","html":"<div class=\"note\">Review all current flows.</div>","minDesktopHeight":15}, {}],
								summaryFlowsGrid1: ["wm.DojoGrid", {"columns":[{"show":false,"field":"id","title":"Id","width":"80px","align":"right","formatFunc":"","mobileColumn":false},{"show":true,"field":"fromActor","title":"From","width":"50%","align":"left","formatFunc":"","editorProps":{"restrictValues":true},"expression":"if ( ${fromActor}  ) { ${fromActor} } else {  \" * unspecified *\"; }","mobileColumn":false},{"show":true,"field":"toActor","title":"To","width":"50%","align":"left","formatFunc":"","editorProps":{"restrictValues":true},"expression":"if ( ${toActor}  ) { ${toActor} } else {  \" * unspecified *\"; }","mobileColumn":false},{"show":true,"field":"name","title":"Subject","width":"100%","align":"left","formatFunc":"","editorProps":{"restrictValues":true},"mobileColumn":false},{"show":false,"field":"description","title":"Description","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"version","title":"Version","width":"80px","align":"right","formatFunc":"","mobileColumn":false},{"show":false,"field":"PHONE COLUMN","title":"-","width":"100%","align":"left","expression":"\"<div class='MobileRowTitle'>Subject: \" + ${name} + \"</div>\"\n","mobileColumn":true}],"height":"100%","margin":"4","minDesktopHeight":60,"primaryKeyFields":["id"],"singleClickEdit":true}, {"onSelect":"switchToFlow"}, {
									binding: ["wm.Binding", {}, {}, {
										wire: ["wm.Wire", {"expression":undefined,"source":"flowsByProjectVariable1","targetProperty":"dataSet"}, {}]
									}]
								}]
							}],
							layer3: ["wm.Layer", {"horizontalAlign":"left","themeStyleType":"ContentPanel","verticalAlign":"top"}, {}, {
								binding: ["wm.Binding", {}, {}, {
									wire: ["wm.Wire", {"expression":"\"Approaches (\" + ${approachIssueCountVariable1.count} + \")\"","targetProperty":"caption"}, {}]
								}],
								html11: ["wm.Html", {"autoSizeHeight":true,"height":"48px","html":"<div class=\"note\">Approach-centric view.</div>","minDesktopHeight":15}, {}],
								dojoGrid6: ["wm.DojoGrid", {"columns":[{"show":false,"field":"PHONE COLUMN","title":"-","width":"100%","align":"left","editorProps":{"restrictValues":true},"expression":"\"<div class='MobileRowTitle'>Approach: \" + ${approach.name} + \"</div>\"\n+ \"<div class='MobileRow'>Description: \" + ${approach.description} + \"</div>\"\n+ \"<div class='MobileRow'>Issues: \" + ${issues} + \"</div>\"\n+ \"<div class='MobileRow'>Start: \" + wm.List.prototype.dateFormatter({}, null,null,null,${approach.start}) + \"</div>\"\n+ \"<div class='MobileRow'>End: \" + wm.List.prototype.dateFormatter({}, null,null,null,${approach.end}) + \"</div>\"\n+ \"<div class='MobileRow'>Approved: \" + ${approach.approved} + \"</div>\"\n","mobileColumn":true},{"show":false,"field":"approach.id","title":"Approach.id","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"approach.interview.id","title":"Approach.interview.id","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"approach.interview.scheduled","title":"Approach.interview.scheduled","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"approach.interview.done","title":"Approach.interview.done","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"approach.interview.resource.id","title":"Approach.interview.resource.id","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"approach.interview.resource.project.id","title":"Approach.interview.resource.project.id","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"approach.interview.resource.project.description","title":"Approach.interview.resource.project.description","width":"100%","align":"left","formatFunc":"","editorProps":{"restrictValues":true},"mobileColumn":false},{"show":false,"field":"approach.interview.resource.project.name","title":"Approach.interview.resource.project.name","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"approach.interview.resource.project.version","title":"Approach.interview.resource.project.version","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"approach.interview.resource.description","title":"Approach.interview.resource.description","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"approach.interview.resource.name","title":"Approach.interview.resource.name","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"approach.interview.resource.version","title":"Approach.interview.resource.version","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"approach.interview.notes","title":"Approach.interview.notes","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"approach.interview.version","title":"Approach.interview.version","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":true,"field":"approach.name","title":"Approach","width":"50%","align":"left","formatFunc":"","editorProps":{"restrictValues":true},"mobileColumn":false},{"show":true,"field":"approach.description","title":"Description","width":"100%","align":"left","formatFunc":"","editorProps":{"restrictValues":true},"mobileColumn":false},{"show":true,"field":"issues","title":"Issues","width":"60px","align":"center","formatFunc":"","formatProps":null,"editorProps":{"restrictValues":true},"mobileColumn":false},{"show":false,"field":"approach.version","title":"Approach.version","width":"100%","align":"left","formatFunc":"","editorProps":{"restrictValues":true},"mobileColumn":false},{"show":true,"field":"approach.start","title":"Start","width":"60px","align":"left","formatFunc":"wm_date_formatter","editorProps":{"restrictValues":true},"mobileColumn":false},{"show":true,"field":"approach.end","title":"End","width":"60px","align":"left","formatFunc":"wm_date_formatter","editorProps":null,"mobileColumn":false},{"show":true,"field":"approach.cost","title":"Cost","width":"60px","align":"left","constraints":{"min":0},"editorProps":{"restrictValues":true},"expression":"if ( ${approach.cost} == null ) { \"\"; } else { ${approach.cost}; }","mobileColumn":false},{"show":true,"field":"approach.approved","title":"Approved","width":"70px","align":"left","formatFunc":"","fieldType":"dojox.grid.cells.Bool","editorProps":{"restrictValues":true},"mobileColumn":false},{"show":false,"field":"approach.interview.resource.project.tenantId","title":"Approach.interview.resource.project.tenantId","width":"100%","displayType":"Java.lang.Integer","align":"left","formatFunc":""}],"height":"100%","margin":"4","minDesktopHeight":60,"singleClickEdit":true}, {"onSelect":"switchToApproach"}, {
									binding: ["wm.Binding", {}, {}, {
										wire: ["wm.Wire", {"expression":undefined,"source":"approachIssueCountVariable1","targetProperty":"dataSet"}, {}]
									}]
								}]
							}]
						}]
					}],
					Edit_Solutions: ["wm.Layer", {"borderColor":"","caption":"Solutions","horizontalAlign":"left","themeStyleType":"ContentPanel","verticalAlign":"top"}, {"onShow":"planDojoGrid.deselectAll"}, {
						binding: ["wm.Binding", {}, {}, {
							wire: ["wm.Wire", {"expression":undefined,"source":"projectDojoGrid.isRowSelected","targetProperty":"showing"}, {}]
						}],
						html12: ["wm.Html", {"autoSizeHeight":true,"height":"48px","html":"<div class=\"note\">Pick or create an implementation plan based on an analysis.</div>","minDesktopHeight":15}, {}],
						analystDBLivePanel: ["wm.LivePanel", {"autoScroll":false,"horizontalAlign":"left","layoutKind":"left-to-right","verticalAlign":"top"}, {}, {
							binding: ["wm.Binding", {}, {}, {
								wire: ["wm.Wire", {"source":"Plan_List","targetId":null,"targetProperty":"gridLayer"}, {}],
								wire1: ["wm.Wire", {"source":"Edit_Plan","targetId":null,"targetProperty":"detailsLayer"}, {}],
								wire2: ["wm.Wire", {"source":"planLiveForm1","targetId":null,"targetProperty":"liveForm"}, {}],
								wire3: ["wm.Wire", {"source":"planDojoGrid","targetId":null,"targetProperty":"dataGrid"}, {}],
								wire4: ["wm.Wire", {"source":"planSaveButton","targetId":null,"targetProperty":"saveButton"}, {}]
							}],
							planDojoGrid: ["wm.DojoGrid", {"columns":[{"show":false,"field":"PHONE COLUMN","title":"-","width":"100%","align":"left","editorProps":{"restrictValues":true},"expression":"","mobileColumn":true},{"show":false,"field":"dataValue","title":"DataValue","width":"100%","displayType":"String","align":"left","formatFunc":""}],"height":"100%","margin":"4"}, {"onSelect":"approachLiveVariable1","onSelect1":"analystDBLivePanel.popupLivePanelEdit"}, {
								binding: ["wm.Binding", {}, {}, {
									wire: ["wm.Wire", {"expression":undefined,"source":"plansVariable1","targetProperty":"dataSet"}, {}]
								}]
							}],
							planNewButton: ["wm.Button", {"_classes":{"domNode":["iconButton"]},"caption":undefined,"imageIndex":1,"imageList":"app.silkIconList","margin":"4","width":"32px"}, {"onclick":"analystDBLivePanel.popupLivePanelInsert"}]
						}]
					}],
					Edit_Documents: ["wm.Layer", {"borderColor":"","caption":"Documents","horizontalAlign":"left","themeStyleType":"ContentPanel","verticalAlign":"top"}, {}, {
						documentsLivePanel1: ["wm.LivePanel", {"horizontalAlign":"left","verticalAlign":"top"}, {}, {
							binding: ["wm.Binding", {}, {}, {
								wire: ["wm.Wire", {"source":"documentsDojoGrid","targetId":null,"targetProperty":"dataGrid"}, {}]
							}],
							html5: ["wm.Html", {"autoSizeHeight":true,"height":"48px","html":"<div class=\"note\">Manage the list of global documents referred to by flows and issues.</div>","minDesktopHeight":15}, {}],
							analystDBLivePanel1: ["wm.LivePanel", {"horizontalAlign":"left","layoutKind":"left-to-right","verticalAlign":"top"}, {}, {
								binding: ["wm.Binding", {}, {}, {
									wire: ["wm.Wire", {"source":"documentsDojoGrid","targetId":null,"targetProperty":"dataGrid"}, {}]
								}],
								documentsDojoGrid: ["wm.DojoGrid", {"columns":[{"show":false,"field":"id","title":"Id","width":"80px","align":"right","formatFunc":"","mobileColumn":false},{"show":false,"field":"PHONE COLUMN","title":"-","width":"100%","align":"left","expression":"\"<div class='MobileRowTitle'>Document: \" + ${document} + \"</div>\"\n","mobileColumn":true},{"show":true,"field":"documentCategory.id","title":"Phase","width":"25%","align":"left","formatFunc":"","fieldType":"dojox.grid.cells.ComboBox","editorProps":{"selectDataSet":"documentcategoryLiveVariable1","displayField":"name","isSimpleType":false,"restrictValues":true},"expression":"${documentCategory.name}","mobileColumn":false},{"show":false,"field":"documentCategory.name","title":"DocumentCategory.name","width":"100%","align":"left","formatFunc":"","fieldType":"dojox.grid.cells.ComboBox","editorProps":{"restrictValues":true},"mobileColumn":false},{"show":true,"field":"document","title":"Document","width":"75%","align":"left","formatFunc":"","fieldType":"dojox.grid.cells._Widget","editorProps":{"restrictValues":true},"mobileColumn":false},{"show":false,"field":"tenantId","title":"TenantId","width":"80px","displayType":"Number","align":"right","formatFunc":""}],"deleteColumn":true,"dsType":"com.analystdb.data.Documents","height":"100%","liveEditing":true,"margin":"4","minWidth":100,"primaryKeyFields":["id"],"singleClickEdit":true}, {"onLiveEditBeforeUpdate":"documentsDojoGridLiveEditBeforeUpdate","onLiveEditDeleteSuccess":"documentsLiveVariable2","onLiveEditInsertSuccess":"documentsLiveVariable2","onLiveEditUpdateSuccess":"documentsLiveVariable2"}, {
									binding: ["wm.Binding", {}, {}, {
										wire: ["wm.Wire", {"source":"documentsLiveVariable2","targetProperty":"dataSet"}, {}]
									}]
								}],
								documentsNewButton1: ["wm.Button", {"_classes":{"domNode":["iconButton"]},"caption":undefined,"imageIndex":1,"imageList":"app.silkIconList","margin":"4","width":"32px"}, {"onclick":"documentsDojoGrid.addEmptyRow"}]
							}]
						}],
						button9: ["wm.Panel", {"fitToContentHeight":true,"height":"34px","horizontalAlign":"left","layoutKind":"left-to-right","verticalAlign":"top","width":"100%"}, {}, {
							button10: ["wm.Button", {"caption":"Phases","margin":"4"}, {"onclick":"Edit_Doc_Category"}],
							html13: ["wm.Html", {"autoSizeHeight":true,"height":"22px","html":"Manage document categories.","minDesktopHeight":15,"padding":"5,0,0"}, {}]
						}]
					}],
					Edit_Doc_Category: ["wm.Layer", {"borderColor":"","caption":"Phases","horizontalAlign":"left","themeStyleType":"ContentPanel","verticalAlign":"top"}, {}, {
						analystDBLivePanel2: ["wm.LivePanel", {"horizontalAlign":"left","layoutKind":"left-to-right","verticalAlign":"top"}, {}, {
							binding: ["wm.Binding", {}, {}, {
								wire: ["wm.Wire", {"source":"documentcategoryDojoGrid","targetId":null,"targetProperty":"dataGrid"}, {}]
							}],
							documentcategoryDojoGrid: ["wm.DojoGrid", {"columns":[{"show":true,"field":"name","title":"Name","width":"100%","align":"left","formatFunc":"","fieldType":"dojox.grid.cells._Widget","editorProps":{"restrictValues":true},"mobileColumn":false},{"show":false,"field":"PHONE COLUMN","title":"-","width":"100%","align":"left","expression":"\"<div class='MobileRowTitle'>Name: \" + ${name} + \"</div>\"\n+ \"<div class='MobileRow'>Sequence: \" + ${sequence} + \"</div>\"\n","mobileColumn":true},{"show":false,"field":"id","title":"Id","width":"80px","align":"right","formatFunc":"","mobileColumn":false},{"show":true,"field":"sequence","title":"Sequence","width":"70px","align":"center","formatFunc":"","mobileColumn":false},{"show":false,"field":"tenantId","title":"TenantId","width":"80px","align":"right","formatFunc":"","mobileColumn":false}],"deleteColumn":true,"dsType":"com.analystdb.data.DocumentCategory","height":"100%","liveEditing":true,"margin":"4","singleClickEdit":true}, {"onLiveEditBeforeUpdate":"documentcategoryDojoGridLiveEditBeforeUpdate","onLiveEditDeleteSuccess":"documentsLiveVariable2","onLiveEditInsertSuccess":"documentsLiveVariable2","onLiveEditUpdateSuccess":"documentsLiveVariable2"}, {
								binding: ["wm.Binding", {}, {}, {
									wire: ["wm.Wire", {"source":"documentcategoryLiveVariable1","targetProperty":"dataSet"}, {}]
								}]
							}],
							documentcategoryNewButton: ["wm.Button", {"_classes":{"domNode":["iconButton"]},"caption":undefined,"imageIndex":1,"imageList":"app.silkIconList","margin":"4","width":"32px"}, {"onclick":"documentcategoryDojoGrid.addEmptyRow"}]
						}]
					}],
					Edit_Interview: ["wm.Layer", {"borderColor":"","horizontalAlign":"left","themeStyleType":"ContentPanel","verticalAlign":"top"}, {"onShow":"flowDojoGrid.deselectAll","onShow1":"dataGrid2.deselectAll","onShow2":"approachDojoGrid.deselectAll","onShow3":"resourceattributeGrid.deselectAll"}, {
						binding: ["wm.Binding", {}, {}, {
							wire1: ["wm.Wire", {"expression":undefined,"source":"interviewDojoGrid1.isRowSelected","targetProperty":"showing"}, {}],
							wire: ["wm.Wire", {"expression":undefined,"source":"nameEditor6.dataValue","targetProperty":"caption"}, {}]
						}],
						interviewForm1: ["wm.LiveForm", {"height":"100%","horizontalAlign":"left","liveEditing":false,"operation":"update","saveOnEnterKey":false,"verticalAlign":"top"}, {"onSuccess":"recentInterviewsVariable1"}, {
							binding: ["wm.Binding", {}, {}, {
								wire: ["wm.Wire", {"expression":undefined,"source":"interviewDojoGrid1.selectedItem.interview","targetProperty":"dataSet"}, {}],
								wire4: ["wm.Wire", {"expression":undefined,"source":"relatedEditor1.dataOutput","targetProperty":"dataOutput.resource"}, {}],
								wire5: ["wm.Wire", {"expression":undefined,"source":"interviewDojoGrid1.selectedItem.interview.id","targetProperty":"dataOutput.id"}, {}],
								wire6: ["wm.Wire", {"expression":undefined,"source":"interviewDojoGrid1.selectedItem.interview.scheduled","targetProperty":"dataOutput.scheduled"}, {}],
								wire7: ["wm.Wire", {"expression":undefined,"source":"interviewDojoGrid1.selectedItem.interview.done","targetProperty":"dataOutput.done"}, {}]
							}],
							calendar1Panel: ["wm.Panel", {"height":"172px","horizontalAlign":"left","layoutKind":"left-to-right","padding":"5","verticalAlign":"top","width":"100%"}, {}, {
								relatedEditor1: ["wm.RelatedEditor", {"editingMode":"editable subform","formField":"resource","height":"100%","horizontalAlign":"left","verticalAlign":"top"}, {}, {
									binding: ["wm.Binding", {}, {}, {
										wire: ["wm.Wire", {"expression":undefined,"source":"interviewDojoGrid1.selectedItem.interview.resource","targetProperty":"dataSet"}, {}]
									}],
									nameEditor6: ["wm.Text", {"border":"0","caption":"Resource","captionAlign":"left","captionSize":"80px","dataValue":"","desktopHeight":"26px","emptyValue":"emptyString","formField":"name","height":"26px","invalidMessage":"Name must start with a capital letter","maxChars":"127","regExp":"[A-Z].*","required":true,"width":"100%"}, {"onEnterKeyPress":"interviewForm1.saveData"}],
									resourceattributeLivePanel1: ["wm.LivePanel", {"horizontalAlign":"left","layoutKind":"left-to-right","verticalAlign":"top"}, {}, {
										binding: ["wm.Binding", {}, {}, {
											wire: ["wm.Wire", {"source":"resourceattributeDojoGrid","targetId":null,"targetProperty":"dataGrid"}, {}]
										}],
										resourceattributeDojoGrid: ["wm.DojoGrid", {"columns":[{"show":false,"field":"id","title":"Id","width":"80px","align":"right","formatFunc":"","editorProps":{"restrictValues":true},"mobileColumn":false},{"show":true,"field":"name","title":"Name","width":"25%","align":"left","formatFunc":"","fieldType":"dojox.grid.cells._Widget","editorProps":{"restrictValues":true},"mobileColumn":false},{"show":true,"field":"val","title":"Val","width":"100%","align":"left","formatFunc":"","fieldType":"dojox.grid.cells._Widget","mobileColumn":false},{"show":false,"field":"PHONE COLUMN","title":"-","width":"100%","align":"left","expression":"\"<div class='MobileRowTitle'>Name: \" + ${name} + \"</div>\"\n+ \"<div class='MobileRow'>Val: \" + ${val} + \"</div>\"\n","mobileColumn":true},{"show":false,"field":"version","title":"Version","width":"80px","align":"right","formatFunc":"","mobileColumn":false}],"dsType":"com.analystdb.data.ResourceAttribute","height":"100%","liveEditing":true,"margin":"4","noHeader":true,"primaryKeyFields":["id"]}, {"onDeselect":"button12.disable","onSelect":"button12.enable"}, {
											binding: ["wm.Binding", {}, {}, {
												wire: ["wm.Wire", {"source":"resourceattributeLiveVariable1","targetProperty":"dataSet"}, {}]
											}]
										}],
										resourceattributeGridButtonPanel: ["wm.Panel", {"desktopHeight":"32px","enableTouchHeight":true,"fitToContentHeight":true,"fitToContentWidth":true,"height":"66px","horizontalAlign":"right","mobileHeight":"40px","verticalAlign":"top","width":"34px"}, {}, {
											resourceattributeNewButton: ["wm.Button", {"_classes":{"domNode":["iconButton"]},"caption":undefined,"imageIndex":1,"imageList":"app.silkIconList","margin":"4","width":"32px"}, {"onclick1":"resourceattributeNewButtonClick1"}],
											button12: ["wm.Button", {"_classes":{"domNode":["iconButton"]},"caption":undefined,"disabled":true,"hint":"Remove property","imageIndex":21,"imageList":"app.silkIconList","margin":"4","width":"32px"}, {"onclick":"button12Click"}]
										}]
									}]
								}],
								calendar1: ["wm.dijit.Calendar", {"width":"240px"}, {}, {
									binding: ["wm.Binding", {}, {}, {
										wire: ["wm.Wire", {"expression":undefined,"source":"interviewDojoGrid1.selectedItem.interview.scheduled","targetProperty":"dateValue"}, {}]
									}]
								}]
							}],
							tabLayers3: ["wm.TabLayers", {}, {}, {
								layer6: ["wm.Layer", {"horizontalAlign":"left","themeStyleType":"ContentPanel","verticalAlign":"top"}, {}, {
									binding: ["wm.Binding", {}, {}, {
										wire: ["wm.Wire", {"expression":"if ( ${dataGrid2.dataSet.data} ) { \"Issues  (\" + ${dataGrid2.dataSet.data.length} + \")\"; } else { \"Issues\"; }","targetProperty":"caption"}, {}]
									}],
									issuecommentLivePanel1: ["wm.LivePanel", {"autoScroll":false,"horizontalAlign":"left","layoutKind":"left-to-right","verticalAlign":"top"}, {}, {
										binding: ["wm.Binding", {}, {}, {
											wire: ["wm.Wire", {"source":"Issuecomment_List","targetId":null,"targetProperty":"gridLayer"}, {}],
											wire1: ["wm.Wire", {"source":"Edit_Issuecomment","targetId":null,"targetProperty":"detailsLayer"}, {}],
											wire2: ["wm.Wire", {"source":"issuecommentLiveForm1","targetId":null,"targetProperty":"liveForm"}, {}],
											wire3: ["wm.Wire", {"source":"dataGrid2","targetId":null,"targetProperty":"dataGrid"}, {}],
											wire4: ["wm.Wire", {"source":"issuecommentSaveButton","targetId":null,"targetProperty":"saveButton"}, {}]
										}],
										dataGrid2: ["wm.DojoGrid", {"columns":[{"show":false,"field":"id","title":"Issue","width":"50px","align":"center","formatFunc":"","editorProps":null,"expression":"\"I-\" + ${id}","mobileColumn":false},{"show":true,"field":"issue.sequence","title":"Issue","width":"50px","align":"center","formatFunc":"","editorProps":{"restrictValues":true},"expression":"\"I-\" + ${issue.sequence}","mobileColumn":false},{"show":true,"field":"description","title":"Description","width":"100%","align":"left","formatFunc":"","editorProps":{"restrictValues":true},"mobileColumn":false},{"show":true,"field":"applicableToMe","title":"Applies","width":"50px","align":"center","formatFunc":"","editorProps":{"restrictValues":true},"mobileColumn":false},{"show":true,"field":"fixed","title":"Fixed","width":"50px","align":"center","formatFunc":"","editorProps":{"restrictValues":true},"mobileColumn":false},{"show":false,"field":"PHONE COLUMN","title":"-","width":"100%","align":"left","expression":"\"<div class='MobileRowTitle'>Description: \" + ${description} + \"</div>\"\n+ \"<div class='MobileRow'>Applies: \" + ${applicableToMe} + \"</div>\"\n+ \"<div class='MobileRow'>Fixed: \" + ${fixed} + \"</div>\"\n","mobileColumn":true},{"show":false,"field":"version","title":"Version","width":"80px","align":"right","formatFunc":"","mobileColumn":false},{"show":false,"field":"issue.id","title":"Issue.id","width":"80px","align":"right","formatFunc":"","mobileColumn":false},{"show":false,"field":"interview.id","title":"Interview.id","width":"80px","displayType":"Number","align":"right","formatFunc":""},{"show":false,"field":"interview.scheduled","title":"Interview.scheduled","width":"80px","displayType":"Date","align":"left","formatFunc":"wm_date_formatter"},{"show":false,"field":"interview.notes","title":"Interview.notes","width":"100%","displayType":"Text","align":"left","formatFunc":""},{"show":false,"field":"interview.done","title":"Interview.done","width":"100%","displayType":"CheckBox","align":"left","formatFunc":""},{"show":false,"field":"interview.version","title":"Interview.version","width":"80px","displayType":"Number","align":"right","formatFunc":""}],"height":"100%","margin":"4","primaryKeyFields":["id"]}, {"onSelect":"showComment"}, {
											binding: ["wm.Binding", {}, {}, {
												wire: ["wm.Wire", {"expression":undefined,"source":"interviewIssuesVariable1","targetProperty":"dataSet"}, {}]
											}]
										}],
										issuecommentNewButton: ["wm.Button", {"_classes":{"domNode":["iconButton"]},"caption":undefined,"imageIndex":1,"imageList":"app.silkIconList","margin":"4","width":"32px"}, {"onclick":"issuecommentLivePanel1.popupLivePanelInsert"}]
									}]
								}],
								layer7: ["wm.Layer", {"horizontalAlign":"left","layoutKind":"left-to-right","themeStyleType":"ContentPanel","verticalAlign":"top"}, {}, {
									binding: ["wm.Binding", {}, {}, {
										wire: ["wm.Wire", {"expression":"if ( ${flowDojoGrid.dataSet.data} ) { \"Flows  (\" + ${flowDojoGrid.dataSet.data.length} + \")\"; } else { \"Flows\"; }","targetProperty":"caption"}, {}]
									}],
									flowLivePanel1: ["wm.LivePanel", {"autoScroll":false,"horizontalAlign":"left","layoutKind":"left-to-right","verticalAlign":"top"}, {}, {
										binding: ["wm.Binding", {}, {}, {
											wire: ["wm.Wire", {"source":"Flow_List","targetId":null,"targetProperty":"gridLayer"}, {}],
											wire1: ["wm.Wire", {"source":"Edit_Flow","targetId":null,"targetProperty":"detailsLayer"}, {}],
											wire2: ["wm.Wire", {"source":"flowLiveForm1","targetId":null,"targetProperty":"liveForm"}, {}],
											wire3: ["wm.Wire", {"source":"flowDojoGrid","targetId":null,"targetProperty":"dataGrid"}, {}],
											wire4: ["wm.Wire", {"source":"flowSaveButton","targetId":null,"targetProperty":"saveButton"}, {}]
										}],
										flowDojoGrid: ["wm.DojoGrid", {"columns":[{"show":false,"field":"id","title":"Id","width":"80px","align":"right","formatFunc":"","mobileColumn":false},{"show":true,"field":"fromActor","title":"From","width":"50%","align":"left","formatFunc":"","editorProps":{"restrictValues":true},"mobileColumn":false},{"show":true,"field":"toActor","title":"To","width":"50%","align":"left","formatFunc":"","editorProps":{"restrictValues":true},"mobileColumn":false},{"show":true,"field":"name","title":"Subject","width":"100%","align":"left","formatFunc":"","editorProps":{"restrictValues":true},"mobileColumn":false},{"show":false,"field":"description","title":"Description","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"PHONE COLUMN","title":"-","width":"100%","align":"left","expression":"\"<div class='MobileRowTitle'>From: \" + ${fromActor} + \"</div>\"\n+ \"<div class='MobileRow'>To: \" + ${toActor} + \"</div>\"\n+ \"<div class='MobileRow'>Subject: \" + ${name} + \"</div>\"\n","mobileColumn":true},{"show":false,"field":"interview.id","title":"Interview.id","width":"80px","displayType":"Number","align":"right","formatFunc":""},{"show":false,"field":"interview.scheduled","title":"Interview.scheduled","width":"80px","displayType":"Date","align":"left","formatFunc":"wm_date_formatter"},{"show":false,"field":"interview.notes","title":"Interview.notes","width":"100%","displayType":"Text","align":"left","formatFunc":""},{"show":false,"field":"interview.done","title":"Interview.done","width":"100%","displayType":"CheckBox","align":"left","formatFunc":""},{"show":false,"field":"interview.version","title":"Interview.version","width":"80px","displayType":"Number","align":"right","formatFunc":""},{"show":false,"field":"documents.id","title":"Documents.id","width":"80px","displayType":"Number","align":"right","formatFunc":""},{"show":false,"field":"documents.document","title":"Documents.document","width":"100%","displayType":"Text","align":"left","formatFunc":""},{"show":false,"field":"version","title":"Version","width":"80px","displayType":"Number","align":"right","formatFunc":""}],"height":"100%","liveEditing":true,"margin":"4","minWidth":0,"primaryKeyFields":["id"]}, {"onSelect":"flowIssuesVariable","onSelect1":"flowLivePanel1.popupLivePanelEdit"}, {
											binding: ["wm.Binding", {}, {}, {
												wire: ["wm.Wire", {"expression":undefined,"source":"interviewFlowsVariable1","targetProperty":"dataSet"}, {}]
											}]
										}],
										flowNewButton: ["wm.Button", {"_classes":{"domNode":["iconButton"]},"caption":undefined,"imageIndex":1,"imageList":"app.silkIconList","margin":"4","width":"32px"}, {"onclick":"flowLivePanel1.popupLivePanelInsert"}]
									}]
								}],
								layer8: ["wm.Layer", {"horizontalAlign":"left","themeStyleType":"ContentPanel","verticalAlign":"top"}, {}, {
									binding: ["wm.Binding", {}, {}, {
										wire: ["wm.Wire", {"expression":"if ( ${approachDojoGrid.dataSet.data} ) { \"Approaches  (\" + ${approachDojoGrid.dataSet.data.length} + \")\"; } else { \"Approaches\"; }","targetProperty":"caption"}, {}]
									}],
									approachLivePanel1: ["wm.LivePanel", {"autoScroll":false,"horizontalAlign":"left","layoutKind":"left-to-right","verticalAlign":"top"}, {}, {
										binding: ["wm.Binding", {}, {}, {
											wire: ["wm.Wire", {"source":"Approach_List","targetId":null,"targetProperty":"gridLayer"}, {}],
											wire1: ["wm.Wire", {"source":"Edit_Approach","targetId":null,"targetProperty":"detailsLayer"}, {}],
											wire2: ["wm.Wire", {"source":"approachLiveForm1","targetId":null,"targetProperty":"liveForm"}, {}],
											wire3: ["wm.Wire", {"source":"approachDojoGrid","targetId":null,"targetProperty":"dataGrid"}, {}],
											wire4: ["wm.Wire", {"source":"approachSaveButton","targetId":null,"targetProperty":"saveButton"}, {}]
										}],
										approachDojoGrid: ["wm.DojoGrid", {"columns":[{"show":false,"field":"id","title":"Id","width":"80px","align":"right","formatFunc":"","mobileColumn":false},{"show":true,"field":"name","title":"Name","width":"40%","align":"left","formatFunc":"","editorProps":{"restrictValues":true},"mobileColumn":false},{"show":true,"field":"description","title":"Description","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"PHONE COLUMN","title":"-","width":"100%","align":"left","expression":"\"<div class='MobileRowTitle'>Name: \" + ${name} + \"</div>\"\n+ \"<div class='MobileRow'>Description: \" + ${description} + \"</div>\"\n","mobileColumn":true},{"show":false,"field":"version","title":"Version","width":"80px","displayType":"Number","align":"right","formatFunc":""},{"show":false,"field":"interview.id","title":"Interview.id","width":"80px","displayType":"Number","align":"right","formatFunc":""},{"show":false,"field":"interview.scheduled","title":"Interview.scheduled","width":"80px","displayType":"Date","align":"left","formatFunc":"wm_date_formatter"},{"show":false,"field":"interview.notes","title":"Interview.notes","width":"100%","displayType":"Text","align":"left","formatFunc":""},{"show":false,"field":"interview.done","title":"Interview.done","width":"100%","displayType":"CheckBox","align":"left","formatFunc":""},{"show":false,"field":"interview.version","title":"Interview.version","width":"80px","displayType":"Number","align":"right","formatFunc":""},{"show":false,"field":"start","title":"Start","width":"80px","displayType":"Date","align":"left","formatFunc":"wm_date_formatter"},{"show":false,"field":"end","title":"End","width":"80px","displayType":"Date","align":"left","formatFunc":"wm_date_formatter"},{"show":false,"field":"approved","title":"Approved","width":"100%","displayType":"CheckBox","align":"left","formatFunc":""},{"show":false,"field":"cost","title":"Cost","width":"80px","displayType":"Number","align":"right","formatFunc":""}],"height":"100%","margin":"4","primaryKeyFields":["id"]}, {"onSelect":"approachIssuesVariable","onSelect1":"approachLivePanel1.popupLivePanelEdit"}, {
											binding: ["wm.Binding", {}, {}, {
												wire: ["wm.Wire", {"expression":undefined,"source":"interviewApproachesVariable1","targetProperty":"dataSet"}, {}]
											}]
										}],
										approachNewButton: ["wm.Button", {"_classes":{"domNode":["iconButton"]},"caption":undefined,"imageIndex":1,"imageList":"app.silkIconList","margin":"4","width":"32px"}, {"onclick":"approachLivePanel1.popupLivePanelInsert"}]
									}]
								}]
							}]
						}]
					}],
					Edit_Plan: ["wm.Layer", {"autoScroll":true,"borderColor":"","horizontalAlign":"left","themeStyleType":"ContentPanel","verticalAlign":"top"}, {}, {
						binding: ["wm.Binding", {}, {}, {
							wire: ["wm.Wire", {"expression":undefined,"source":"nameEditor3.dataValue","targetProperty":"caption"}, {}],
							wire1: ["wm.Wire", {"expression":undefined,"source":"planDojoGrid.isRowSelected","targetProperty":"showing"}, {}]
						}],
						planLiveForm1: ["wm.LiveForm", {"alwaysPopulateEditors":true,"fitToContentHeight":true,"height":"140px","horizontalAlign":"left","liveEditing":false,"margin":"4","verticalAlign":"top"}, {"onDeleteData":"Edit_Solutions","onSuccess":"analystDBLivePanel.popupLiveFormSuccess"}, {
							binding: ["wm.Binding", {}, {}, {
								wire1: ["wm.Wire", {"expression":undefined,"source":"analysisDojoGrid.selectedItem","targetProperty":"dataOutput.analysis"}, {}],
								wire: ["wm.Wire", {"expression":undefined,"source":"planDojoGrid.selectedItem.plan","targetProperty":"dataSet"}, {}]
							}],
							nameEditor3Panel: ["wm.Panel", {"height":"34px","horizontalAlign":"left","layoutKind":"left-to-right","verticalAlign":"middle","width":"100%"}, {}, {
								nameEditor3: ["wm.Text", {"border":"0","caption":"Name","captionSize":"140px","changeOnKey":true,"dataValue":"","desktopHeight":"26px","emptyValue":"emptyString","formField":"name","height":"26px","required":true,"width":"100%"}, {}],
								planDeleteButton: ["wm.Button", {"caption":"Delete","imageIndex":21,"imageList":"app.silkIconList","margin":"4"}, {"onclick":"planLiveForm1.deleteData"}, {
									binding: ["wm.Binding", {}, {}, {
										wire: ["wm.Wire", {"source":"planDojoGrid.emptySelection","targetId":null,"targetProperty":"disabled"}, {}]
									}]
								}],
								planSaveButton: ["wm.Button", {"caption":"Save","imageIndex":0,"imageList":"app.silkIconList","margin":"4"}, {"onclick":"planLiveForm1.saveDataIfValid"}, {
									binding: ["wm.Binding", {}, {}, {
										wire: ["wm.Wire", {"expression":"${planLiveForm1.invalid} || !${planLiveForm1.isDirty}","targetId":null,"targetProperty":"disabled"}, {}]
									}]
								}]
							}],
							descriptionEditor3Panel: ["wm.Panel", {"height":"96px","horizontalAlign":"left","layoutKind":"left-to-right","verticalAlign":"top","width":"100%"}, {}, {
								descriptionEditor3: ["wm.LargeTextArea", {"border":"0","caption":"Description","captionAlign":"right","captionPosition":"left","captionSize":"140px","changeOnKey":true,"dataValue":"","emptyValue":"emptyString","formField":"description","width":"100%"}, {}],
								approvedEditor1: ["wm.Checkbox", {"caption":"Approved","captionSize":"140px","desktopHeight":"26px","displayValue":false,"formField":"approved","height":"26px","width":"159px"}, {}]
							}]
						}],
						tabLayers4: ["wm.TabLayers", {}, {}, {
							layer10: ["wm.Layer", {"caption":"Approaches","horizontalAlign":"left","themeStyleType":"ContentPanel","verticalAlign":"top"}, {}, {
								html15: ["wm.Html", {"autoSizeHeight":true,"height":"48px","html":"<div class=\"note\">Pick the approaches to be included in this solution.</div>","minDesktopHeight":15}, {}],
								dojoGrid9: ["wm.DojoGrid", {"columns":[{"show":false,"field":"id","title":"Id","width":"80px","align":"right","formatFunc":"","mobileColumn":false},{"show":true,"field":"name","title":"Approach","width":"25%","align":"left","formatFunc":"","editorProps":{"restrictValues":true},"mobileColumn":false},{"show":true,"field":"description","title":"Description","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"version","title":"Version","width":"80px","align":"right","formatFunc":"","mobileColumn":false},{"show":false,"field":"interview.id","title":"Id","width":"80px","align":"right","formatFunc":"","mobileColumn":false},{"show":false,"field":"interview.scheduled","title":"Scheduled","width":"80px","align":"left","formatFunc":"wm_date_formatter","mobileColumn":false},{"show":false,"field":"interview.notes","title":"Notes","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"interview.done","title":"Done","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"interview.version","title":"Version","width":"80px","align":"right","formatFunc":"","mobileColumn":false},{"show":false,"field":"PHONE COLUMN","title":"-","width":"100%","align":"left","expression":"\"<div class='MobileRowTitle'>Approach: \" + ${name} + \"</div>\"\n+ \"<div class='MobileRow'>Description: \" + ${description} + \"</div>\"\n","mobileColumn":true},{"show":false,"field":"start","title":"Start","width":"80px","displayType":"Date","align":"left","formatFunc":"wm_date_formatter"},{"show":false,"field":"end","title":"End","width":"80px","displayType":"Date","align":"left","formatFunc":"wm_date_formatter"},{"show":false,"field":"approved","title":"Approved","width":"100%","displayType":"CheckBox","align":"left","formatFunc":""},{"show":false,"field":"cost","title":"Cost","width":"80px","displayType":"Number","align":"right","formatFunc":""}],"dsType":"com.analystdb.data.Approach","height":"100%","margin":"4","minDesktopHeight":60,"selectionMode":"checkbox","singleClickEdit":true}, {}, {
									binding: ["wm.Binding", {}, {}, {
										wire: ["wm.Wire", {"expression":undefined,"source":"approachLiveVariable1","targetProperty":"dataSet"}, {}]
									}]
								}]
							}],
							layer11: ["wm.Layer", {"caption":"Timings and Costs","horizontalAlign":"left","themeStyleType":"ContentPanel","verticalAlign":"top"}, {}, {
								html16: ["wm.Html", {"autoSizeHeight":true,"height":"48px","html":"<div class=\"note\">Enter some specifics for each approaches.</div>","minDesktopHeight":15}, {}],
								costGrid: ["wm.DojoGrid", {"columns":[{"show":false,"field":"PHONE COLUMN","title":"-","width":"100%","align":"left","editorProps":{"restrictValues":true},"expression":"\"<div class='MobileRowTitle'>Start: \" + wm.List.prototype.dateFormatter({}, null,null,null,${Start}) + \"</div>\"\n+ \"<div class='MobileRow'>End: \" + wm.List.prototype.dateFormatter({}, null,null,null,${End}) + \"</div>\"\n+ \"<div class='MobileRow'>Cost: \" + wm.List.prototype.currencyFormatter({\"currency\":\"USD\"}, null,null,null,${Cost}) + \"</div>\"\n","mobileColumn":true},{"show":true,"field":"Start","title":"Start","width":"25%","align":"left","formatFunc":"wm_date_formatter","fieldType":"dojox.grid.cells.DateTextBox","editorProps":{"restrictValues":true},"isCustomField":true,"mobileColumn":false},{"show":true,"field":"End","title":"End","width":"25%","align":"left","formatFunc":"wm_date_formatter","fieldType":"dojox.grid.cells.DateTextBox","editorProps":{"restrictValues":true},"isCustomField":true,"mobileColumn":false},{"show":true,"field":"Cost","title":"Cost","width":"25%","align":"left","formatFunc":"wm_currency_formatter","formatProps":{"currency":"USD"},"fieldType":"dojox.grid.cells.NumberTextBox","constraints":{"min":0},"editorProps":{"restrictValues":true},"isCustomField":true,"mobileColumn":false},{"show":false,"field":"id","title":"Id","width":"80px","displayType":"Number","align":"right","formatFunc":""},{"show":false,"field":"name","title":"Name","width":"100%","displayType":"Text","align":"left","formatFunc":""},{"show":false,"field":"description","title":"Description","width":"100%","displayType":"Text","align":"left","formatFunc":""},{"show":false,"field":"version","title":"Version","width":"80px","displayType":"Number","align":"right","formatFunc":""},{"show":false,"field":"start","title":"Start","width":"80px","displayType":"Date","align":"left","formatFunc":"wm_date_formatter"},{"show":false,"field":"end","title":"End","width":"80px","displayType":"Date","align":"left","formatFunc":"wm_date_formatter"},{"show":false,"field":"approved","title":"Approved","width":"100%","displayType":"CheckBox","align":"left","formatFunc":""},{"show":false,"field":"cost","title":"Cost","width":"80px","displayType":"Number","align":"right","formatFunc":""}],"dsType":"com.analystdb.data.Approach","height":"100%","margin":"4","minDesktopHeight":60,"singleClickEdit":true}, {}, {
									binding: ["wm.Binding", {}, {}, {
										wire: ["wm.Wire", {"expression":undefined,"source":"dojoGrid9.selectedItem","targetProperty":"dataSet"}, {}]
									}]
								}]
							}],
							layer12: ["wm.Layer", {"caption":"Timeline","horizontalAlign":"left","themeStyleType":"ContentPanel","verticalAlign":"top"}, {}, {
								html17: ["wm.Html", {"autoSizeHeight":true,"height":"48px","html":"<div class=\"note\">Here will be a Gantt chart of the previously selected data.</div>","minDesktopHeight":15}, {}]
							}]
						}]
					}],
					Edit_Issuecomment: ["wm.Layer", {"autoScroll":true,"borderColor":"","horizontalAlign":"left","themeStyleType":"ContentPanel","verticalAlign":"top"}, {"onShow":"dojoGrid7.deselectAll","onShow1":"dojoGrid5.deselectAll","onShow2":"issueattributeDojoGrid.deselectAll"}, {
						binding: ["wm.Binding", {}, {}, {
							wire: ["wm.Wire", {"expression":undefined,"source":"dataGrid2.isRowSelected","targetProperty":"showing"}, {}],
							wire1: ["wm.Wire", {"expression":"if ( ${dataGrid2.isRowSelected} ) {\n  \"Comment on I-\" + ${dataGrid2.selectedItem.issue.sequence};\n} else {\n  \"(New Issue)\";\n}","targetProperty":"caption"}, {}]
						}],
						issuecommentLiveForm1: ["wm.LiveForm", {"alwaysPopulateEditors":true,"height":"100%","horizontalAlign":"left","liveEditing":false,"margin":"4","verticalAlign":"top"}, {"onSuccess":"interviewIssuesVariable1","onSuccess1":"Edit_Interview"}, {
							binding: ["wm.Binding", {}, {}, {
								wire: ["wm.Wire", {"expression":undefined,"source":"dataGrid2.selectedItem","targetProperty":"dataSet"}, {}],
								wire1: ["wm.Wire", {"expression":undefined,"source":"relatedEditor2.dataOutput","targetProperty":"dataOutput.issue"}, {}]
							}],
							text1Panel: ["wm.Panel", {"fitToContentHeight":true,"height":"36px","horizontalAlign":"left","layoutKind":"left-to-right","verticalAlign":"middle","width":"100%"}, {}, {
								relatedEditor2: ["wm.RelatedEditor", {"fitToContentHeight":true,"formField":"issue","height":"34px","horizontalAlign":"left","layoutKind":"left-to-right","verticalAlign":"middle"}, {}, {
									binding: ["wm.Binding", {}, {}, {
										wire: ["wm.Wire", {"expression":undefined,"source":"dataGrid2.selectedItem.issue","targetProperty":"dataSet"}, {}],
										wire1: ["wm.Wire", {"expression":undefined,"source":"issueLookup1.selectedItem","targetProperty":"dataOutput"}, {}],
										wire2: ["wm.Wire", {"expression":undefined,"source":"dataGrid2.isRowSelected","targetProperty":"showing"}, {}]
									}],
									issueLookup1: ["wm.Lookup", {"autoDataSet":false,"caption":"Issue","captionSize":"140px","dataType":"com.analystdb.data.Issue","dataValue":undefined,"defaultInsert":"0","desktopHeight":"26px","displayExpression":"\"I-\" + ${sequence}","displayField":"sequence","displayValue":"","formField":"","height":"26px","required":true,"width":"100%"}, {}, {
										binding: ["wm.Binding", {}, {}, {
											dataFieldWire: ["wm.Wire", {"source":"issueLookup1.liveVariable","targetProperty":"dataSet"}, {}],
											wire: ["wm.Wire", {"expression":undefined,"source":"projectIssues","targetProperty":"dataSet"}, {}]
										}]
									}],
									button19: ["wm.Button", {"_classes":{"domNode":["iconButton"]},"caption":undefined,"hint":"See issue ","imageIndex":66,"imageList":"app.silkIconList","margin":"4","width":"32px"}, {"onclick":"button19Click"}]
								}],
								issuecommentFormButtonPanel: ["wm.Panel", {"desktopHeight":"34px","enableTouchHeight":true,"height":"34px","horizontalAlign":"right","layoutKind":"left-to-right","mobileHeight":"40px","verticalAlign":"middle","width":"100%"}, {}, {
									button11: ["wm.Button", {"caption":"Cancel","imageIndex":21,"imageList":"app.silkIconList","margin":"4"}, {"onclick":"Edit_Interview"}, {
										binding: ["wm.Binding", {}, {}, {
											wire: ["wm.Wire", {"expression":undefined,"source":"dataGrid2.emptySelection","targetProperty":"showing"}, {}]
										}]
									}],
									issuecommentDeleteButton: ["wm.Button", {"caption":"Delete","imageIndex":21,"imageList":"app.silkIconList","margin":"4"}, {"onclick":"issuecommentLiveForm1.deleteData"}, {
										binding: ["wm.Binding", {}, {}, {
											wire: ["wm.Wire", {"expression":undefined,"source":"dataGrid2.isRowSelected","targetProperty":"showing"}, {}]
										}]
									}],
									issuecommentSaveButton: ["wm.Button", {"caption":"Save","imageIndex":0,"imageList":"app.silkIconList","margin":"4"}, {"onclick":"issuecommentSaveButtonClick"}, {
										binding: ["wm.Binding", {}, {}, {
											wire: ["wm.Wire", {"expression":"!${issuecommentLiveForm1.isDirty}","targetProperty":"disabled"}, {}]
										}]
									}]
								}]
							}],
							descriptionEditor2: ["wm.LargeTextArea", {"border":"0","caption":"Comment","captionAlign":"right","captionPosition":"left","captionSize":"140px","changeOnKey":true,"dataValue":"","emptyValue":"emptyString","formField":"description","height":"50%","width":"100%"}, {}],
							applicableToMeEditor1Panel: ["wm.Panel", {"height":"26px","horizontalAlign":"left","layoutKind":"left-to-right","verticalAlign":"top","width":"100%"}, {}, {
								binding: ["wm.Binding", {}, {}, {
									wire: ["wm.Wire", {"expression":undefined,"source":"dataGrid2.isRowSelected","targetProperty":"showing"}, {}]
								}],
								applicableToMeEditor1: ["wm.Checkbox", {"caption":"Applies","captionSize":"140px","defaultInsert":true,"desktopHeight":"26px","displayValue":false,"formField":"applicableToMe","height":"26px","width":"100%"}, {}],
								fixedEditor1: ["wm.Checkbox", {"caption":"Fixed","captionSize":"140px","desktopHeight":"26px","displayValue":false,"formField":"fixed","height":"26px","width":"100%"}, {}]
							}],
							tabLayers5: ["wm.TabLayers", {}, {}, {
								binding: ["wm.Binding", {}, {}, {
									wire: ["wm.Wire", {"expression":undefined,"source":"dataGrid2.isRowSelected","targetProperty":"showing"}, {}]
								}],
								layer13: ["wm.Layer", {"caption":"Affected Flows","horizontalAlign":"left","layoutKind":"left-to-right","themeStyleType":"ContentPanel","verticalAlign":"top"}, {}, {
									dojoGrid7: ["wm.DojoGrid", {"columns":[{"show":true,"field":"flow.fromActor","title":"From","width":"50%","align":"left","formatFunc":"","editorProps":{"restrictValues":true},"mobileColumn":false},{"show":true,"field":"flow.toActor","title":"To","width":"50%","align":"left","formatFunc":"","editorProps":{"restrictValues":true},"mobileColumn":false},{"show":true,"field":"flow.name","title":"Flow","width":"100%","align":"left","formatFunc":"","editorProps":{"restrictValues":true},"mobileColumn":false},{"show":false,"field":"PHONE COLUMN","title":"-","width":"100%","align":"left","expression":"\"<div class='MobileRowTitle'>From: \" + ${flow.fromActor} + \"</div>\"\n+ \"<div class='MobileRow'>To: \" + ${flow.toActor} + \"</div>\"\n+ \"<div class='MobileRow'>Flow: \" + ${flow.name} + \"</div>\"\n","mobileColumn":true},{"show":false,"field":"flow.interview.id","title":"Flow.interview.id","width":"80px","align":"right","formatFunc":"","mobileColumn":false}],"dsType":"com.analystdb.data.IssueCommentFlows","height":"100%","margin":"4","minDesktopHeight":60,"singleClickEdit":true}, {"onSelect":"switchToFlow"}, {
										binding: ["wm.Binding", {}, {}, {
											wire: ["wm.Wire", {"expression":undefined,"source":"issueCommentFlowsVariable","targetProperty":"dataSet"}, {}]
										}]
									}],
									button14: ["wm.Button", {"_classes":{"domNode":["iconButton"]},"caption":undefined,"imageIndex":75,"imageList":"app.silkIconList","margin":"4","width":"32px"}, {"onclick":"otherIssueFlowsVariable1","onclick1":"EditIssueFlows.show"}]
								}],
								layer14: ["wm.Layer", {"caption":"Relevent Approaches","horizontalAlign":"left","layoutKind":"left-to-right","themeStyleType":"ContentPanel","verticalAlign":"top"}, {}, {
									dojoGrid5: ["wm.DojoGrid", {"columns":[{"show":false,"field":"PHONE COLUMN","title":"-","width":"100%","align":"left","editorProps":{"restrictValues":true},"expression":"\"<div class='MobileRowTitle'>Name: \" + ${approach.name} + \"</div>\"\n+ \"<div class='MobileRow'>Description: \" + ${approach.description} + \"</div>\"\n","mobileColumn":true},{"show":false,"field":"approach.id","title":"Approach.id","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"approach.approved","title":"Approach.approved","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"approach.interview.id","title":"Approach.interview.id","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"approach.interview.scheduled","title":"Approach.interview.scheduled","width":"100%","align":"left","formatFunc":"","editorProps":{"restrictValues":true},"mobileColumn":false},{"show":false,"field":"approach.interview.done","title":"Approach.interview.done","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"approach.interview.resource.id","title":"Approach.interview.resource.id","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"approach.interview.resource.project.id","title":"Approach.interview.resource.project.id","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"approach.interview.resource.project.description","title":"Approach.interview.resource.project.description","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"approach.interview.resource.project.name","title":"Approach.interview.resource.project.name","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"approach.interview.resource.project.version","title":"Approach.interview.resource.project.version","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"approach.interview.resource.description","title":"Approach.interview.resource.description","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"approach.interview.resource.name","title":"Approach.interview.resource.name","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"approach.interview.resource.version","title":"Approach.interview.resource.version","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"approach.interview.notes","title":"Approach.interview.notes","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"approach.interview.version","title":"Approach.interview.version","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"approach.start","title":"Approach.start","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":true,"field":"approach.name","title":"Name","width":"50%","align":"left","formatFunc":"","editorProps":{"restrictValues":true},"mobileColumn":false},{"show":true,"field":"approach.description","title":"Description","width":"100%","align":"left","formatFunc":"","editorProps":{"restrictValues":true},"mobileColumn":false},{"show":false,"field":"approach.cost","title":"Approach.cost","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"approach.end","title":"Approach.end","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"approach.version","title":"Approach.version","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"approach.interview.resource.project.tenantId","title":"Approach.interview.resource.project.tenantId","width":"100%","displayType":"Java.lang.Integer","align":"left","formatFunc":""}],"height":"100%","margin":"4","minDesktopHeight":60,"singleClickEdit":true}, {"onSelect":"switchToApproach"}, {
										binding: ["wm.Binding", {}, {}, {
											wire: ["wm.Wire", {"expression":undefined,"source":"issueApproaches","targetProperty":"dataSet"}, {}]
										}]
									}],
									button15: ["wm.Button", {"_classes":{"domNode":["iconButton"]},"caption":undefined,"imageIndex":75,"imageList":"app.silkIconList","margin":"4","width":"32px"}, {"onclick":"otherIssueApproachesVariable1","onclick1":"EditIssueApproaches.show"}]
								}],
								layer15: ["wm.Layer", {"caption":"Details","horizontalAlign":"left","themeStyleType":"ContentPanel","verticalAlign":"top"}, {}, {
									issueattributeLivePanel1: ["wm.LivePanel", {"horizontalAlign":"left","layoutKind":"left-to-right","verticalAlign":"top"}, {}, {
										binding: ["wm.Binding", {}, {}, {
											wire: ["wm.Wire", {"source":"issueattributeDojoGrid","targetId":null,"targetProperty":"dataGrid"}, {}]
										}],
										issueattributeDojoGrid: ["wm.DojoGrid", {"columns":[{"show":true,"field":"name","title":"Name","width":"25%","align":"left","formatFunc":"","fieldType":"dojox.grid.cells._Widget","mobileColumn":false},{"show":true,"field":"val","title":"Val","width":"100%","align":"left","formatFunc":"","fieldType":"dojox.grid.cells._Widget","mobileColumn":false},{"show":false,"field":"PHONE COLUMN","title":"-","width":"100%","align":"left","expression":"\"<div class='MobileRowTitle'>Name: \" + ${name} + \"</div>\"\n+ \"<div class='MobileRow'>Val: \" + ${val} + \"</div>\"\n","mobileColumn":true},{"show":false,"field":"id","title":"Id","width":"80px","displayType":"Number","align":"right","formatFunc":""},{"show":false,"field":"version","title":"Version","width":"80px","displayType":"Number","align":"right","formatFunc":""}],"deleteColumn":true,"dsType":"com.analystdb.data.IssueAttribute","height":"100%","liveEditing":true,"margin":"4","noHeader":true,"primaryKeyFields":["id"]}, {}, {
											binding: ["wm.Binding", {}, {}, {
												wire: ["wm.Wire", {"source":"issueAttributesVariable","targetProperty":"dataSet"}, {}]
											}]
										}],
										issueattributeNewButton: ["wm.Button", {"_classes":{"domNode":["iconButton"]},"caption":undefined,"imageIndex":1,"imageList":"app.silkIconList","margin":"4","width":"32px"}, {"onclick":"issueattributeNewButtonClick"}]
									}]
								}]
							}]
						}]
					}],
					Edit_Flow: ["wm.Layer", {"autoScroll":true,"borderColor":"","caption":"Edit Flow","horizontalAlign":"left","themeStyleType":"ContentPanel","verticalAlign":"top"}, {}, {
						binding: ["wm.Binding", {}, {}, {
							wire: ["wm.Wire", {"expression":undefined,"source":"flowDojoGrid.isRowSelected","targetProperty":"showing"}, {}]
						}],
						flowLiveForm1: ["wm.LiveForm", {"alwaysPopulateEditors":true,"height":"100%","horizontalAlign":"left","liveEditing":false,"margin":"4","readonly":true,"verticalAlign":"top"}, {"onBeforeServiceCall":"flowLiveForm1BeforeServiceCall","onSuccess":"interviewFlowsVariable1","onSuccess1":"Edit_Interview"}, {
							binding: ["wm.Binding", {}, {}, {
								wire: ["wm.Wire", {"expression":undefined,"source":"flowDojoGrid.selectedItem","targetProperty":"dataSet"}, {}],
								wire1: ["wm.Wire", {"expression":undefined,"source":"relatedEditor2.dataOutput","targetProperty":"dataOutput.documents"}, {}]
							}],
							fromActorEditor1Panel: ["wm.Panel", {"height":"34px","horizontalAlign":"left","layoutKind":"left-to-right","verticalAlign":"middle","width":"100%"}, {}, {
								fromActorEditor1: ["wm.Text", {"border":"0","caption":"From","captionSize":"140px","changeOnKey":true,"dataValue":"","desktopHeight":"26px","emptyValue":"emptyString","formField":"fromActor","height":"26px","readonly":true,"width":"100%"}, {}],
								flowFormButtonPanel: ["wm.Panel", {"desktopHeight":"32px","enableTouchHeight":true,"height":"32px","horizontalAlign":"right","layoutKind":"left-to-right","mobileHeight":"40px","verticalAlign":"top","width":"100%"}, {}, {
									flowDeleteButton: ["wm.Button", {"caption":"Delete","imageIndex":21,"imageList":"app.silkIconList","margin":"4"}, {"onclick":"flowLiveForm1.deleteData"}, {
										binding: ["wm.Binding", {}, {}, {
											wire: ["wm.Wire", {"source":"flowDojoGrid.emptySelection","targetId":null,"targetProperty":"disabled"}, {}]
										}]
									}],
									flowSaveButton: ["wm.Button", {"caption":"Save","imageIndex":0,"imageList":"app.silkIconList","margin":"4"}, {"onclick":"flowLiveForm1.saveDataIfValid"}, {
										binding: ["wm.Binding", {}, {}, {
											wire: ["wm.Wire", {"expression":"${flowLiveForm1.invalid} || !${flowLiveForm1.isDirty}","targetId":null,"targetProperty":"disabled"}, {}]
										}]
									}]
								}]
							}],
							toActorEditor1: ["wm.Text", {"border":"0","caption":"To","captionSize":"140px","changeOnKey":true,"dataValue":"","desktopHeight":"26px","emptyValue":"emptyString","formField":"toActor","height":"26px","readonly":true,"width":"50%"}, {}],
							nameEditor2: ["wm.Text", {"border":"0","caption":"Subject","captionSize":"140px","changeOnKey":true,"dataValue":"","desktopHeight":"26px","emptyValue":"emptyString","formField":"name","height":"26px","readonly":true,"required":true,"width":"100%"}, {}],
							documentsLookup1: ["wm.Lookup", {"allowNone":true,"autoDataSet":false,"caption":"Document","captionSize":"140px","dataType":"com.analystdb.data.Documents","desktopHeight":"26px","displayExpression":"${documentCategory.name} + \" - \" + ${document}","displayField":"document","emptyValue":"null","formField":"documents","height":"26px","readonly":true,"width":"100%"}, {}, {
								binding: ["wm.Binding", {}, {}, {
									dataFieldWire: ["wm.Wire", {"source":"documentsLookup1.liveVariable","targetProperty":"dataSet"}, {}],
									wire1: ["wm.Wire", {"expression":undefined,"source":"flowLiveForm1.dataOutput.documents","targetProperty":"dataValue"}, {}],
									wire: ["wm.Wire", {"expression":undefined,"source":"documentsLiveVariable2","targetProperty":"dataSet"}, {}]
								}]
							}],
							descriptionEditor4: ["wm.LargeTextArea", {"border":"0","caption":"Description","captionAlign":"right","captionPosition":"left","captionSize":"140px","changeOnKey":true,"dataValue":"","emptyValue":"emptyString","formField":"description","readonly":true,"width":"100%"}, {}],
							panel6: ["wm.Panel", {"height":"100%","horizontalAlign":"left","layoutKind":"left-to-right","verticalAlign":"top","width":"100%"}, {}, {
								dojoGrid8: ["wm.DojoGrid", {"columns":[{"show":true,"field":"issueComment.issue.sequence","title":"Issue","width":"50px","align":"center","formatFunc":"","editorProps":{"restrictValues":true},"expression":"\"I-\" + ${issueComment.issue.sequence}","mobileColumn":false},{"show":true,"field":"issueComment.description","title":"Description","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"issueComment.issue.description","title":"Description","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"PHONE COLUMN","title":"-","width":"100%","align":"left","expression":"\"<div class='MobileRowTitle'>Description: \" + ${issueComment.description} + \"</div>\"\n","mobileColumn":true},{"show":false,"field":"issueComment.interview.id","title":"IssueComment.interview.id","width":"80px","displayType":"Number","align":"right","formatFunc":""}],"dsType":"com.analystdb.data.IssueCommentFlows","height":"100%","margin":"4","minDesktopHeight":60,"singleClickEdit":true}, {"onSelect":"switchToIssue"}, {
									binding: ["wm.Binding", {}, {}, {
										wire: ["wm.Wire", {"expression":undefined,"source":"flowIssuesVariable","targetProperty":"dataSet"}, {}]
									}]
								}],
								button16: ["wm.Button", {"_classes":{"domNode":["iconButton"]},"caption":undefined,"imageIndex":75,"imageList":"app.silkIconList","margin":"4","width":"32px"}, {"onclick":"otherFlowIssuesVariable1","onclick1":"EditFlowIssues.show"}]
							}]
						}]
					}],
					Edit_Approach: ["wm.Layer", {"autoScroll":true,"borderColor":"","caption":"Edit Approach","horizontalAlign":"left","themeStyleType":"ContentPanel","verticalAlign":"top"}, {}, {
						binding: ["wm.Binding", {}, {}, {
							wire: ["wm.Wire", {"expression":undefined,"source":"approachDojoGrid.isRowSelected","targetProperty":"showing"}, {}]
						}],
						approachLiveForm1: ["wm.LiveForm", {"alwaysPopulateEditors":true,"height":"100%","horizontalAlign":"left","liveEditing":false,"margin":"4","readonly":true,"verticalAlign":"top"}, {"onBeforeServiceCall":"flowLiveForm1BeforeServiceCall","onSuccess":"interviewApproachesVariable1","onSuccess1":"Edit_Interview"}, {
							binding: ["wm.Binding", {}, {}, {
								wire: ["wm.Wire", {"expression":undefined,"source":"approachDojoGrid.selectedItem","targetProperty":"dataSet"}, {}]
							}],
							nameEditor4Panel: ["wm.Panel", {"fitToContentHeight":true,"height":"36px","horizontalAlign":"left","layoutKind":"left-to-right","verticalAlign":"middle","width":"100%"}, {}, {
								nameEditor4: ["wm.Text", {"border":"0","caption":"Name","captionSize":"140px","changeOnKey":true,"dataValue":"","desktopHeight":"26px","emptyValue":"emptyString","formField":"name","height":"26px","readonly":true,"required":true,"width":"100%"}, {}],
								approachFormButtonPanel: ["wm.Panel", {"desktopHeight":"32px","enableTouchHeight":true,"fitToContentHeight":true,"fitToContentWidth":true,"height":"34px","horizontalAlign":"right","layoutKind":"left-to-right","mobileHeight":"40px","verticalAlign":"top","width":"162px"}, {}, {
									approachDeleteButton: ["wm.Button", {"caption":"Delete","imageIndex":21,"imageList":"app.silkIconList","margin":"4"}, {"onclick":"approachLiveForm1.deleteData"}, {
										binding: ["wm.Binding", {}, {}, {
											wire: ["wm.Wire", {"source":"approachDojoGrid.emptySelection","targetId":null,"targetProperty":"disabled"}, {}]
										}]
									}],
									approachSaveButton: ["wm.Button", {"caption":"Save","imageIndex":0,"imageList":"app.silkIconList","margin":"4"}, {"onclick":"approachLiveForm1.saveDataIfValid"}, {
										binding: ["wm.Binding", {}, {}, {
											wire: ["wm.Wire", {"expression":"${approachLiveForm1.invalid} || !${approachLiveForm1.isDirty}","targetId":null,"targetProperty":"disabled"}, {}]
										}]
									}]
								}]
							}],
							descriptionEditor5: ["wm.LargeTextArea", {"border":"0","caption":"Description","captionAlign":"right","captionPosition":"left","captionSize":"140px","changeOnKey":true,"dataValue":"","desktopHeight":"100px","emptyValue":"emptyString","formField":"description","height":"100px","mobileHeight":"100%","readonly":true,"width":"100%"}, {}],
							date1: ["wm.Date", {"border":"0","caption":"Start","captionSize":"140px","desktopHeight":"26px","emptyValue":"null","formField":"start","height":"26px","readonly":true,"width":"100%"}, {}],
							date2: ["wm.Date", {"border":"0","caption":"End","captionSize":"140px","desktopHeight":"26px","emptyValue":"null","formField":"end","height":"26px","readonly":true,"width":"100%"}, {}],
							currency1: ["wm.Currency", {"border":"0","caption":"Cost","captionSize":"140px","dataValue":0,"desktopHeight":"26px","emptyValue":"null","formField":"cost","height":"26px","readonly":true,"width":"100%"}, {}],
							checkbox1: ["wm.Checkbox", {"caption":"Approved","captionSize":"140px","desktopHeight":"26px","displayValue":false,"formField":"approved","height":"26px","readonly":true}, {}],
							dojoGrid4Panel: ["wm.Panel", {"height":"100%","horizontalAlign":"left","layoutKind":"left-to-right","verticalAlign":"top","width":"100%"}, {}, {
								binding: ["wm.Binding", {}, {}, {
									wire: ["wm.Wire", {"expression":undefined,"source":"approachDojoGrid.isRowSelected","targetProperty":"showing"}, {}]
								}],
								dojoGrid4: ["wm.DojoGrid", {"columns":[{"show":true,"field":"issue.sequence","title":"Addresses","width":"80px","align":"center","formatFunc":"","editorProps":{"restrictValues":true},"expression":"\"I-\" + ${issue.sequence}","mobileColumn":false},{"show":true,"field":"issue.description","title":"Description","width":"100%","align":"left","formatFunc":"","editorProps":{"restrictValues":true},"mobileColumn":false},{"show":false,"field":"approach.name","title":"Name","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"approach.description","title":"Description","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"PHONE COLUMN","title":"-","width":"100%","align":"left","expression":"\"<div class='MobileRowTitle'>Description: \" + ${issue.description} + \"</div>\"\n","mobileColumn":true},{"show":false,"field":"approach.interview.id","title":"Approach.interview.id","width":"80px","displayType":"Number","align":"right","formatFunc":""}],"dsType":"com.analystdb.data.IssueApproach","height":"100%","margin":"4","minDesktopHeight":60,"singleClickEdit":true}, {}, {
									binding: ["wm.Binding", {}, {}, {
										wire: ["wm.Wire", {"expression":undefined,"source":"approachIssuesVariable","targetProperty":"dataSet"}, {}]
									}]
								}],
								button13: ["wm.Button", {"caption":undefined,"imageIndex":75,"imageList":"app.silkIconList","margin":"4","width":"32px"}, {"onclick":"otherApproachIssuesVariable1","onclick1":"EditApproachIssues.show"}]
							}]
						}]
					}],
					Edit_Issue2: ["wm.Layer", {"borderColor":"","horizontalAlign":"left","margin":"4,2","themeStyleType":"ContentPanel","verticalAlign":"top"}, {}, {
						binding: ["wm.Binding", {}, {}, {
							wire: ["wm.Wire", {"expression":"\"I-\" + ${issueForm.dataOutput.sequence}","targetProperty":"caption"}, {}]
						}],
						issueForm: ["wm.LiveForm", {"captionSize":"90px","height":"100%","horizontalAlign":"left","readonly":true,"verticalAlign":"top"}, {}, {
							binding: ["wm.Binding", {}, {}, {
								wire: ["wm.Wire", {"expression":undefined,"source":"summaryIssuesGrid1.selectedItem.issue","targetProperty":"dataSet"}, {}]
							}],
							sequenceEditor1Panel: ["wm.Panel", {"fitToContentHeight":true,"height":"34px","horizontalAlign":"left","layoutKind":"left-to-right","verticalAlign":"middle","width":"100%"}, {}, {
								sequenceEditor1: ["wm.Number", {"border":"0","caption":"Issue","captionSize":"90px","changeOnKey":true,"dataValue":0,"desktopHeight":"26px","emptyValue":"zero","formField":"sequence","formatter":"sequenceEditor1ReadOnlyNodeFormat","height":"26px","readonly":true,"required":true,"width":"394px"}, {}],
								issueFormEditPanel: ["wm.EditPanel", {"desktopHeight":"32px","height":"32px","isCustomized":true,"liveForm":"issueForm","lock":false,"operationPanel":"operationPanel1","savePanel":"savePanel1"}, {}, {
									savePanel1: ["wm.Panel", {"height":"100%","horizontalAlign":"right","layoutKind":"left-to-right","showing":false,"verticalAlign":"top","width":"100%"}, {}, {
										saveButton1: ["wm.Button", {"caption":"Save","height":"100%","imageIndex":0,"imageList":"app.silkIconList","margin":"4"}, {"onclick":"issueFormEditPanel.saveData"}, {
											binding: ["wm.Binding", {}, {}, {
												wire: ["wm.Wire", {"expression":undefined,"source":"issueFormEditPanel.formInvalid","targetProperty":"disabled"}, {}]
											}]
										}],
										cancelButton1: ["wm.Button", {"caption":"Cancel","height":"100%","imageIndex":21,"imageList":"app.silkIconList","margin":"4"}, {"onclick":"issueFormEditPanel.cancelEdit"}]
									}],
									operationPanel1: ["wm.Panel", {"height":"100%","horizontalAlign":"right","layoutKind":"left-to-right","verticalAlign":"top","width":"100%"}, {}, {
										deleteButton1: ["wm.Button", {"caption":"Delete","height":"100%","imageIndex":21,"imageList":"app.silkIconList","margin":"4"}, {"onclick":"issueFormEditPanel.deleteData"}, {
											binding: ["wm.Binding", {}, {}, {
												wire: ["wm.Wire", {"expression":undefined,"source":"issueFormEditPanel.formUneditable","targetProperty":"disabled"}, {}]
											}]
										}],
										updateButton1: ["wm.Button", {"caption":"Edit","height":"100%","imageIndex":0,"imageList":"app.silkIconList","margin":"4"}, {"onclick":"issueFormEditPanel.beginDataUpdate"}, {
											binding: ["wm.Binding", {}, {}, {
												wire: ["wm.Wire", {"expression":undefined,"source":"issueFormEditPanel.formUneditable","targetProperty":"disabled"}, {}]
											}]
										}]
									}]
								}]
							}],
							descriptionEditor6Panel: ["wm.Panel", {"height":"100%","horizontalAlign":"left","layoutKind":"left-to-right","verticalAlign":"top","width":"100%"}, {}, {
								descriptionEditor6: ["wm.LargeTextArea", {"border":"0","caption":"Description","captionAlign":"right","captionPosition":"left","captionSize":"90px","changeOnKey":true,"dataValue":"","emptyValue":"emptyString","formField":"description","height":"100%","maxHeight":0,"minDesktopHeight":96,"minHeight":96,"readonly":true,"width":"100%"}, {}],
								issueCategoryGrid2: ["wm.DojoGrid", {"columns":[{"show":false,"field":"id.issueId","title":"IssueId","width":"80px","align":"right","formatFunc":"","editorProps":{"restrictValues":true},"mobileColumn":false},{"show":false,"field":"id.issueCategoryId","title":"IssueCategoryId","width":"80px","align":"right","formatFunc":"","mobileColumn":false},{"show":true,"field":"issueCategory.name","title":"Categories","width":"100%","align":"left","formatFunc":"","editorProps":{"restrictValues":true},"mobileColumn":false},{"show":false,"field":"PHONE COLUMN","title":"-","width":"100%","align":"left","expression":"\"<div class='MobileRowTitle'>Categories: \" + ${issueCategory.name} + \"</div>\"\n","mobileColumn":true}],"dsType":"com.analystdb.data.IssueIssueCategory","height":"100%","margin":"4","minDesktopHeight":60,"selectionMode":"none","singleClickEdit":true,"width":"50%"}, {}, {
									binding: ["wm.Binding", {}, {}, {
										wire: ["wm.Wire", {"expression":undefined,"source":"issueCategoriesVariable1","targetProperty":"dataSet"}, {}]
									}]
								}],
								button18: ["wm.Button", {"_classes":{"domNode":["iconButton"]},"caption":undefined,"imageIndex":75,"imageList":"app.silkIconList","margin":"4","width":"32px"}, {"onclick":"otherCategoriesVariable1","onclick1":"EditCategories.show"}]
							}]
						}],
						tabLayers6: ["wm.TabLayers", {}, {}, {
							layer16: ["wm.Layer", {"caption":"Comments","horizontalAlign":"left","themeStyleType":"ContentPanel","verticalAlign":"top"}, {}, {
								commentGrid1: ["wm.DojoGrid", {"columns":[{"show":false,"field":"id","title":"Id","width":"80px","align":"right","formatFunc":"","editorProps":{"restrictValues":true},"mobileColumn":false},{"show":true,"field":"interview.resource.name","title":"From","width":"25%","align":"left","formatFunc":"","editorProps":{"restrictValues":true},"mobileColumn":false},{"show":true,"field":"description","title":"Comment","width":"100%","align":"left","formatFunc":"","editorProps":{"restrictValues":true},"mobileColumn":false},{"show":true,"field":"applicableToMe","title":"Applies","width":"45px","align":"center","formatFunc":"","fieldType":"dojox.grid.cells.Bool","editorProps":{"restrictValues":true},"mobileColumn":false},{"show":true,"field":"fixed","title":"Fixed","width":"45px","align":"center","formatFunc":"","fieldType":"dojox.grid.cells.Bool","editorProps":{"restrictValues":true},"mobileColumn":false},{"show":false,"field":"interview.scheduled","title":"Scheduled","width":"80px","align":"left","formatFunc":"wm_date_formatter","mobileColumn":false},{"show":false,"field":"PHONE COLUMN","title":"-","width":"100%","align":"left","expression":"\"<div class='MobileRowTitle'>From: \" + ${interview.resource.name} + \"</div>\"\n+ \"<div class='MobileRow'>Comment: \" + ${description} + \"</div>\"\n+ \"<div class='MobileRow'>Applies: \" + ${applicableToMe} + \"</div>\"\n+ \"<div class='MobileRow'>Fixed: \" + ${fixed} + \"</div>\"\n","mobileColumn":true},{"show":false,"field":"version","title":"Version","width":"80px","displayType":"Number","align":"right","formatFunc":""}],"dsType":"com.analystdb.data.IssueComment","height":"100%","margin":"4","minDesktopHeight":60,"primaryKeyFields":["id"],"singleClickEdit":true}, {"onSelect":"switchToIssue"}, {
									binding: ["wm.Binding", {}, {}, {
										wire: ["wm.Wire", {"expression":undefined,"source":"issuecommentLiveVariable1","targetProperty":"dataSet"}, {}]
									}]
								}]
							}],
							layer17: ["wm.Layer", {"caption":"Approaches","horizontalAlign":"left","layoutKind":"left-to-right","themeStyleType":"ContentPanel","verticalAlign":"top"}, {}, {
								dojoGrid1: ["wm.DojoGrid", {"columns":[{"show":true,"field":"approach.name","title":"Name","width":"40%","align":"left","formatFunc":"","mobileColumn":false},{"show":true,"field":"approach.description","title":"Description","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"PHONE COLUMN","title":"-","width":"100%","align":"left","expression":"\"<div class='MobileRowTitle'>Name: \" + ${approach.name} + \"</div>\"\n+ \"<div class='MobileRow'>Description: \" + ${approach.description} + \"</div>\"\n","mobileColumn":true},{"show":false,"field":"id.issueId","title":"Id.issueId","width":"80px","align":"right","formatFunc":"","mobileColumn":false},{"show":false,"field":"id.approachId","title":"Id.approachId","width":"80px","align":"right","formatFunc":"","mobileColumn":false},{"show":false,"field":"approach.interview.id","title":"Approach.interview.id","width":"80px","align":"right","formatFunc":"","mobileColumn":false},{"show":false,"field":"approach.interview.resource.id","title":"Approach.interview.resource.id","width":"80px","align":"right","formatFunc":"","mobileColumn":false}],"height":"100%","margin":"4","minDesktopHeight":60,"singleClickEdit":true}, {}, {
									binding: ["wm.Binding", {}, {}, {
										wire: ["wm.Wire", {"expression":undefined,"source":"issueFormApproaches","targetProperty":"dataSet"}, {}]
									}]
								}],
								button20: ["wm.Button", {"caption":undefined,"imageIndex":75,"imageList":"app.silkIconList","margin":"4","width":"32px"}, {}]
							}],
							layer18: ["wm.Layer", {"caption":"Flows","horizontalAlign":"left","themeStyleType":"ContentPanel","verticalAlign":"top"}, {}, {
								dojoGrid10: ["wm.DojoGrid", {"columns":[{"show":false,"field":"PHONE COLUMN","title":"-","width":"100%","align":"left","editorProps":{"restrictValues":true},"expression":"\"<div class='MobileRowTitle'>From: \" + ${flow.fromActor} + \"</div>\"\n+ \"<div class='MobileRow'>To: \" + ${flow.toActor} + \"</div>\"\n+ \"<div class='MobileRow'>Name: \" + ${flow.name} + \"</div>\"\n+ \"<div class='MobileRow'>Phase: \" + ${flow.documents.documentCategory.name} + \"</div>\"\n+ \"<div class='MobileRow'>Document: \" + ${flow.documents.document} + \"</div>\"\n","mobileColumn":true},{"show":false,"field":"flow.id","title":"Flow.id","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"flow.documents.id","title":"Flow.documents.id","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"flow.documents.documentCategory.id","title":"Flow.documents.documentCategory.id","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"flow.documents.documentCategory.sequence","title":"Flow.documents.documentCategory.sequence","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"flow.interview.id","title":"Flow.interview.id","width":"100%","align":"left","formatFunc":"","editorProps":{"restrictValues":true},"mobileColumn":false},{"show":false,"field":"flow.interview.scheduled","title":"Flow.interview.scheduled","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"flow.interview.done","title":"Flow.interview.done","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"flow.interview.resource.id","title":"Flow.interview.resource.id","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"flow.interview.resource.project.id","title":"Flow.interview.resource.project.id","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"flow.interview.resource.project.description","title":"Flow.interview.resource.project.description","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"flow.interview.resource.project.name","title":"Flow.interview.resource.project.name","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"flow.interview.resource.project.version","title":"Flow.interview.resource.project.version","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"flow.interview.resource.description","title":"Flow.interview.resource.description","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"flow.interview.resource.name","title":"Flow.interview.resource.name","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"flow.interview.resource.version","title":"Flow.interview.resource.version","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"flow.interview.notes","title":"Flow.interview.notes","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"flow.interview.version","title":"Flow.interview.version","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"flow.description","title":"Flow.description","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":true,"field":"flow.fromActor","title":"From","width":"50%","align":"left","formatFunc":"","editorProps":{"restrictValues":true},"mobileColumn":false},{"show":true,"field":"flow.toActor","title":"To","width":"50%","align":"left","formatFunc":"","editorProps":{"restrictValues":true},"mobileColumn":false},{"show":true,"field":"flow.name","title":"Name","width":"100%","align":"left","formatFunc":"","editorProps":{"restrictValues":true},"mobileColumn":false},{"show":true,"field":"flow.documents.documentCategory.name","title":"Phase","width":"50%","align":"left","formatFunc":"","editorProps":{"restrictValues":true},"mobileColumn":false},{"show":true,"field":"flow.documents.document","title":"Document","width":"100%","align":"left","formatFunc":"","editorProps":{"restrictValues":true},"mobileColumn":false},{"show":false,"field":"flow.version","title":"Flow.version","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"flow.interview.resource.project.tenantId","title":"Flow.interview.resource.project.tenantId","width":"100%","displayType":"Java.lang.Integer","align":"left","formatFunc":""},{"show":false,"field":"flow.documents.tenantId","title":"Flow.documents.tenantId","width":"100%","displayType":"Java.lang.Integer","align":"left","formatFunc":""},{"show":false,"field":"flow.documents.documentCategory.tenantId","title":"Flow.documents.documentCategory.tenantId","width":"100%","displayType":"Java.lang.Integer","align":"left","formatFunc":""}],"height":"100%","margin":"4","minDesktopHeight":60,"singleClickEdit":true}, {"onSelect":"switchToFlow"}, {
									binding: ["wm.Binding", {}, {}, {
										wire: ["wm.Wire", {"expression":undefined,"source":"issueFlows","targetProperty":"dataSet"}, {}]
									}]
								}]
							}]
						}]
					}]
				}]
			}]
		}],
		footer: ["wm.Panel", {"fitToContentHeight":true,"height":"82px","horizontalAlign":"right","layoutKind":"left-to-right","verticalAlign":"top","width":"800px"}, {}, {
			picture1: ["wm.Picture", {"height":"80px","width":"804px"}, {}, {
				binding: ["wm.Binding", {}, {}, {
					wire: ["wm.Wire", {"expression":"\"resources/images/\" + ${tenantInfo.logo}","targetProperty":"source"}, {}]
				}]
			}]
		}]
	}]
}