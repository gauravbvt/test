wizard.widgets = {
	logoutVariable1: ["wm.LogoutVariable", {"inFlightBehavior":"executeLast"}, {}, {
		input: ["wm.ServiceInput", {"type":"logoutInputs"}, {}]
	}],
	projectLiveVariable1: ["wm.LiveVariable", {"autoUpdate":false,"type":"com.analystdb.data.Project"}, {}, {
		liveView: ["wm.LiveView", {"dataType":"com.analystdb.data.Project","view":[{"caption":"Id","sortable":true,"dataIndex":"id","type":"java.lang.Long","displayType":"Number","required":true,"readonly":true,"includeLists":true,"includeForms":true,"order":0,"subType":null},{"caption":"Description","sortable":true,"dataIndex":"description","type":"java.lang.String","displayType":"Text","required":false,"readonly":false,"includeLists":true,"includeForms":true,"order":1,"subType":null},{"caption":"Name","sortable":true,"dataIndex":"name","type":"java.lang.String","displayType":"Text","required":true,"readonly":false,"includeLists":true,"includeForms":true,"order":2,"subType":null},{"caption":"Version","sortable":true,"dataIndex":"version","type":"java.lang.Integer","displayType":"Number","required":false,"readonly":false,"includeLists":true,"includeForms":true,"order":3,"subType":null}]}, {}]
	}],
	documentsLiveVariable1: ["wm.LiveVariable", {"autoUpdate":false,"orderBy":"asc: documentCategory.sequence, asc: document","type":"com.analystdb.data.Documents"}, {}, {
		liveView: ["wm.LiveView", {"dataType":"com.analystdb.data.Documents","related":["documentCategory"],"view":[{"caption":"Id","sortable":true,"dataIndex":"id","type":"java.lang.Integer","displayType":"Number","required":true,"readonly":true,"includeLists":true,"includeForms":true,"order":0,"subType":null},{"caption":"Document","sortable":true,"dataIndex":"document","type":"java.lang.String","displayType":"Text","required":false,"readonly":false,"includeLists":true,"includeForms":true,"order":6001,"subType":null,"widthUnits":"px"},{"caption":"Name","sortable":true,"dataIndex":"documentCategory.name","type":"java.lang.String","displayType":"Text","required":true,"widthUnits":"px","includeLists":true,"includeForms":true,"order":7001}]}, {}]
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
		liveView: ["wm.LiveView", {"dataType":"com.analystdb.data.Issue","related":["project"],"view":[{"caption":"Id","sortable":true,"dataIndex":"project.id","type":"java.lang.Long","displayType":"Number","required":true,"readonly":true,"includeLists":true,"includeForms":true,"order":0,"subType":null},{"caption":"Id","sortable":true,"dataIndex":"id","type":"java.lang.Long","displayType":"Number","required":true,"readonly":true,"includeLists":true,"includeForms":true,"order":0,"subType":null},{"caption":"Description","sortable":true,"dataIndex":"project.description","type":"java.lang.String","displayType":"Text","required":false,"readonly":false,"includeLists":true,"includeForms":true,"order":1,"subType":null},{"caption":"Name","sortable":true,"dataIndex":"project.name","type":"java.lang.String","displayType":"Text","required":true,"readonly":false,"includeLists":true,"includeForms":true,"order":2,"subType":null},{"caption":"Version","sortable":true,"dataIndex":"project.version","type":"java.lang.Integer","displayType":"Number","required":false,"readonly":false,"includeLists":true,"includeForms":true,"order":3,"subType":null},{"caption":"Version","sortable":true,"dataIndex":"version","type":"java.lang.Integer","displayType":"Number","required":false,"readonly":false,"includeLists":true,"includeForms":true,"order":3,"subType":null},{"caption":"Description","sortable":true,"dataIndex":"description","type":"java.lang.String","displayType":"Text","required":false,"readonly":false,"includeLists":true,"includeForms":true,"order":6001,"subType":null,"widthUnits":"px"},{"caption":"Name","sortable":true,"dataIndex":"name","type":"java.lang.String","displayType":"Text","required":true,"readonly":false,"includeLists":true,"includeForms":true,"order":6002,"subType":null,"widthUnits":"px"},{"caption":"Sequence","sortable":true,"dataIndex":"sequence","type":"java.lang.Integer","displayType":"Number","required":true,"readonly":false,"includeLists":true,"includeForms":true,"order":6004,"subType":null,"widthUnits":"px"}]}, {}]
	}],
	recentInterviewsVariable1: ["wm.ServiceVariable", {"inFlightBehavior":"executeLast","operation":"recentInterviews","service":"analystDB"}, {}, {
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
		liveView: ["wm.LiveView", {"dataType":"com.analystdb.data.DocumentCategory","view":[{"caption":"Id","sortable":true,"dataIndex":"id","type":"java.lang.Integer","displayType":"Number","required":true,"readonly":true,"includeLists":true,"includeForms":true,"order":0,"subType":null},{"caption":"Sequence","sortable":true,"dataIndex":"sequence","type":"java.lang.Short","displayType":"Number","required":false,"readonly":false,"includeLists":true,"includeForms":true,"order":2,"subType":null},{"caption":"Name","sortable":true,"dataIndex":"name","type":"java.lang.String","displayType":"Text","required":true,"readonly":false,"includeLists":true,"includeForms":true,"order":5001,"subType":null,"widthUnits":"px"}]}, {}]
	}],
	issueCategoryCount: ["wm.ServiceVariable", {"inFlightBehavior":"executeLast","operation":"issueCategoryCounts","service":"analystDB"}, {}, {
		input: ["wm.ServiceInput", {"type":"issueCategoryCountsInputs"}, {}, {
			binding: ["wm.Binding", {}, {}, {
				wire: ["wm.Wire", {"expression":undefined,"source":"projectDojoGrid.selectedItem.id","targetProperty":"project"}, {}]
			}]
		}]
	}],
	issueByCategoriesVariable1: ["wm.LiveVariable", {"autoUpdate":false,"inFlightBehavior":"executeLast","orderBy":"asc: issue.sequence","startUpdate":false,"type":"com.analystdb.data.IssueIssueCategory"}, {}, {
		binding: ["wm.Binding", {}, {}, {
			wire: ["wm.Wire", {"expression":undefined,"source":"issueCategoryGrid1.selectedItem.category","targetProperty":"filter.issueCategory"}, {}]
		}],
		liveView: ["wm.LiveView", {"dataType":"com.analystdb.data.IssueIssueCategory","related":["issue","id","issueCategory"],"view":[{"caption":"Sequence","sortable":true,"dataIndex":"issue.sequence","type":"java.lang.Integer","displayType":"Number","required":true,"widthUnits":"px","includeLists":true,"includeForms":true,"order":2001},{"caption":"Description","sortable":true,"dataIndex":"issue.description","type":"java.lang.String","displayType":"Text","required":false,"widthUnits":"px","includeLists":true,"includeForms":true,"order":2003},{"caption":"IssueId","sortable":true,"dataIndex":"id.issueId","type":"java.lang.Long","displayType":"Number","required":true,"readonly":true,"includeLists":true,"includeForms":true,"order":0,"subType":null},{"caption":"IssueCategoryId","sortable":true,"dataIndex":"id.issueCategoryId","type":"java.lang.Long","displayType":"Number","required":true,"readonly":true,"includeLists":true,"includeForms":true,"order":1,"subType":null},{"caption":"Id","sortable":true,"dataIndex":"issueCategory.id","type":"java.lang.Long","displayType":"Number","required":true,"readonly":true,"includeLists":true,"includeForms":true,"order":0,"subType":null},{"caption":"Name","sortable":true,"dataIndex":"issueCategory.name","type":"java.lang.String","displayType":"Text","required":true,"readonly":false,"includeLists":true,"includeForms":true,"order":1,"subType":null},{"caption":"Version","sortable":true,"dataIndex":"issueCategory.version","type":"java.lang.Integer","displayType":"Number","required":false,"readonly":false,"includeLists":true,"includeForms":true,"order":2,"subType":null}]}, {}]
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
				wire1: ["wm.Wire", {"expression":undefined,"source":"dojoGrid3.selectedItem.id","targetProperty":"document"}, {}]
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
	interviewLiveVariable1: ["wm.LiveVariable", {"autoUpdate":false,"inFlightBehavior":"executeLast","operation":"insert","startUpdate":false,"type":"com.analystdb.data.Interview"}, {"onSuccess":"recentInterviewsVariable1","onSuccess1":"upcomingInterviewsVariable1"}, {
		liveView: ["wm.LiveView", {"dataType":"com.analystdb.data.Interview","view":[{"caption":"Id","sortable":true,"dataIndex":"id","type":"java.lang.Long","displayType":"Number","required":true,"readonly":true,"includeLists":true,"includeForms":true,"order":0,"subType":null},{"caption":"Scheduled","sortable":true,"dataIndex":"scheduled","type":"java.util.Date","displayType":"Date","required":true,"readonly":false,"includeLists":true,"includeForms":true,"order":1,"subType":null},{"caption":"Notes","sortable":true,"dataIndex":"notes","type":"java.lang.String","displayType":"Text","required":false,"readonly":false,"includeLists":true,"includeForms":true,"order":2,"subType":null},{"caption":"Done","sortable":true,"dataIndex":"done","type":"java.lang.Boolean","displayType":"CheckBox","required":true,"readonly":false,"includeLists":true,"includeForms":true,"order":3,"subType":null},{"caption":"Version","sortable":true,"dataIndex":"version","type":"java.lang.Integer","displayType":"Number","required":false,"readonly":false,"includeLists":true,"includeForms":true,"order":4,"subType":null}]}, {}]
	}],
	analysisInsert: ["wm.LiveVariable", {"autoUpdate":false,"inFlightBehavior":"executeLast","operation":"insert","startUpdate":false,"type":"com.analystdb.data.Analysis"}, {}, {
		binding: ["wm.Binding", {}, {}, {
			wire: ["wm.Wire", {"expression":undefined,"source":"projectLiveForm1.dataOutput","targetProperty":"sourceData.project"}, {}],
			wire1: ["wm.Wire", {"expression":"\"Default\"","targetProperty":"sourceData.name"}, {}],
			wire2: ["wm.Wire", {"expression":"\"Created with project\"","targetProperty":"sourceData.description"}, {}]
		}],
		liveView: ["wm.LiveView", {"dataType":"com.analystdb.data.Analysis","view":[{"caption":"Id","sortable":true,"dataIndex":"id","type":"java.lang.Long","displayType":"Number","required":true,"readonly":true,"includeLists":true,"includeForms":true,"order":0,"subType":null},{"caption":"Version","sortable":true,"dataIndex":"version","type":"java.lang.Integer","displayType":"Number","required":false,"readonly":false,"includeLists":true,"includeForms":true,"order":3,"subType":null},{"caption":"Description","sortable":true,"dataIndex":"description","type":"java.lang.String","displayType":"Text","required":false,"readonly":false,"includeLists":true,"includeForms":true,"order":2001,"subType":null,"widthUnits":"px"},{"caption":"Name","sortable":true,"dataIndex":"name","type":"java.lang.String","displayType":"Text","required":true,"readonly":false,"includeLists":true,"includeForms":true,"order":2002,"subType":null,"widthUnits":"px"}]}, {}]
	}],
	approachLiveVariable1: ["wm.LiveVariable", {"autoUpdate":false,"startUpdate":false,"type":"com.analystdb.data.Approach"}, {}, {
		liveView: ["wm.LiveView", {"dataType":"com.analystdb.data.Approach","related":["interview"],"view":[{"caption":"Id","sortable":true,"dataIndex":"id","type":"java.lang.Long","displayType":"Number","required":true,"readonly":true,"includeLists":true,"includeForms":true,"order":0,"subType":null},{"caption":"Name","sortable":true,"dataIndex":"name","type":"java.lang.String","displayType":"Text","required":true,"readonly":false,"includeLists":true,"includeForms":true,"order":1,"subType":null},{"caption":"Description","sortable":true,"dataIndex":"description","type":"java.lang.String","displayType":"Text","required":false,"readonly":false,"includeLists":true,"includeForms":true,"order":2,"subType":null},{"caption":"Version","sortable":true,"dataIndex":"version","type":"java.lang.Integer","displayType":"Number","required":false,"readonly":false,"includeLists":true,"includeForms":true,"order":3,"subType":null},{"caption":"Id","sortable":true,"dataIndex":"interview.id","type":"java.lang.Long","displayType":"Number","required":true,"readonly":true,"includeLists":true,"includeForms":true,"order":0,"subType":null},{"caption":"Scheduled","sortable":true,"dataIndex":"interview.scheduled","type":"java.util.Date","displayType":"Date","required":true,"readonly":false,"includeLists":true,"includeForms":true,"order":1,"subType":null},{"caption":"Notes","sortable":true,"dataIndex":"interview.notes","type":"java.lang.String","displayType":"Text","required":false,"readonly":false,"includeLists":true,"includeForms":true,"order":2,"subType":null},{"caption":"Done","sortable":true,"dataIndex":"interview.done","type":"java.lang.Boolean","displayType":"CheckBox","required":true,"readonly":false,"includeLists":true,"includeForms":true,"order":3,"subType":null},{"caption":"Version","sortable":true,"dataIndex":"interview.version","type":"java.lang.Integer","displayType":"Number","required":false,"readonly":false,"includeLists":true,"includeForms":true,"order":4,"subType":null}]}, {}],
		binding: ["wm.Binding", {}, {}, {
			wire: ["wm.Wire", {"expression":undefined,"source":"interviewDojoGrid1.selectedItem.interview","targetProperty":"filter.interview"}, {}]
		}]
	}],
	approachIssuesVariable: ["wm.LiveVariable", {"autoUpdate":false,"startUpdate":false,"type":"com.analystdb.data.IssueApproach"}, {}, {
		liveView: ["wm.LiveView", {"dataType":"com.analystdb.data.IssueApproach","related":["issue","approach"],"view":[{"caption":"Sequence","sortable":true,"dataIndex":"issue.sequence","type":"java.lang.Integer","displayType":"Number","required":true,"readonly":false,"includeLists":true,"includeForms":true,"order":3001,"subType":null,"widthUnits":"px"},{"caption":"Description","sortable":true,"dataIndex":"issue.description","type":"java.lang.String","displayType":"Text","required":false,"readonly":false,"includeLists":true,"includeForms":true,"order":3003,"subType":null,"widthUnits":"px"},{"caption":"Name","sortable":true,"dataIndex":"approach.name","type":"java.lang.String","displayType":"Text","required":true,"readonly":false,"includeLists":true,"includeForms":true,"order":4001,"subType":null,"widthUnits":"px"},{"caption":"Description","sortable":true,"dataIndex":"approach.description","type":"java.lang.String","displayType":"Text","required":false,"readonly":false,"includeLists":true,"includeForms":true,"order":4002,"subType":null,"widthUnits":"px"}]}, {}],
		binding: ["wm.Binding", {}, {}, {
			wire: ["wm.Wire", {"expression":undefined,"source":"approachDojoGrid.selectedItem","targetProperty":"filter.approach"}, {}]
		}]
	}],
	issueApproachesVariable: ["wm.LiveVariable", {"autoUpdate":false,"inFlightBehavior":"executeLast","startUpdate":false,"type":"com.analystdb.data.IssueApproach"}, {}, {
		binding: ["wm.Binding", {}, {}, {
			wire: ["wm.Wire", {"expression":undefined,"source":"dataGrid2.selectedItem.issue","targetProperty":"filter.issue"}, {}]
		}],
		liveView: ["wm.LiveView", {"dataType":"com.analystdb.data.IssueApproach","related":["issue","approach"],"view":[{"caption":"Sequence","sortable":true,"dataIndex":"issue.sequence","type":"java.lang.Integer","displayType":"Number","required":true,"widthUnits":"px","includeLists":true,"includeForms":true,"order":7001},{"caption":"Description","sortable":true,"dataIndex":"issue.description","type":"java.lang.String","displayType":"Text","required":false,"widthUnits":"px","includeLists":true,"includeForms":true,"order":7003},{"caption":"Name","sortable":true,"dataIndex":"approach.name","type":"java.lang.String","displayType":"Text","required":true,"widthUnits":"px","includeLists":true,"includeForms":true,"order":8001},{"caption":"Description","sortable":true,"dataIndex":"approach.description","type":"java.lang.String","displayType":"Text","required":false,"widthUnits":"px","includeLists":true,"includeForms":true,"order":8002}]}, {}]
	}],
	issueFlowsVariable: ["wm.LiveVariable", {"autoUpdate":false,"inFlightBehavior":"executeLast","startUpdate":false,"type":"com.analystdb.data.IssueCommentFlows"}, {}, {
		binding: ["wm.Binding", {}, {}, {
			wire: ["wm.Wire", {"expression":undefined,"source":"dataGrid2.selectedItem","targetProperty":"filter.issueComment"}, {}]
		}],
		liveView: ["wm.LiveView", {"dataType":"com.analystdb.data.IssueCommentFlows","related":["flow"],"view":[{"caption":"FromActor","sortable":true,"dataIndex":"flow.fromActor","type":"java.lang.String","displayType":"Text","required":false,"widthUnits":"px","includeLists":true,"includeForms":true,"order":13001},{"caption":"ToActor","sortable":true,"dataIndex":"flow.toActor","type":"java.lang.String","displayType":"Text","required":false,"widthUnits":"px","includeLists":true,"includeForms":true,"order":13002},{"caption":"Name","sortable":true,"dataIndex":"flow.name","type":"java.lang.String","displayType":"Text","required":true,"widthUnits":"px","includeLists":true,"includeForms":true,"order":13003}]}, {}]
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
		liveView: ["wm.LiveView", {"dataType":"com.analystdb.data.IssueCommentFlows","related":["issueComment","issueComment.issue"],"view":[{"caption":"Description","sortable":true,"dataIndex":"issueComment.description","type":"java.lang.String","displayType":"Text","required":false,"widthUnits":"px","includeLists":true,"includeForms":true,"order":21001},{"caption":"Sequence","sortable":true,"dataIndex":"issueComment.issue.sequence","type":"java.lang.Integer","displayType":"Number","required":true,"widthUnits":"px","includeLists":true,"includeForms":true,"order":22001},{"caption":"Description","sortable":true,"dataIndex":"issueComment.issue.description","type":"java.lang.String","displayType":"Text","required":false,"widthUnits":"px","includeLists":true,"includeForms":true,"order":22003}]}, {}]
	}],
	EditApproachIssues: ["wm.DesignableDialog", {"buttonBarId":"buttonBar","containerWidgetId":"containerWidget","title":"Edit approach's issues"}, {}, {
		containerWidget: ["wm.Container", {"_classes":{"domNode":["wmdialogcontainer","MainContent"]},"autoScroll":true,"height":"100%","horizontalAlign":"left","layoutKind":"left-to-right","padding":"5","verticalAlign":"top","width":"100%"}, {}, {
			selectedGrid: ["wm.DojoGrid", {"columns":[{"show":true,"field":"issue.sequence","title":"Issue","width":"50px","align":"center","formatFunc":"","editorProps":{"restrictValues":true},"expression":"\"I-\" + ${issue.sequence}","mobileColumn":false},{"show":true,"field":"issue.description","title":"Description","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"approach.name","title":"Name","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"approach.description","title":"Description","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"PHONE COLUMN","title":"-","width":"100%","align":"left","expression":"\"<div class='MobileRowTitle'>Description: \" + ${issue.description} + \"</div>\"\n","mobileColumn":true}],"dsType":"com.analystdb.data.IssueApproach","height":"100%","localizationStructure":{},"margin":"4","minDesktopHeight":60,"singleClickEdit":true}, {}, {
				binding: ["wm.Binding", {}, {}, {
					wire: ["wm.Wire", {"expression":undefined,"source":"approachIssuesVariable","targetProperty":"dataSet"}, {}]
				}]
			}],
			panel2: ["wm.Panel", {"fitToContentWidth":true,"height":"100%","horizontalAlign":"left","verticalAlign":"middle","width":"82px"}, {}, {
				addButton: ["wm.Button", {"caption":"Add","imageIndex":3,"imageList":"app.silkIconList","margin":"4"}, {}],
				removeButton: ["wm.Button", {"caption":"Remove","imageIndex":5,"imageList":"app.silkIconList","margin":"4"}, {}]
			}],
			availableGrid: ["wm.DojoGrid", {"columns":[{"show":true,"field":"id","title":"Issue","width":"50px","align":"center","formatFunc":"","expression":"\"I-\" + ${id}","mobileColumn":false},{"show":true,"field":"description","title":"Description","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"applicableToMe","title":"ApplicableToMe","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"fixed","title":"Fixed","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"version","title":"Version","width":"80px","align":"right","formatFunc":"","mobileColumn":false},{"show":false,"field":"PHONE COLUMN","title":"-","width":"100%","align":"left","expression":"\"<div class='MobileRowTitle'>Description: \" + ${description} + \"</div>\"\n","mobileColumn":true}],"dsType":"com.analystdb.data.IssueComment","height":"100%","margin":"4","minDesktopHeight":60,"singleClickEdit":true}, {}, {
				binding: ["wm.Binding", {}, {}, {
					wire: ["wm.Wire", {"expression":undefined,"source":"interviewDojoGrid1.selectedItem.interview.issueComments","targetProperty":"dataSet"}, {}]
				}]
			}]
		}],
		buttonBar: ["wm.Panel", {"_classes":{"domNode":["dialogfooter"]},"border":"1","desktopHeight":"32px","enableTouchHeight":true,"height":"32px","horizontalAlign":"right","layoutKind":"left-to-right","mobileHeight":"40px","verticalAlign":"top","width":"100%"}, {}, {
			cancelButton: ["wm.Button", {"caption":"Cancel","imageIndex":21,"imageList":"app.silkIconList","margin":"4"}, {"onclick":"EditApproachIssues.hide"}],
			doneButton: ["wm.Button", {"caption":"Done","imageIndex":0,"imageList":"app.silkIconList","margin":"4"}, {"onclick":"EditApproachIssues.hide"}]
		}]
	}],
	EditIssueApproaches: ["wm.DesignableDialog", {"buttonBarId":"buttonBar","containerWidgetId":"containerWidget","title":"Edit issue's approaches"}, {}, {
		containerWidget1: ["wm.Container", {"_classes":{"domNode":["wmdialogcontainer","MainContent"]},"autoScroll":true,"height":"100%","horizontalAlign":"left","layoutKind":"left-to-right","padding":"5","verticalAlign":"top","width":"100%"}, {}, {
			selectedGrid1: ["wm.DojoGrid", {"columns":[{"show":false,"field":"issue.sequence","title":"Sequence","width":"80px","align":"right","formatFunc":"","mobileColumn":false},{"show":false,"field":"issue.description","title":"Description","width":"100%","align":"left","formatFunc":"","editorProps":{"restrictValues":true},"mobileColumn":false},{"show":true,"field":"approach.name","title":"Selected","width":"100%","align":"left","formatFunc":"","editorProps":{"restrictValues":true},"mobileColumn":false},{"show":false,"field":"approach.description","title":"Description","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"PHONE COLUMN","title":"-","width":"100%","align":"left","expression":"\"<div class='MobileRowTitle'>Selected: \" + ${approach.name} + \"</div>\"\n","mobileColumn":true}],"dsType":"com.analystdb.data.IssueApproach","height":"100%","margin":"4","minDesktopHeight":60,"singleClickEdit":true}, {}, {
				binding: ["wm.Binding", {}, {}, {
					wire: ["wm.Wire", {"expression":undefined,"source":"issueApproachesVariable","targetProperty":"dataSet"}, {}]
				}]
			}],
			panel3: ["wm.Panel", {"fitToContentWidth":true,"height":"100%","horizontalAlign":"left","verticalAlign":"middle","width":"82px"}, {}, {
				addButton1: ["wm.Button", {"caption":"Add","imageIndex":3,"imageList":"app.silkIconList","margin":"4"}, {}],
				removeButton1: ["wm.Button", {"caption":"Remove","imageIndex":5,"imageList":"app.silkIconList","margin":"4"}, {}]
			}],
			availableGrid1: ["wm.DojoGrid", {"columns":[{"show":false,"field":"id","title":"Id","width":"80px","align":"right","formatFunc":"","mobileColumn":false},{"show":true,"field":"name","title":"Available","width":"100%","align":"left","formatFunc":"","editorProps":{"restrictValues":true},"mobileColumn":false},{"show":false,"field":"description","title":"Description","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"version","title":"Version","width":"80px","align":"right","formatFunc":"","mobileColumn":false},{"show":false,"field":"interview.id","title":"Id","width":"80px","align":"right","formatFunc":"","mobileColumn":false},{"show":false,"field":"interview.scheduled","title":"Scheduled","width":"80px","align":"left","formatFunc":"wm_date_formatter","mobileColumn":false},{"show":false,"field":"interview.notes","title":"Notes","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"interview.done","title":"Done","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"interview.version","title":"Version","width":"80px","align":"right","formatFunc":"","mobileColumn":false},{"show":false,"field":"PHONE COLUMN","title":"-","width":"100%","align":"left","expression":"\"<div class='MobileRowTitle'>Available: \" + ${name} + \"</div>\"\n","mobileColumn":true}],"dsType":"com.analystdb.data.Approach","height":"100%","margin":"4","minDesktopHeight":60,"primaryKeyFields":["id"],"singleClickEdit":true}, {}, {
				binding: ["wm.Binding", {}, {}, {
					wire: ["wm.Wire", {"expression":undefined,"source":"approachLiveVariable1","targetProperty":"dataSet"}, {}]
				}]
			}]
		}],
		buttonBar1: ["wm.Panel", {"_classes":{"domNode":["dialogfooter"]},"border":"1","desktopHeight":"32px","enableTouchHeight":true,"height":"32px","horizontalAlign":"right","layoutKind":"left-to-right","mobileHeight":"40px","verticalAlign":"top","width":"100%"}, {}, {
			cancelButton1: ["wm.Button", {"caption":"Cancel","imageIndex":21,"imageList":"app.silkIconList","margin":"4"}, {"onclick":"EditIssueApproaches.hide"}],
			doneButton1: ["wm.Button", {"caption":"Done","imageIndex":0,"imageList":"app.silkIconList","margin":"4"}, {"onclick":"EditIssueApproaches.hide"}]
		}]
	}],
	EditFlowIssues: ["wm.DesignableDialog", {"buttonBarId":"buttonBar","containerWidgetId":"containerWidget","title":"Edit flow's issues"}, {}, {
		containerWidget2: ["wm.Container", {"_classes":{"domNode":["wmdialogcontainer","MainContent"]},"autoScroll":true,"height":"100%","horizontalAlign":"left","layoutKind":"left-to-right","padding":"5","verticalAlign":"top","width":"100%"}, {}, {
			selectedGrid2: ["wm.DojoGrid", {"columns":[{"show":false,"field":"PHONE COLUMN","title":"-","width":"100%","align":"left","expression":"\"<div class='MobileRowTitle'>Description: \" + ${issueComment.description} + \"</div>\"\n","mobileColumn":true},{"show":true,"field":"issueComment.issue.sequence","title":"Issue","width":"50px","align":"center","formatFunc":"","editorProps":{"restrictValues":true},"expression":"\"I-\" + ${issueComment.issue.sequence}","mobileColumn":false},{"show":true,"field":"issueComment.description","title":"Description","width":"100%","align":"left","formatFunc":"","editorProps":{"restrictValues":true},"mobileColumn":false},{"show":false,"field":"issueComment.issue.description","title":"IssueComment.issue.description","width":"100%","align":"left","formatFunc":"","mobileColumn":false}],"height":"100%","margin":"4","minDesktopHeight":60,"singleClickEdit":true}, {}, {
				binding: ["wm.Binding", {}, {}, {
					wire: ["wm.Wire", {"expression":undefined,"source":"flowIssuesVariable","targetProperty":"dataSet"}, {}]
				}]
			}],
			panel4: ["wm.Panel", {"fitToContentWidth":true,"height":"100%","horizontalAlign":"left","verticalAlign":"middle","width":"82px"}, {}, {
				addButton2: ["wm.Button", {"caption":"Add","imageIndex":3,"imageList":"app.silkIconList","margin":"4"}, {}],
				removeButton2: ["wm.Button", {"caption":"Remove","imageIndex":5,"imageList":"app.silkIconList","margin":"4"}, {}]
			}],
			availableGrid2: ["wm.DojoGrid", {"columns":[{"show":true,"field":"id","title":"Issue","width":"50px","align":"center","formatFunc":"","editorProps":{"restrictValues":true},"expression":"\"I-\" + ${id}","mobileColumn":false},{"show":true,"field":"description","title":"Description","width":"100%","align":"left","formatFunc":"","editorProps":{"restrictValues":true},"mobileColumn":false},{"show":false,"field":"applicableToMe","title":"ApplicableToMe","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"fixed","title":"Fixed","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"version","title":"Version","width":"80px","align":"right","formatFunc":"","mobileColumn":false},{"show":false,"field":"PHONE COLUMN","title":"-","width":"100%","align":"left","expression":"\"<div class='MobileRowTitle'>Description: \" + ${description} + \"</div>\"\n","mobileColumn":true}],"dsType":"com.analystdb.data.IssueComment","height":"100%","margin":"4","minDesktopHeight":60,"primaryKeyFields":["id"],"singleClickEdit":true}, {}, {
				binding: ["wm.Binding", {}, {}, {
					wire: ["wm.Wire", {"expression":undefined,"source":"interviewDojoGrid1.selectedItem.interview.issueComments","targetProperty":"dataSet"}, {}]
				}]
			}]
		}],
		buttonBar2: ["wm.Panel", {"_classes":{"domNode":["dialogfooter"]},"border":"1","desktopHeight":"32px","enableTouchHeight":true,"height":"32px","horizontalAlign":"right","layoutKind":"left-to-right","mobileHeight":"40px","verticalAlign":"top","width":"100%"}, {}, {
			cancelButton2: ["wm.Button", {"caption":"Cancel","imageIndex":21,"imageList":"app.silkIconList","margin":"4"}, {"onclick":"EditFlowIssues.hide"}],
			doneButton2: ["wm.Button", {"caption":"Done","imageIndex":0,"imageList":"app.silkIconList","margin":"4"}, {"onclick":"EditFlowIssues.hide"}]
		}]
	}],
	EditIssueFlows: ["wm.DesignableDialog", {"buttonBarId":"buttonBar","containerWidgetId":"containerWidget","title":"Edit issue's flows"}, {}, {
		containerWidget3: ["wm.Container", {"_classes":{"domNode":["wmdialogcontainer","MainContent"]},"autoScroll":true,"height":"100%","horizontalAlign":"left","layoutKind":"left-to-right","padding":"5","verticalAlign":"top","width":"100%"}, {}, {
			selectedGrid3: ["wm.DojoGrid", {"columns":[{"show":true,"field":"flow.fromActor","title":"From","width":"50%","align":"left","formatFunc":"","mobileColumn":false},{"show":true,"field":"flow.toActor","title":"To","width":"50%","align":"left","formatFunc":"","editorProps":{"restrictValues":true},"mobileColumn":false},{"show":true,"field":"flow.name","title":"Name","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"PHONE COLUMN","title":"-","width":"100%","align":"left","expression":"\"<div class='MobileRowTitle'>From: \" + ${flow.fromActor} + \"</div>\"\n+ \"<div class='MobileRow'>To: \" + ${flow.toActor} + \"</div>\"\n+ \"<div class='MobileRow'>Name: \" + ${flow.name} + \"</div>\"\n","mobileColumn":true}],"height":"100%","margin":"4","minDesktopHeight":60,"singleClickEdit":true}, {}, {
				binding: ["wm.Binding", {}, {}, {
					wire: ["wm.Wire", {"expression":undefined,"source":"issueFlowsVariable","targetProperty":"dataSet"}, {}]
				}]
			}],
			panel5: ["wm.Panel", {"fitToContentWidth":true,"height":"100%","horizontalAlign":"left","verticalAlign":"middle","width":"82px"}, {}, {
				addButton3: ["wm.Button", {"caption":"Add","imageIndex":3,"imageList":"app.silkIconList","margin":"4"}, {}],
				removeButton3: ["wm.Button", {"caption":"Remove","imageIndex":5,"imageList":"app.silkIconList","margin":"4"}, {}]
			}],
			availableGrid3: ["wm.DojoGrid", {"columns":[{"show":false,"field":"id","title":"Id","width":"80px","align":"right","formatFunc":"","mobileColumn":false},{"show":true,"field":"fromActor","title":"From","width":"50%","align":"left","formatFunc":"","editorProps":{"restrictValues":true},"mobileColumn":false},{"show":true,"field":"toActor","title":"To","width":"50%","align":"left","formatFunc":"","editorProps":{"restrictValues":true},"mobileColumn":false},{"show":true,"field":"name","title":"Name","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"description","title":"Description","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"version","title":"Version","width":"80px","align":"right","formatFunc":"","mobileColumn":false},{"show":false,"field":"PHONE COLUMN","title":"-","width":"100%","align":"left","expression":"\"<div class='MobileRowTitle'>From: \" + ${fromActor} + \"</div>\"\n+ \"<div class='MobileRow'>To: \" + ${toActor} + \"</div>\"\n+ \"<div class='MobileRow'>Name: \" + ${name} + \"</div>\"\n","mobileColumn":true}],"height":"100%","margin":"4","minDesktopHeight":60,"singleClickEdit":true}, {}, {
				binding: ["wm.Binding", {}, {}, {
					wire: ["wm.Wire", {"expression":undefined,"source":"interviewDojoGrid1.selectedItem.interview.flows","targetProperty":"dataSet"}, {}]
				}]
			}]
		}],
		buttonBar3: ["wm.Panel", {"_classes":{"domNode":["dialogfooter"]},"border":"1","desktopHeight":"32px","enableTouchHeight":true,"height":"32px","horizontalAlign":"right","layoutKind":"left-to-right","mobileHeight":"40px","verticalAlign":"top","width":"100%"}, {}, {
			cancelButton3: ["wm.Button", {"caption":"Cancel","imageIndex":21,"imageList":"app.silkIconList","margin":"4"}, {"onclick":"EditIssueFlows.hide"}],
			doneButton3: ["wm.Button", {"caption":"Done","imageIndex":0,"imageList":"app.silkIconList","margin":"4"}, {"onclick":"EditIssueFlows.hide"}]
		}]
	}],
	layoutBox: ["wm.Layout", {"_classes":{"domNode":["back"]},"enableTouchHeight":true,"horizontalAlign":"center","verticalAlign":"top","width":"867px"}, {}, {
		container: ["wm.Panel", {"_classes":{"domNode":["container"]},"height":"100%","horizontalAlign":"left","verticalAlign":"top","width":"800px"}, {}, {
			header: ["wm.Panel", {"_classes":{"domNode":["banner"]},"height":"51px","horizontalAlign":"left","layoutKind":"left-to-right","verticalAlign":"middle","width":"100%"}, {}, {
				logo: ["wm.Picture", {"height":"51px","source":"resources/images/rf-logo.png","width":"291px"}, {}],
				button1Panel: ["wm.Panel", {"height":"34px","horizontalAlign":"right","verticalAlign":"top","width":"100%"}, {}, {
					logoutButton: ["wm.Button", {"caption":"Logout","margin":"4"}, {"onclick":"logoutVariable1"}]
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
							projectDojoGrid: ["wm.DojoGrid", {"columns":[{"show":false,"field":"id","title":"Id","width":"80px","align":"right","formatFunc":"","editorProps":{"restrictValues":true},"mobileColumn":false},{"show":true,"field":"name","title":"Name","width":"25%","align":"left","formatFunc":"","editorProps":{"restrictValues":true},"mobileColumn":false},{"show":true,"field":"description","title":"Description","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"version","title":"Version","width":"80px","align":"right","formatFunc":"","mobileColumn":false},{"show":false,"field":"PHONE COLUMN","title":"-","width":"100%","align":"left","expression":"\"<div class='MobileRowTitle'>Name: \" + ${name} + \"</div>\"\n+ \"<div class='MobileRow'>Description: \" + ${description} + \"</div>\"\n","mobileColumn":true}],"dsType":"com.analystdb.data.Project","height":"100%","localizationStructure":{},"margin":"4","primaryKeyFields":["id"]}, {"onSelect2":"projectLivePanel1.popupLivePanelEdit"}, {
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
							wire: ["wm.Wire", {"expression":undefined,"source":"nameEditor1.dataValue","targetProperty":"caption"}, {}]
						}],
						projectLiveForm1: ["wm.LiveForm", {"alwaysPopulateEditors":true,"captionSize":"100px","height":"100%","horizontalAlign":"left","liveEditing":false,"margin":"4","verticalAlign":"top"}, {"onDeleteData":"Project_List","onInsertData":"analysisInsert","onSuccess":"projectLivePanel1.popupLiveFormSuccess"}, {
							binding: ["wm.Binding", {}, {}, {
								wire: ["wm.Wire", {"expression":undefined,"source":"projectDojoGrid.selectedItem","targetProperty":"dataSet"}, {}]
							}],
							nameEditor1Panel: ["wm.Panel", {"height":"34px","horizontalAlign":"left","layoutKind":"left-to-right","verticalAlign":"middle","width":"100%"}, {}, {
								nameEditor1: ["wm.Text", {"border":"0","caption":"Name","changeOnKey":true,"dataValue":"Phase 2","desktopHeight":"26px","emptyValue":"emptyString","formField":"name","height":"26px","required":true,"width":"100%"}, {}],
								projectDeleteButton: ["wm.Button", {"caption":"Delete","hint":"Delete this project","imageIndex":21,"imageList":"app.silkIconList","margin":"4"}, {"onclick":"projectLiveForm1.deleteData"}, {
									binding: ["wm.Binding", {}, {}, {
										wire: ["wm.Wire", {"source":"projectDojoGrid.emptySelection","targetId":null,"targetProperty":"disabled"}, {}]
									}]
								}],
								projectSaveButton: ["wm.Button", {"caption":"Save","imageIndex":0,"imageList":"app.silkIconList","margin":"4"}, {"onclick":"projectLiveForm1.saveDataIfValid"}, {
									binding: ["wm.Binding", {}, {}, {
										wire: ["wm.Wire", {"expression":"${projectLiveForm1.invalid} || !${projectLiveForm1.isDirty}","targetId":null,"targetProperty":"disabled"}, {}]
									}]
								}]
							}],
							descriptionEditor1: ["wm.LargeTextArea", {"border":"0","caption":"Description","captionAlign":"right","captionPosition":"left","captionSize":"100px","changeOnKey":true,"dataValue":"KM Strategy & Transformation Roadmap","emptyValue":"emptyString","formField":"description","width":"100%"}, {}],
							panel1: ["wm.Panel", {"height":"100%","horizontalAlign":"left","margin":"15,0,0,100","verticalAlign":"top","width":"100%"}, {}, {
								button1: ["wm.Panel", {"fitToContentHeight":true,"height":"34px","horizontalAlign":"left","layoutKind":"left-to-right","verticalAlign":"top","width":"100%"}, {}, {
									button2: ["wm.Button", {"caption":"Interviews","imageIndex":27,"imageList":"app.silkIconList","margin":"4","width":"100px"}, {"onclick":"recentInterviewsVariable1","onclick1":"upcomingInterviewsVariable1","onclick2":"resourcesVariable1","onclick3":"Edit_Interviews"}, {
										binding: ["wm.Binding", {}, {}, {
											wire: ["wm.Wire", {"expression":undefined,"source":"projectLiveForm1.isDirty","targetProperty":"disabled"}, {}]
										}]
									}],
									html1: ["wm.Html", {"autoSizeHeight":true,"height":"22px","html":"Schedule and do some interviews to gather issues and flows.","minDesktopHeight":15,"padding":"5,0,0"}, {}]
								}],
								button3: ["wm.Panel", {"fitToContentHeight":true,"height":"34px","horizontalAlign":"left","layoutKind":"left-to-right","verticalAlign":"top","width":"100%"}, {}, {
									button4: ["wm.Button", {"caption":"Summary","imageIndex":23,"imageList":"app.silkIconList","margin":"4","width":"100px"}, {"onclick":"docCatIssuesVariable1","onclick1":"issueCategoryCount","onclick2":"flowsByProjectVariable1","onclick3":"approachIssueCountVariable1","onclick4":"issueLiveVariable1","onclick5":"Edit_Summary"}, {
										binding: ["wm.Binding", {}, {}, {
											wire: ["wm.Wire", {"expression":undefined,"source":"projectLiveForm1.isDirty","targetProperty":"disabled"}, {}]
										}]
									}],
									html2: ["wm.Html", {"autoSizeHeight":true,"height":"22px","html":"Make sense of the interview data.","minDesktopHeight":15,"padding":"5,0,0"}, {}]
								}],
								button5: ["wm.Panel", {"fitToContentHeight":true,"height":"34px","horizontalAlign":"left","layoutKind":"left-to-right","verticalAlign":"top","width":"100%"}, {}, {
									button8: ["wm.Button", {"caption":"Solutions","imageIndex":19,"imageList":"app.silkIconList","margin":"4","width":"100px"}, {"onclick":"plansVariable1","onclick1":"Edit_Solutions"}, {
										binding: ["wm.Binding", {}, {}, {
											wire: ["wm.Wire", {"expression":undefined,"source":"projectLiveForm1.isDirty","targetProperty":"disabled"}, {}]
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
					Edit_Summary: ["wm.Layer", {"borderColor":"","caption":"Summary","horizontalAlign":"left","themeStyleType":"ContentPanel","verticalAlign":"top"}, {"onShow":"summaryIssuesGrid1.deselectAll","onShow1":"summaryFlowsGrid1.deselectAll","onShow2":"summaryAppIssuesGrid1.deselectAll","onShow3":"summaryDocIssuesGrid1.deselectAll"}, {
						binding: ["wm.Binding", {}, {}, {
							wire: ["wm.Wire", {"expression":undefined,"source":"projectDojoGrid.isRowSelected","targetProperty":"showing"}, {}]
						}],
						tabLayers2: ["wm.TabLayers", {}, {}, {
							layer4: ["wm.Layer", {"horizontalAlign":"left","themeStyleType":"ContentPanel","verticalAlign":"top"}, {}, {
								binding: ["wm.Binding", {}, {}, {
									wire: ["wm.Wire", {"expression":"\"Issues (\" + ${issueLiveVariable1.count} + \")\"","targetProperty":"caption"}, {}]
								}],
								html9: ["wm.Html", {"autoSizeHeight":true,"height":"48px","html":"<div class=\"note\">Review all unresolved issues (where not everyone agrees that they are fixed).</div>","minDesktopHeight":15}, {}],
								dojoChart1Panel: ["wm.Panel", {"height":"200px","horizontalAlign":"left","layoutKind":"left-to-right","verticalAlign":"top","width":"100%"}, {}, {
									dojoChart1: ["wm.DojoChart", {"chartType":"Pie","hideLegend":true,"legendHeight":"0px","padding":"4","theme":"PlotKit.blue","xAxis":"name","yAxis":"name,issues","ydisplay":"Number"}, {}, {
										binding: ["wm.Binding", {}, {}, {
											wire: ["wm.Wire", {"expression":undefined,"source":"issueCategoryCount","targetProperty":"dataSet"}, {}]
										}],
										yformat: ["wm.NumberFormatter", {}, {}]
									}],
									issueCategoryGrid1: ["wm.DojoGrid", {"columns":[{"show":false,"field":"PHONE COLUMN","title":"-","width":"100%","align":"left","editorProps":{"restrictValues":true},"expression":"\"<div class='MobileRowTitle'>Category: \" + ${category.name} + \"</div>\"\n+ \"<div class='MobileRow'>Issues: \" + ${issues} + \"</div>\"\n","mobileColumn":true},{"show":false,"field":"category.id","title":"Category.id","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"category.project.id","title":"Category.project.id","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"category.project.description","title":"Category.project.description","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"category.project.name","title":"Category.project.name","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"category.project.version","title":"Category.project.version","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":true,"field":"category.name","title":"Category","width":"100%","align":"left","formatFunc":"","editorProps":{"restrictValues":true},"mobileColumn":false},{"show":false,"field":"category.version","title":"Category.version","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":true,"field":"issues","title":"Issues","width":"60px","align":"center","formatFunc":"","editorProps":{"restrictValues":true},"mobileColumn":false},{"show":false,"field":"name","title":"Name","width":"100%","align":"left","formatFunc":"","mobileColumn":false}],"dsType":"com.analystdb.data.output.IssueCategoryCountsRtnType","margin":"4","minDesktopHeight":60,"singleClickEdit":true}, {"onSelect":"issueByCategoriesVariable1"}, {
										binding: ["wm.Binding", {}, {}, {
											wire: ["wm.Wire", {"expression":undefined,"source":"issueCategoryCount","targetProperty":"dataSet"}, {}]
										}]
									}]
								}],
								summaryIssuesGrid1: ["wm.DojoGrid", {"columns":[{"show":true,"field":"issue.sequence","title":"Issue","width":"50px","align":"center","formatFunc":"","expression":"\"I-\" + ${issue.sequence}","mobileColumn":false},{"show":true,"field":"issue.description","title":"Description","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"PHONE COLUMN","title":"-","width":"100%","align":"left","expression":"\"<div class='MobileRowTitle'>Description: \" + ${issue.description} + \"</div>\"\n","mobileColumn":true},{"show":false,"field":"id.issueId","title":"Id.issueId","width":"80px","displayType":"Number","align":"right","formatFunc":""},{"show":false,"field":"id.issueCategoryId","title":"Id.issueCategoryId","width":"80px","displayType":"Number","align":"right","formatFunc":""},{"show":false,"field":"issueCategory.id","title":"IssueCategory.id","width":"80px","displayType":"Number","align":"right","formatFunc":""},{"show":false,"field":"issueCategory.name","title":"IssueCategory.name","width":"100%","displayType":"Text","align":"left","formatFunc":""},{"show":false,"field":"issueCategory.version","title":"IssueCategory.version","width":"80px","displayType":"Number","align":"right","formatFunc":""}],"dsType":"com.analystdb.data.IssueIssueCategory","height":"100%","margin":"4","minDesktopHeight":60,"singleClickEdit":true}, {"onSelect":"Edit_Issue","onShow":"summaryIssuesGrid1.deselectAll"}, {
									binding: ["wm.Binding", {}, {}, {
										wire: ["wm.Wire", {"expression":undefined,"source":"issueByCategoriesVariable1","targetProperty":"dataSet"}, {}],
										wire1: ["wm.Wire", {"expression":undefined,"source":"issueCategoryGrid1.isRowSelected","targetProperty":"showing"}, {}]
									}]
								}]
							}],
							layer5: ["wm.Layer", {"horizontalAlign":"left","themeStyleType":"ContentPanel","verticalAlign":"top"}, {}, {
								binding: ["wm.Binding", {}, {}, {
									wire: ["wm.Wire", {"expression":"\"Flows (\" +${flowsByProjectVariable1.count} + \")\"","targetProperty":"caption"}, {}]
								}],
								html10: ["wm.Html", {"autoSizeHeight":true,"height":"48px","html":"<div class=\"note\">Review all current flows.</div>","minDesktopHeight":15}, {}],
								summaryFlowsGrid1: ["wm.DojoGrid", {"columns":[{"show":false,"field":"id","title":"Id","width":"80px","align":"right","formatFunc":"","mobileColumn":false},{"show":true,"field":"fromActor","title":"From","width":"50%","align":"left","formatFunc":"","editorProps":{"restrictValues":true},"expression":"if ( ${fromActor}  ) { ${fromActor} } else {  \" * unspecified *\"; }","mobileColumn":false},{"show":true,"field":"toActor","title":"To","width":"50%","align":"left","formatFunc":"","editorProps":{"restrictValues":true},"expression":"if ( ${toActor}  ) { ${toActor} } else {  \" * unspecified *\"; }","mobileColumn":false},{"show":true,"field":"name","title":"Subject","width":"100%","align":"left","formatFunc":"","editorProps":{"restrictValues":true},"mobileColumn":false},{"show":false,"field":"description","title":"Description","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"version","title":"Version","width":"80px","align":"right","formatFunc":"","mobileColumn":false},{"show":false,"field":"PHONE COLUMN","title":"-","width":"100%","align":"left","expression":"\"<div class='MobileRowTitle'>Subject: \" + ${name} + \"</div>\"\n","mobileColumn":true}],"height":"100%","margin":"4","minDesktopHeight":60,"primaryKeyFields":["id"],"singleClickEdit":true}, {"onSelect":"View_Flow"}, {
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
								dojoGrid6: ["wm.DojoGrid", {"columns":[{"show":false,"field":"PHONE COLUMN","title":"-","width":"100%","align":"left","expression":"\"<div class='MobileRowTitle'>Approach: \" + ${approach.name} + \"</div>\"\n+ \"<div class='MobileRow'>Description: \" + ${approach.description} + \"</div>\"\n+ \"<div class='MobileRow'>Issues: \" + ${issues} + \"</div>\"\n","mobileColumn":true},{"show":false,"field":"approach.id","title":"Approach.id","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"approach.interview.id","title":"Approach.interview.id","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"approach.interview.scheduled","title":"Approach.interview.scheduled","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"approach.interview.done","title":"Approach.interview.done","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"approach.interview.resource.id","title":"Approach.interview.resource.id","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"approach.interview.resource.project.id","title":"Approach.interview.resource.project.id","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"approach.interview.resource.project.description","title":"Approach.interview.resource.project.description","width":"100%","align":"left","formatFunc":"","editorProps":{"restrictValues":true},"mobileColumn":false},{"show":false,"field":"approach.interview.resource.project.name","title":"Approach.interview.resource.project.name","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"approach.interview.resource.project.version","title":"Approach.interview.resource.project.version","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"approach.interview.resource.description","title":"Approach.interview.resource.description","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"approach.interview.resource.name","title":"Approach.interview.resource.name","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"approach.interview.resource.version","title":"Approach.interview.resource.version","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"approach.interview.notes","title":"Approach.interview.notes","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"approach.interview.version","title":"Approach.interview.version","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":true,"field":"approach.name","title":"Approach","width":"25%","align":"left","formatFunc":"","editorProps":{"restrictValues":true},"mobileColumn":false},{"show":true,"field":"approach.description","title":"Description","width":"100%","align":"left","formatFunc":"","editorProps":{"restrictValues":true},"mobileColumn":false},{"show":false,"field":"approach.version","title":"Approach.version","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":true,"field":"issues","title":"Issues","width":"50px","align":"center","formatFunc":"","editorProps":{"restrictValues":true},"mobileColumn":false}],"height":"100%","margin":"4","minDesktopHeight":60,"singleClickEdit":true}, {"onSelect":"approachIssuesVariable1"}, {
									binding: ["wm.Binding", {}, {}, {
										wire: ["wm.Wire", {"expression":undefined,"source":"approachIssueCountVariable1","targetProperty":"dataSet"}, {}]
									}]
								}],
								summaryAppIssuesGrid1: ["wm.DojoGrid", {"columns":[{"show":false,"field":"issue.id","title":"Id","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"issue.project.id","title":"Id","width":"100%","align":"left","formatFunc":"","editorProps":{"restrictValues":true},"mobileColumn":false},{"show":false,"field":"issue.project.description","title":"Description","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"issue.project.name","title":"Name","width":"100%","align":"left","formatFunc":"","editorProps":{"restrictValues":true},"mobileColumn":false},{"show":false,"field":"issue.project.version","title":"Version","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":true,"field":"issue.sequence","title":"Issue","width":"50px","align":"center","formatFunc":"","editorProps":{"restrictValues":true},"expression":"\"I-\" + ${issue.sequence}","mobileColumn":false},{"show":true,"field":"issue.description","title":"Description","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"issue.name","title":"Name","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"issue.version","title":"Version","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"PHONE COLUMN","title":"-","width":"100%","align":"left","expression":"\"<div class='MobileRowTitle'>Description: \" + ${issue.description} + \"</div>\"\n","mobileColumn":true}],"dsType":"com.analystdb.data.output.ApproachIssuesRtnType","height":"100%","margin":"4","minDesktopHeight":60,"singleClickEdit":true}, {"onSelect":"View_Issue"}, {
									binding: ["wm.Binding", {}, {}, {
										wire: ["wm.Wire", {"expression":undefined,"source":"approachIssuesVariable1","targetProperty":"dataSet"}, {}],
										wire1: ["wm.Wire", {"expression":undefined,"source":"dojoGrid6.isRowSelected","targetProperty":"showing"}, {}]
									}]
								}]
							}],
							layer9: ["wm.Layer", {"caption":"Documents","horizontalAlign":"left","themeStyleType":"ContentPanel","verticalAlign":"top"}, {}, {
								html14: ["wm.Html", {"autoSizeHeight":true,"height":"48px","html":"<div class=\"note\">Document-centric view of pending issues.</div>","minDesktopHeight":15}, {}],
								dojoChart2Panel: ["wm.Panel", {"height":"200px","horizontalAlign":"left","layoutKind":"left-to-right","verticalAlign":"top","width":"100%"}, {}, {
									dojoChart2: ["wm.DojoChart", {"chartType":"Pie","hideLegend":true,"legendHeight":"0px","padding":"4","theme":"PlotKit.blue","xAxis":"phase","yAxis":"issues"}, {}, {
										binding: ["wm.Binding", {}, {}, {
											wire: ["wm.Wire", {"expression":undefined,"source":"docCatIssuesVariable1","targetProperty":"dataSet"}, {}]
										}]
									}],
									dojoGrid2: ["wm.DojoGrid", {"columns":[{"show":true,"field":"phase","title":"Phase","width":"100%","align":"left","formatFunc":"","editorProps":{"restrictValues":true},"mobileColumn":false},{"show":true,"field":"issues","title":"Issues","width":"40px","align":"center","formatFunc":"","editorProps":{"restrictValues":true},"mobileColumn":false},{"show":false,"field":"PHONE COLUMN","title":"-","width":"100%","align":"left","expression":"\"<div class='MobileRowTitle'>Phase: \" + ${phase} + \"</div>\"\n+ \"<div class='MobileRow'>Issues: \" + ${issues} + \"</div>\"\n","mobileColumn":true},{"show":false,"field":"category.id","title":"Category.id","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"category.sequence","title":"Category.sequence","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"category.name","title":"Category.name","width":"100%","align":"left","formatFunc":"","mobileColumn":false}],"dsType":"com.analystdb.data.output.DocumentCategoryIssueCountsRtnType","margin":"4","minDesktopHeight":60,"singleClickEdit":true}, {"onSelect":"docIssuesCountsVariable1"}, {
										binding: ["wm.Binding", {}, {}, {
											wire: ["wm.Wire", {"expression":undefined,"source":"docCatIssuesVariable1","targetProperty":"dataSet"}, {}]
										}]
									}],
									dojoGrid3: ["wm.DojoGrid", {"columns":[{"show":true,"field":"doc","title":"Document","width":"100%","align":"left","formatFunc":"","editorProps":{"restrictValues":true},"mobileColumn":false},{"show":true,"field":"issues","title":"Issues","width":"40px","align":"center","formatFunc":"","editorProps":{"restrictValues":true},"mobileColumn":false},{"show":false,"field":"PHONE COLUMN","title":"-","width":"100%","align":"left","expression":"\"<div class='MobileRowTitle'>Document: \" + ${doc} + \"</div>\"\n+ \"<div class='MobileRow'>Issues: \" + ${issues} + \"</div>\"\n","mobileColumn":true},{"show":false,"field":"id","title":"Id","width":"100%","align":"left","formatFunc":"","mobileColumn":false}],"dsType":"com.analystdb.data.output.DocumentIssueCountRtnType","margin":"4","minDesktopHeight":60,"singleClickEdit":true}, {"onSelect":"docIssuesVariable1"}, {
										binding: ["wm.Binding", {}, {}, {
											wire: ["wm.Wire", {"expression":undefined,"source":"docIssuesCountsVariable1","targetProperty":"dataSet"}, {}],
											wire1: ["wm.Wire", {"expression":undefined,"source":"dojoGrid2.isRowSelected","targetProperty":"showing"}, {}]
										}]
									}]
								}],
								summaryDocIssuesGrid1: ["wm.DojoGrid", {"columns":[{"show":false,"field":"c0.id","title":"Id","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"c0.project.id","title":"Id","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"c0.project.description","title":"Description","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"c0.project.name","title":"Name","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"c0.project.version","title":"Version","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":true,"field":"c0.sequence","title":"Issue","width":"50px","align":"center","formatFunc":"","editorProps":{"restrictValues":true},"expression":"\"I-\" + ${c0.sequence}","mobileColumn":false},{"show":true,"field":"c0.description","title":"Description","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"c0.name","title":"Name","width":"100%","align":"left","formatFunc":"","editorProps":{"restrictValues":true},"mobileColumn":false},{"show":false,"field":"c0.version","title":"Version","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"PHONE COLUMN","title":"-","width":"100%","align":"left","expression":"\"<div class='MobileRowTitle'>Description: \" + ${c0.description} + \"</div>\"\n","mobileColumn":true}],"dsType":"com.analystdb.data.output.DocumentIssuesRtnType","height":"100%","margin":"4","minDesktopHeight":60,"singleClickEdit":true}, {"onSelect":"View_Issue1"}, {
									binding: ["wm.Binding", {}, {}, {
										wire: ["wm.Wire", {"expression":undefined,"source":"docIssuesVariable1","targetProperty":"dataSet"}, {}],
										wire1: ["wm.Wire", {"expression":undefined,"source":"dojoGrid3.isRowSelected","targetProperty":"showing"}, {}]
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
							planDojoGrid: ["wm.DojoGrid", {"columns":[{"show":false,"field":"PHONE COLUMN","title":"-","width":"100%","align":"left","editorProps":{"restrictValues":true},"expression":"\"<div class='MobileRowTitle'>Solution: \" + ${plan.name} + \"</div>\"\n+ \"<div class='MobileRow'>Description: \" + ${plan.description} + \"</div>\"\n+ \"<div class='MobileRow'>Approved: \" + ${plan.approved} + \"</div>\"\n","mobileColumn":true},{"show":false,"field":"plan.id","title":"Plan.id","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":true,"field":"plan.name","title":"Solution","width":"25%","align":"left","formatFunc":"","editorProps":{"restrictValues":true},"mobileColumn":false},{"show":true,"field":"plan.description","title":"Description","width":"100%","align":"left","formatFunc":"","editorProps":{"restrictValues":true},"mobileColumn":false},{"show":false,"field":"plan.analysis.id","title":"Plan.analysis.id","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"analysis","title":"Analysis","width":"50%","align":"left","formatFunc":"","editorProps":{"restrictValues":true},"mobileColumn":false},{"show":true,"field":"plan.approved","title":"Approved","width":"60px","align":"left","formatFunc":"","fieldType":"dojox.grid.cells.Bool","editorProps":{"restrictValues":true},"mobileColumn":false},{"show":false,"field":"plan.analysis.project.id","title":"Plan.analysis.project.id","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"plan.analysis.project.description","title":"Plan.analysis.project.description","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"plan.analysis.project.name","title":"Plan.analysis.project.name","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"plan.analysis.project.version","title":"Plan.analysis.project.version","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"plan.analysis.description","title":"Plan.analysis.description","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"plan.analysis.name","title":"Plan.analysis.name","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"plan.analysis.version","title":"Plan.analysis.version","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"plan.version","title":"Plan.version","width":"100%","align":"left","formatFunc":"","mobileColumn":false}],"height":"100%","margin":"4"}, {"onSelect":"approachLiveVariable1","onSelect1":"analystDBLivePanel.popupLivePanelEdit"}, {
								binding: ["wm.Binding", {}, {}, {
									wire: ["wm.Wire", {"expression":undefined,"source":"plansVariable1","targetProperty":"dataSet"}, {}]
								}]
							}],
							planNewButton: ["wm.Button", {"_classes":{"domNode":["iconButton"]},"caption":undefined,"imageIndex":1,"imageList":"app.silkIconList","margin":"4","width":"32px"}, {"onclick":"analystDBLivePanel.popupLivePanelInsert"}]
						}]
					}],
					Edit_Documents: ["wm.Layer", {"borderColor":"","caption":"Documents","horizontalAlign":"left","showing":false,"themeStyleType":"ContentPanel","verticalAlign":"top"}, {}, {
						documentsLivePanel1: ["wm.LivePanel", {"horizontalAlign":"left","verticalAlign":"top"}, {}, {
							binding: ["wm.Binding", {}, {}, {
								wire: ["wm.Wire", {"source":"documentsDojoGrid","targetId":null,"targetProperty":"dataGrid"}, {}]
							}],
							html5: ["wm.Html", {"autoSizeHeight":true,"height":"48px","html":"<div class=\"note\">Manage the list of global documents referred to by flows and issues.</div>","minDesktopHeight":15}, {}],
							documentsDojoGridPanel: ["wm.Panel", {"height":"100%","horizontalAlign":"left","layoutKind":"left-to-right","verticalAlign":"top","width":"100%"}, {}, {
								documentsDojoGrid: ["wm.DojoGrid", {"columns":[{"show":true,"field":"documentCategory.name","title":"Phase","width":"25%","align":"left","formatFunc":"","fieldType":"dojox.grid.cells.ComboBox","editorProps":{"selectDataSet":"documentcategoryLiveVariable1","displayField":"name","restrictValues":true},"mobileColumn":false},{"show":true,"field":"document","title":"Document","width":"100%","align":"left","formatFunc":"","fieldType":"dojox.grid.cells._Widget","editorProps":{"restrictValues":true},"mobileColumn":false},{"show":false,"field":"PHONE COLUMN","title":"-","width":"100%","align":"left","expression":"\"<div class='MobileRowTitle'>Phase: \" + ${documentCategory.name} + \"</div>\"\n+ \"<div class='MobileRow'>Document: \" + ${document} + \"</div>\"\n","mobileColumn":true},{"show":false,"field":"id","title":"Id","width":"80px","align":"right","formatFunc":"","mobileColumn":false}],"deleteColumn":true,"height":"100%","liveEditing":true,"margin":"4","minHeight":0,"minWidth":0,"singleClickEdit":true}, {}, {
									binding: ["wm.Binding", {}, {}, {
										wire: ["wm.Wire", {"expression":undefined,"source":"documentsLiveVariable1","targetProperty":"dataSet"}, {}]
									}]
								}],
								documentsNewButton: ["wm.Button", {"_classes":{"domNode":["iconButton"]},"caption":undefined,"imageIndex":1,"imageList":"app.silkIconList","margin":"4","width":"32px"}, {"onclick":"documentsDojoGrid.addEmptyRow"}]
							}]
						}],
						button9: ["wm.Panel", {"fitToContentHeight":true,"height":"34px","horizontalAlign":"left","layoutKind":"left-to-right","verticalAlign":"top","width":"100%"}, {}, {
							button10: ["wm.Button", {"caption":"Phases","margin":"4"}, {"onclick":"Edit_Doc_Category"}],
							html13: ["wm.Html", {"autoSizeHeight":true,"height":"22px","html":"Manage document categories.","minDesktopHeight":15,"padding":"5,0,0"}, {}]
						}]
					}],
					Edit_Doc_Category: ["wm.Layer", {"borderColor":"","caption":"Phases","horizontalAlign":"left","showing":false,"themeStyleType":"ContentPanel","verticalAlign":"top"}, {}, {
						analystDBLivePanel2: ["wm.LivePanel", {"horizontalAlign":"left","layoutKind":"left-to-right","verticalAlign":"top"}, {}, {
							binding: ["wm.Binding", {}, {}, {
								wire: ["wm.Wire", {"source":"documentcategoryDojoGrid","targetId":null,"targetProperty":"dataGrid"}, {}]
							}],
							documentcategoryDojoGrid: ["wm.DojoGrid", {"columns":[{"show":true,"field":"name","title":"Name","width":"100%","align":"left","formatFunc":"","fieldType":"dojox.grid.cells._Widget","mobileColumn":false},{"show":false,"field":"PHONE COLUMN","title":"-","width":"100%","align":"left","expression":"\"<div class='MobileRowTitle'>Name: \" + ${name} + \"</div>\"\n","mobileColumn":true},{"show":false,"field":"id","title":"Id","width":"80px","displayType":"Number","align":"right","formatFunc":""},{"show":false,"field":"sequence","title":"Sequence","width":"80px","displayType":"Number","align":"right","formatFunc":""}],"deleteColumn":true,"dsType":"com.analystdb.data.DocumentCategory","height":"100%","liveEditing":true,"margin":"4","singleClickEdit":true}, {"onLiveEditUpdateSuccess":"documentcategoryLiveVariable1"}, {
								binding: ["wm.Binding", {}, {}, {
									wire: ["wm.Wire", {"source":"documentcategoryLiveVariable1","targetProperty":"dataSet"}, {}]
								}]
							}],
							documentcategoryNewButton: ["wm.Button", {"_classes":{"domNode":["iconButton"]},"caption":undefined,"imageIndex":1,"imageList":"app.silkIconList","margin":"4","width":"32px"}, {"onclick":"documentcategoryDojoGrid.addEmptyRow"}]
						}]
					}],
					Edit_Interviews: ["wm.Layer", {"borderColor":"","caption":"Interviews","horizontalAlign":"left","themeStyleType":"ContentPanel","verticalAlign":"top"}, {"onShow":"interviewDojoGrid1.deselectAll"}, {
						binding: ["wm.Binding", {}, {}, {
							wire: ["wm.Wire", {"expression":undefined,"source":"projectDojoGrid.isRowSelected","targetProperty":"showing"}, {}]
						}],
						tabLayers1: ["wm.TabLayers", {}, {}, {
							layer2: ["wm.Layer", {"horizontalAlign":"left","themeStyleType":"ContentPanel","verticalAlign":"top"}, {}, {
								binding: ["wm.Binding", {}, {}, {
									wire: ["wm.Wire", {"expression":"if ( ${upcomingGrid1.dataSet.data} ) { \"Scheduled  (\" + ${upcomingGrid1.dataSet.data.length} + \")\"; } else { \"Scheduled\"; }","targetProperty":"caption"}, {}]
								}],
								html8: ["wm.Html", {"autoSizeHeight":true,"height":"48px","html":"<div class=\"note\">Manage upcoming interviews.</div>","minDesktopHeight":15}, {}],
								upcomingGrid1Panel: ["wm.Panel", {"height":"100%","horizontalAlign":"left","layoutKind":"left-to-right","verticalAlign":"top","width":"100%"}, {}, {
									upcomingGrid1: ["wm.DojoGrid", {"columns":[{"show":false,"field":"PHONE COLUMN","title":"-","width":"100%","align":"left","editorProps":{"restrictValues":true},"expression":"\"<div class='MobileRowTitle'>Scheduled: \" + wm.List.prototype.dateFormatter({\"useLocalTime\":false,\"datePattern\":\"MM/dd/y\",\"timePattern\":\"hh:mm a  (EEE)\",\"formatLength\":\"short\",\"dateType\":\"date and time\"}, null,null,null,${interview.scheduled}) + \"</div>\"\n+ \"<div class='MobileRow'> Name: \" + ${name} + \"</div>\"\n","mobileColumn":true},{"show":false,"field":"interview.id","title":"Interview.id","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":true,"field":"interview.scheduled","title":"Scheduled","width":"150px","align":"left","formatFunc":"wm_date_formatter","formatProps":{"useLocalTime":false,"datePattern":"MM/dd/y","timePattern":"hh:mm a  (EEE)","formatLength":"short","dateType":"date and time"},"editorProps":{"restrictValues":true},"mobileColumn":false},{"show":false,"field":"interview.resource.id","title":"Interview.resource.id","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"interview.resource.project.id","title":"Interview.resource.project.id","width":"100%","align":"left","formatFunc":"","editorProps":{"restrictValues":true},"mobileColumn":false},{"show":false,"field":"interview.resource.project.description","title":"Interview.resource.project.description","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"interview.resource.project.name","title":"Interview.resource.project.name","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"interview.resource.project.version","title":"Interview.resource.project.version","width":"100%","align":"left","formatFunc":"","editorProps":{"restrictValues":true},"mobileColumn":false},{"show":false,"field":"interview.resource.description","title":"Interview.resource.description","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"interview.resource.name","title":"Interview.resource.name","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"interview.resource.version","title":"Interview.resource.version","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"interview.version","title":"Interview.version","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":true,"field":"name","title":" Name","width":"100%","align":"left","formatFunc":"","editorProps":{"restrictValues":true},"mobileColumn":false},{"show":false,"field":"interview.done","title":"Interview.done","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"interview.notes","title":"Interview.notes","width":"100%","displayType":"Java.lang.String","align":"left","formatFunc":""},{"show":false,"field":"scheduled","title":"Scheduled","width":"100%","displayType":"Java.util.Date","align":"left","formatFunc":""}],"dsType":"com.analystdb.data.output.UpcomingInterviewsRtnType","height":"100%","margin":"4","minDesktopHeight":60,"singleClickEdit":true}, {}, {
										binding: ["wm.Binding", {}, {}, {
											wire: ["wm.Wire", {"expression":undefined,"source":"upcomingInterviewsVariable1","targetProperty":"dataSet"}, {}]
										}]
									}],
									button10Panel: ["wm.Panel", {"height":"66px","horizontalAlign":"left","verticalAlign":"top","width":"100px"}, {}, {
										interviewButton: ["wm.Button", {"caption":"Interview!","imageIndex":27,"imageList":"app.silkIconList","margin":"4","width":"100%"}, {"onclick":"interviewButtonClick","onclick3":"Edit_Interview"}, {
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
							}],
							layer1: ["wm.Layer", {"horizontalAlign":"left","themeStyleType":"ContentPanel","verticalAlign":"top"}, {}, {
								binding: ["wm.Binding", {}, {}, {
									wire: ["wm.Wire", {"expression":"if ( ${interviewDojoGrid1.dataSet.data} ) { \"Recent  (\" + ${interviewDojoGrid1.dataSet.data.length} + \")\"; } else { \"Recent\"; }","targetProperty":"caption"}, {}]
								}],
								html7: ["wm.Html", {"autoSizeHeight":true,"height":"48px","html":"<div class=\"note\">Most recent interviews from each resource.</div>","minDesktopHeight":15}, {}],
								interviewDojoGrid1: ["wm.DojoGrid", {"columns":[{"show":false,"field":"interview.id","title":"Id","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":true,"field":"interview.scheduled","title":"Date","width":"100px","align":"left","formatFunc":"wm_date_formatter","editorProps":{"restrictValues":true},"mobileColumn":false},{"show":false,"field":"interview.resource.id","title":"Id","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"interview.resource.project.id","title":"Id","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"interview.resource.project.description","title":"Description","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"interview.resource.project.name","title":"Name","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"interview.resource.project.version","title":"Version","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"interview.resource.description","title":"Description","width":"100%","align":"left","formatFunc":"","editorProps":{"restrictValues":true},"mobileColumn":false},{"show":false,"field":"interview.resource.name","title":"Name","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"interview.resource.version","title":"Version","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"interview.version","title":"Version","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"PHONE COLUMN","title":"-","width":"100%","align":"left","expression":"\"<div class='MobileRowTitle'>Date: \" + wm.List.prototype.dateFormatter({}, null,null,null,${interview.scheduled}) + \"</div>\"\n+ \"<div class='MobileRow'>Resource: \" + ${resource} + \"</div>\"\n","mobileColumn":true},{"show":false,"field":"interview.done","title":"Interview.done","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"interview.notes","title":"Interview.notes","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":true,"field":"resource","title":"Resource","width":"100%","align":"left","formatFunc":"","editorProps":{"restrictValues":true},"mobileColumn":false},{"show":true,"field":"issues","title":"Issues","width":"80px","align":"center","editorProps":{"restrictValues":true},"expression":"if ( ${issues} == 0 ) { \"\" } else { ${issues} }","mobileColumn":false},{"show":true,"field":"approaches","title":"Approaches","width":"80px","align":"center","editorProps":{"restrictValues":true},"expression":"if ( ${approaches} == 0 ) { \"\" } else { ${approaches} }","mobileColumn":false},{"show":true,"field":"flows","title":"Flows","width":"80px","align":"center","editorProps":{"restrictValues":true},"expression":"if ( ${flows} == 0 ) { \"\" } else { ${flows} }","mobileColumn":false},{"show":false,"field":"id","title":"Id","width":"100%","displayType":"Java.lang.Long","align":"left","formatFunc":""}],"dsType":"com.analystdb.data.output.RecentInterviewsRtnType","height":"100%","margin":"4","minDesktopHeight":60,"primaryKeyFields":["id"],"singleClickEdit":true}, {"onSelect":"approachLiveVariable1","onSelect3":"Edit_Interview"}, {
									binding: ["wm.Binding", {}, {}, {
										wire: ["wm.Wire", {"expression":undefined,"source":"recentInterviewsVariable1","targetProperty":"dataSet"}, {}]
									}]
								}]
							}]
						}]
					}],
					Edit_Interview: ["wm.Layer", {"borderColor":"","horizontalAlign":"left","themeStyleType":"ContentPanel","verticalAlign":"top"}, {"onShow":"flowDojoGrid.deselectAll","onShow1":"dataGrid2.deselectAll","onShow2":"approachDojoGrid.deselectAll"}, {
						binding: ["wm.Binding", {}, {}, {
							wire1: ["wm.Wire", {"expression":undefined,"source":"interviewDojoGrid1.isRowSelected","targetProperty":"showing"}, {}],
							wire: ["wm.Wire", {"expression":undefined,"source":"nameEditor6.dataValue","targetProperty":"caption"}, {}]
						}],
						interviewForm1: ["wm.LiveForm", {"height":"100%","horizontalAlign":"left","liveEditing":false,"operation":"update","verticalAlign":"top"}, {}, {
							binding: ["wm.Binding", {}, {}, {
								wire: ["wm.Wire", {"expression":undefined,"source":"interviewDojoGrid1.selectedItem.interview","targetProperty":"dataSet"}, {}],
								wire2: ["wm.Wire", {"expression":undefined,"source":"relatedEditor5.dataOutput","targetProperty":"dataOutput.approachs"}, {}],
								wire3: ["wm.Wire", {"expression":undefined,"source":"relatedEditor2.dataOutput","targetProperty":"dataOutput.flows"}, {}],
								wire1: ["wm.Wire", {"expression":undefined,"source":"relatedEditor3.dataOutput","targetProperty":"dataOutput.issueComments"}, {}]
							}],
							calendar1Panel: ["wm.Panel", {"height":"172px","horizontalAlign":"left","layoutKind":"left-to-right","padding":"5","verticalAlign":"top","width":"100%"}, {}, {
								relatedEditor1: ["wm.RelatedEditor", {"editingMode":"editable subform","formField":"resource","height":"100%","horizontalAlign":"left","verticalAlign":"top"}, {}, {
									binding: ["wm.Binding", {}, {}, {
										wire: ["wm.Wire", {"expression":undefined,"source":"interviewDojoGrid1.selectedItem.interview.resource","targetProperty":"dataSet"}, {}]
									}],
									nameEditor6: ["wm.Text", {"border":"0","caption":undefined,"captionSize":"140px","dataValue":"","desktopHeight":"26px","emptyValue":"emptyString","formField":"name","height":"26px","required":true,"width":"100%"}, {}],
									relatedEditor4: ["wm.RelatedEditor", {"editingMode":"readonly","formField":"resourceAttributes","height":"100%","horizontalAlign":"left","layoutKind":"left-to-right","verticalAlign":"top"}, {}, {
										binding: ["wm.Binding", {}, {}, {
											wire: ["wm.Wire", {"expression":undefined,"source":"interviewDojoGrid1.selectedItem.interview.resource.resourceAttributes","targetProperty":"dataSet"}, {}],
											wire1: ["wm.Wire", {"expression":undefined,"source":"relatedEditor1.dataOutput","targetProperty":"dataOutput.resource"}, {}]
										}],
										dataGrid1: ["wm.DojoGrid", {"columns":[{"show":false,"field":"id","title":"Id","width":"80px","align":"right","formatFunc":"","editorProps":{"restrictValues":true},"mobileColumn":false},{"show":true,"field":"name","title":"Name","width":"25%","align":"left","formatFunc":"","fieldType":"dojox.grid.cells._Widget","editorProps":{"restrictValues":true},"mobileColumn":false},{"show":true,"field":"val","title":"Val","width":"100%","align":"left","formatFunc":"","fieldType":"dojox.grid.cells._Widget","editorProps":{"restrictValues":true},"mobileColumn":false},{"show":false,"field":"version","title":"Version","width":"80px","align":"right","formatFunc":"","mobileColumn":false},{"show":false,"field":"PHONE COLUMN","title":"-","width":"100%","align":"left","expression":"\"<div class='MobileRowTitle'>Name: \" + ${name} + \"</div>\"\n+ \"<div class='MobileRow'>Val: \" + ${val} + \"</div>\"\n","mobileColumn":true}],"height":"100%","margin":"4","minHeight":0,"noHeader":true,"selectionMode":"multiple","singleClickEdit":true}, {}, {
											binding: ["wm.Binding", {}, {}, {
												wire: ["wm.Wire", {"expression":undefined,"source":"interviewDojoGrid1.selectedItem.interview.resource.resourceAttributes","targetProperty":"dataSet"}, {}]
											}]
										}],
										button11Panel: ["wm.Panel", {"fitToContentHeight":true,"fitToContentWidth":true,"height":"66px","horizontalAlign":"left","verticalAlign":"top","width":"34px"}, {}, {
											button11: ["wm.Button", {"_classes":{"domNode":["iconButton"]},"caption":undefined,"hint":"Add a property","imageIndex":1,"imageList":"app.silkIconList","margin":"4","width":"32px"}, {"onclick":"dataGrid1.addEmptyRow"}],
											button12: ["wm.Button", {"_classes":{"domNode":["iconButton"]},"caption":undefined,"hint":"Remove property","imageIndex":21,"imageList":"app.silkIconList","margin":"4","width":"32px"}, {"onclick":"button12Click"}]
										}]
									}]
								}],
								calendar1: ["wm.dijit.Calendar", {"width":"240px"}, {}, {
									binding: ["wm.Binding", {}, {}, {
										wire: ["wm.Wire", {"expression":undefined,"source":"interviewForm1.dataOutput.scheduled","targetProperty":"dateValue"}, {}]
									}]
								}]
							}],
							tabLayers3: ["wm.TabLayers", {}, {}, {
								layer6: ["wm.Layer", {"horizontalAlign":"left","themeStyleType":"ContentPanel","verticalAlign":"top"}, {}, {
									binding: ["wm.Binding", {}, {}, {
										wire: ["wm.Wire", {"expression":"if ( ${dataGrid2.dataSet.data} ) { \"Issues  (\" + ${dataGrid2.dataSet.data.length} + \")\"; } else { \"Issues\"; }","targetProperty":"caption"}, {}]
									}],
									relatedEditor3: ["wm.RelatedEditor", {"editingMode":"readonly","formField":"issueComments","height":"100%","horizontalAlign":"left","verticalAlign":"top"}, {}, {
										binding: ["wm.Binding", {}, {}, {
											wire: ["wm.Wire", {"expression":undefined,"source":"interviewDojoGrid1.selectedItem.interview.issueComments","targetProperty":"dataSet"}, {}]
										}],
										issuecommentLivePanel1: ["wm.LivePanel", {"autoScroll":false,"horizontalAlign":"left","layoutKind":"left-to-right","verticalAlign":"top"}, {}, {
											binding: ["wm.Binding", {}, {}, {
												wire: ["wm.Wire", {"source":"Issuecomment_List","targetId":null,"targetProperty":"gridLayer"}, {}],
												wire1: ["wm.Wire", {"source":"Edit_Issuecomment","targetId":null,"targetProperty":"detailsLayer"}, {}],
												wire2: ["wm.Wire", {"source":"issuecommentLiveForm1","targetId":null,"targetProperty":"liveForm"}, {}],
												wire3: ["wm.Wire", {"source":"dataGrid2","targetId":null,"targetProperty":"dataGrid"}, {}],
												wire4: ["wm.Wire", {"source":"issuecommentSaveButton","targetId":null,"targetProperty":"saveButton"}, {}]
											}],
											dataGrid2: ["wm.DojoGrid", {"columns":[{"show":true,"field":"id","title":"Issue","width":"50px","align":"center","formatFunc":"","editorProps":null,"expression":"\"I-\" + ${id}","mobileColumn":false},{"show":true,"field":"description","title":"Description","width":"100%","align":"left","formatFunc":"","editorProps":{"restrictValues":true},"mobileColumn":false},{"show":true,"field":"applicableToMe","title":"Applies","width":"50px","align":"left","formatFunc":"","editorProps":{"restrictValues":true},"mobileColumn":false},{"show":true,"field":"fixed","title":"Fixed","width":"50px","align":"left","formatFunc":"","editorProps":{"restrictValues":true},"mobileColumn":false},{"show":false,"field":"PHONE COLUMN","title":"-","width":"100%","align":"left","expression":"\"<div class='MobileRowTitle'>Description: \" + ${description} + \"</div>\"\n+ \"<div class='MobileRow'>Applies: \" + ${applicableToMe} + \"</div>\"\n+ \"<div class='MobileRow'>Fixed: \" + ${fixed} + \"</div>\"\n","mobileColumn":true},{"show":false,"field":"version","title":"Version","width":"80px","align":"right","formatFunc":"","mobileColumn":false}],"height":"100%","margin":"4"}, {"onSelect":"issueFlowsVariable","onSelect1":"issueAttributesVariable","onSelect2":"issueApproachesVariable","onSelect3":"issuecommentLivePanel1.popupLivePanelEdit"}, {
												binding: ["wm.Binding", {}, {}, {
													wire: ["wm.Wire", {"expression":undefined,"source":"interviewDojoGrid1.selectedItem.interview.issueComments","targetProperty":"dataSet"}, {}]
												}]
											}],
											issuecommentNewButton: ["wm.Button", {"_classes":{"domNode":["iconButton"]},"caption":undefined,"imageIndex":1,"imageList":"app.silkIconList","margin":"4","width":"32px"}, {"onclick":"issuecommentLivePanel1.popupLivePanelInsert"}]
										}]
									}]
								}],
								layer7: ["wm.Layer", {"horizontalAlign":"left","layoutKind":"left-to-right","themeStyleType":"ContentPanel","verticalAlign":"top"}, {}, {
									binding: ["wm.Binding", {}, {}, {
										wire: ["wm.Wire", {"expression":"if ( ${flowDojoGrid.dataSet.data} ) { \"Flows  (\" + ${flowDojoGrid.dataSet.data.length} + \")\"; } else { \"Flows\"; }","targetProperty":"caption"}, {}]
									}],
									relatedEditor2: ["wm.RelatedEditor", {"editingMode":"readonly","formField":"flows","height":"100%","horizontalAlign":"left","verticalAlign":"top"}, {}, {
										binding: ["wm.Binding", {}, {}, {
											wire: ["wm.Wire", {"expression":undefined,"source":"interviewDojoGrid1.selectedItem.interview.flows","targetProperty":"dataSet"}, {}]
										}],
										flowLivePanel1: ["wm.LivePanel", {"autoScroll":false,"horizontalAlign":"left","layoutKind":"left-to-right","verticalAlign":"top"}, {}, {
											binding: ["wm.Binding", {}, {}, {
												wire: ["wm.Wire", {"source":"Flow_List","targetId":null,"targetProperty":"gridLayer"}, {}],
												wire1: ["wm.Wire", {"source":"Edit_Flow","targetId":null,"targetProperty":"detailsLayer"}, {}],
												wire2: ["wm.Wire", {"source":"flowLiveForm1","targetId":null,"targetProperty":"liveForm"}, {}],
												wire3: ["wm.Wire", {"source":"flowDojoGrid","targetId":null,"targetProperty":"dataGrid"}, {}],
												wire4: ["wm.Wire", {"source":"flowSaveButton","targetId":null,"targetProperty":"saveButton"}, {}]
											}],
											flowDojoGrid: ["wm.DojoGrid", {"columns":[{"show":false,"field":"id","title":"Id","width":"80px","align":"right","formatFunc":"","mobileColumn":false},{"show":true,"field":"fromActor","title":"From","width":"50%","align":"left","formatFunc":"","editorProps":{"restrictValues":true},"mobileColumn":false},{"show":true,"field":"toActor","title":"To","width":"50%","align":"left","formatFunc":"","editorProps":{"restrictValues":true},"mobileColumn":false},{"show":true,"field":"name","title":"Subject","width":"100%","align":"left","formatFunc":"","editorProps":{"restrictValues":true},"mobileColumn":false},{"show":false,"field":"description","title":"Description","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"version","title":"Version","width":"80px","align":"right","formatFunc":"","mobileColumn":false},{"show":false,"field":"PHONE COLUMN","title":"-","width":"100%","align":"left","expression":"\"<div class='MobileRowTitle'>From: \" + ${fromActor} + \"</div>\"\n+ \"<div class='MobileRow'>To: \" + ${toActor} + \"</div>\"\n+ \"<div class='MobileRow'>Subject: \" + ${name} + \"</div>\"\n","mobileColumn":true}],"height":"100%","margin":"4","minWidth":0,"primaryKeyFields":["id"]}, {"onSelect":"flowIssuesVariable","onSelect1":"flowLivePanel1.popupLivePanelEdit"}, {
												binding: ["wm.Binding", {}, {}, {
													wire: ["wm.Wire", {"expression":undefined,"source":"interviewDojoGrid1.selectedItem.interview.flows","targetProperty":"dataSet"}, {}]
												}]
											}],
											flowNewButton: ["wm.Button", {"_classes":{"domNode":["iconButton"]},"caption":undefined,"imageIndex":1,"imageList":"app.silkIconList","margin":"4","width":"32px"}, {"onclick":"flowLivePanel1.popupLivePanelInsert"}]
										}]
									}]
								}],
								layer8: ["wm.Layer", {"horizontalAlign":"left","themeStyleType":"ContentPanel","verticalAlign":"top"}, {}, {
									binding: ["wm.Binding", {}, {}, {
										wire: ["wm.Wire", {"expression":"if ( ${approachDojoGrid.dataSet.data} ) { \"Approaches  (\" + ${approachDojoGrid.dataSet.data.length} + \")\"; } else { \"Approaches\"; }","targetProperty":"caption"}, {}]
									}],
									relatedEditor5: ["wm.RelatedEditor", {"editingMode":"readonly","formField":"approachs","height":"100%","horizontalAlign":"left","layoutKind":"left-to-right","verticalAlign":"top"}, {}, {
										binding: ["wm.Binding", {}, {}, {
											wire: ["wm.Wire", {"expression":undefined,"source":"interviewDojoGrid1.selectedItem.interview.approachs","targetProperty":"dataSet"}, {}]
										}],
										approachLivePanel1: ["wm.LivePanel", {"autoScroll":false,"horizontalAlign":"left","layoutKind":"left-to-right","verticalAlign":"top"}, {}, {
											binding: ["wm.Binding", {}, {}, {
												wire: ["wm.Wire", {"source":"Approach_List","targetId":null,"targetProperty":"gridLayer"}, {}],
												wire1: ["wm.Wire", {"source":"Edit_Approach","targetId":null,"targetProperty":"detailsLayer"}, {}],
												wire2: ["wm.Wire", {"source":"approachLiveForm1","targetId":null,"targetProperty":"liveForm"}, {}],
												wire3: ["wm.Wire", {"source":"approachDojoGrid","targetId":null,"targetProperty":"dataGrid"}, {}],
												wire4: ["wm.Wire", {"source":"approachSaveButton","targetId":null,"targetProperty":"saveButton"}, {}]
											}],
											approachDojoGrid: ["wm.DojoGrid", {"columns":[{"show":false,"field":"id","title":"Id","width":"80px","align":"right","formatFunc":"","mobileColumn":false},{"show":true,"field":"name","title":"Name","width":"40%","align":"left","formatFunc":"","editorProps":{"restrictValues":true},"mobileColumn":false},{"show":true,"field":"description","title":"Description","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"version","title":"Version","width":"80px","align":"right","formatFunc":"","editorProps":{"restrictValues":true},"mobileColumn":false},{"show":false,"field":"PHONE COLUMN","title":"-","width":"100%","align":"left","expression":"\"<div class='MobileRowTitle'>Name: \" + ${name} + \"</div>\"\n+ \"<div class='MobileRow'>Description: \" + ${description} + \"</div>\"\n","mobileColumn":true}],"height":"100%","margin":"4","primaryKeyFields":["id"]}, {"onSelect":"approachIssuesVariable","onSelect1":"approachLivePanel1.popupLivePanelEdit"}, {
												binding: ["wm.Binding", {}, {}, {
													wire: ["wm.Wire", {"expression":undefined,"source":"interviewDojoGrid1.selectedItem.interview.approachs","targetProperty":"dataSet"}, {}]
												}]
											}],
											approachNewButton: ["wm.Button", {"_classes":{"domNode":["iconButton"]},"caption":undefined,"imageIndex":1,"imageList":"app.silkIconList","margin":"4","width":"32px"}, {"onclick":"approachLivePanel1.popupLivePanelInsert"}]
										}]
									}]
								}]
							}]
						}]
					}],
					Edit_Issue: ["wm.Layer", {"borderColor":"","horizontalAlign":"left","themeStyleType":"ContentPanel","verticalAlign":"top"}, {}, {
						binding: ["wm.Binding", {}, {}, {
							wire1: ["wm.Wire", {"expression":undefined,"source":"summaryIssuesGrid1.isRowSelected","targetProperty":"showing"}, {}],
							wire: ["wm.Wire", {"expression":"\"I-\" + ${summaryIssuesGrid1.selectedItem.issue.sequence}","targetProperty":"caption"}, {}]
						}],
						pageContainer1: ["wm.PageContainer", {"deferLoad":true}, {}]
					}],
					View_Flow: ["wm.Layer", {"borderColor":"","caption":"Flow","horizontalAlign":"left","themeStyleType":"ContentPanel","verticalAlign":"top"}, {}, {
						binding: ["wm.Binding", {}, {}, {
							wire: ["wm.Wire", {"expression":undefined,"source":"summaryFlowsGrid1.isRowSelected","targetProperty":"showing"}, {}]
						}],
						pageContainer2: ["wm.PageContainer", {"deferLoad":true}, {}]
					}],
					View_Issue: ["wm.Layer", {"borderColor":"","horizontalAlign":"left","themeStyleType":"ContentPanel","verticalAlign":"top"}, {}, {
						binding: ["wm.Binding", {}, {}, {
							wire: ["wm.Wire", {"expression":undefined,"source":"summaryAppIssuesGrid1.isRowSelected","targetProperty":"showing"}, {}],
							wire1: ["wm.Wire", {"expression":"\"I-\" + ${summaryAppIssuesGrid1.selectedItem.issue.sequence}","targetProperty":"caption"}, {}]
						}],
						pageContainer3: ["wm.PageContainer", {"deferLoad":true}, {}]
					}],
					View_Issue1: ["wm.Layer", {"borderColor":"","horizontalAlign":"left","themeStyleType":"ContentPanel","verticalAlign":"top"}, {}, {
						binding: ["wm.Binding", {}, {}, {
							wire: ["wm.Wire", {"expression":undefined,"source":"summaryDocIssuesGrid1.isRowSelected","targetProperty":"showing"}, {}],
							wire1: ["wm.Wire", {"expression":"\"I-\" + ${summaryDocIssuesGrid1.selectedItem.c0.sequence}","targetProperty":"caption"}, {}]
						}],
						pageContainer4: ["wm.PageContainer", {"deferLoad":true}, {}]
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
								nameEditor3: ["wm.Text", {"border":"0","caption":"Name","captionSize":"140px","changeOnKey":true,"dataValue":"Short-term solution","desktopHeight":"26px","emptyValue":"emptyString","formField":"name","height":"26px","required":true,"width":"100%"}, {}],
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
								descriptionEditor3: ["wm.LargeTextArea", {"border":"0","caption":"Description","captionAlign":"right","captionPosition":"left","captionSize":"140px","changeOnKey":true,"dataValue":"What can be done quickly","emptyValue":"emptyString","formField":"description","width":"100%"}, {}],
								approvedEditor1: ["wm.Checkbox", {"caption":"Approved","captionSize":"140px","desktopHeight":"26px","displayValue":false,"formField":"approved","height":"26px","width":"159px"}, {}]
							}]
						}],
						tabLayers4: ["wm.TabLayers", {}, {}, {
							layer10: ["wm.Layer", {"caption":"Approaches","horizontalAlign":"left","themeStyleType":"ContentPanel","verticalAlign":"top"}, {}, {
								html15: ["wm.Html", {"autoSizeHeight":true,"height":"48px","html":"<div class=\"note\">Pick the approaches to be included in this solution.</div>","minDesktopHeight":15}, {}],
								dojoGrid9: ["wm.DojoGrid", {"columns":[{"show":false,"field":"id","title":"Id","width":"80px","align":"right","formatFunc":"","mobileColumn":false},{"show":true,"field":"name","title":"Approach","width":"25%","align":"left","formatFunc":"","editorProps":{"restrictValues":true},"mobileColumn":false},{"show":true,"field":"description","title":"Description","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"version","title":"Version","width":"80px","align":"right","formatFunc":"","mobileColumn":false},{"show":false,"field":"interview.id","title":"Id","width":"80px","align":"right","formatFunc":"","mobileColumn":false},{"show":false,"field":"interview.scheduled","title":"Scheduled","width":"80px","align":"left","formatFunc":"wm_date_formatter","mobileColumn":false},{"show":false,"field":"interview.notes","title":"Notes","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"interview.done","title":"Done","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"interview.version","title":"Version","width":"80px","align":"right","formatFunc":"","mobileColumn":false},{"show":false,"field":"PHONE COLUMN","title":"-","width":"100%","align":"left","expression":"\"<div class='MobileRowTitle'>Approach: \" + ${name} + \"</div>\"\n+ \"<div class='MobileRow'>Description: \" + ${description} + \"</div>\"\n","mobileColumn":true}],"dsType":"com.analystdb.data.Approach","height":"100%","margin":"4","minDesktopHeight":60,"selectionMode":"checkbox","singleClickEdit":true}, {}, {
									binding: ["wm.Binding", {}, {}, {
										wire: ["wm.Wire", {"expression":undefined,"source":"approachLiveVariable1","targetProperty":"dataSet"}, {}]
									}]
								}]
							}],
							layer11: ["wm.Layer", {"caption":"Timings and Costs","horizontalAlign":"left","themeStyleType":"ContentPanel","verticalAlign":"top"}, {}, {
								html16: ["wm.Html", {"autoSizeHeight":true,"height":"48px","html":"<div class=\"note\">Enter some specifics for each approaches.</div>","minDesktopHeight":15}, {}],
								costGrid: ["wm.DojoGrid", {"columns":[{"show":false,"field":"PHONE COLUMN","title":"-","width":"100%","align":"left","editorProps":{"restrictValues":true},"expression":"\"<div class='MobileRowTitle'>Name: \" + ${name} + \"</div>\"\n+ \"<div class='MobileRow'>Start: \" + wm.List.prototype.dateFormatter({}, null,null,null,${Start}) + \"</div>\"\n+ \"<div class='MobileRow'>End: \" + wm.List.prototype.dateFormatter({}, null,null,null,${End}) + \"</div>\"\n+ \"<div class='MobileRow'>Cost: \" + wm.List.prototype.currencyFormatter({\"currency\":\"USD\"}, null,null,null,${Cost}) + \"</div>\"\n","mobileColumn":true},{"show":true,"field":"name","title":"Name","width":"100%","align":"left","formatFunc":"","editorProps":null,"mobileColumn":false},{"show":true,"field":"Start","title":"Start","width":"25%","align":"left","formatFunc":"wm_date_formatter","fieldType":"dojox.grid.cells.DateTextBox","editorProps":{"restrictValues":true},"isCustomField":true,"mobileColumn":false},{"show":true,"field":"End","title":"End","width":"25%","align":"left","formatFunc":"wm_date_formatter","fieldType":"dojox.grid.cells.DateTextBox","editorProps":{"restrictValues":true},"isCustomField":true,"mobileColumn":false},{"show":true,"field":"Cost","title":"Cost","width":"25%","align":"left","formatFunc":"wm_currency_formatter","formatProps":{"currency":"USD"},"fieldType":"dojox.grid.cells.NumberTextBox","constraints":{"min":0},"editorProps":{"restrictValues":true},"isCustomField":true,"mobileColumn":false},{"show":false,"field":"id","title":"Id","width":"80px","align":"right","formatFunc":"","mobileColumn":false},{"show":false,"field":"description","title":"Description","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"version","title":"Version","width":"80px","align":"right","formatFunc":"","mobileColumn":false}],"dsType":"com.analystdb.data.Approach","height":"100%","margin":"4","minDesktopHeight":60,"singleClickEdit":true}, {}, {
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
					Edit_Issuecomment: ["wm.Layer", {"autoScroll":true,"borderColor":"","caption":"Edit Comment","horizontalAlign":"left","themeStyleType":"ContentPanel","verticalAlign":"top"}, {}, {
						binding: ["wm.Binding", {}, {}, {
							wire: ["wm.Wire", {"expression":undefined,"source":"dataGrid2.isRowSelected","targetProperty":"showing"}, {}]
						}],
						issuecommentLiveForm1: ["wm.LiveForm", {"alwaysPopulateEditors":true,"height":"100%","horizontalAlign":"left","liveEditing":false,"margin":"4","verticalAlign":"top"}, {"onSuccess":"issuecommentLivePanel1.popupLiveFormSuccess"}, {
							binding: ["wm.Binding", {}, {}, {
								wire: ["wm.Wire", {"expression":undefined,"source":"dataGrid2.selectedItem","targetProperty":"dataSet"}, {}]
							}],
							text1Panel: ["wm.Panel", {"height":"36px","horizontalAlign":"left","layoutKind":"left-to-right","verticalAlign":"middle","width":"100%"}, {}, {
								text1: ["wm.Text", {"border":"0","caption":"Issue","captionSize":"140px","desktopHeight":"26px","displayValue":"79","emptyValue":"emptyString","formatter":"text1ReadOnlyNodeFormat","height":"26px","ignoreParentReadonly":true,"readonly":true,"width":"100%"}, {}, {
									binding: ["wm.Binding", {}, {}, {
										wire: ["wm.Wire", {"expression":undefined,"source":"dataGrid2.selectedItem.issue.sequence","targetProperty":"dataValue"}, {}]
									}]
								}],
								issuecommentFormButtonPanel: ["wm.Panel", {"desktopHeight":"34px","enableTouchHeight":true,"height":"34px","horizontalAlign":"right","layoutKind":"left-to-right","mobileHeight":"40px","verticalAlign":"middle","width":"100%"}, {}, {
									issuecommentDeleteButton: ["wm.Button", {"caption":"Delete","imageIndex":21,"imageList":"app.silkIconList","margin":"4"}, {"onclick":"issuecommentLiveForm1.deleteData"}, {
										binding: ["wm.Binding", {}, {}, {
											wire: ["wm.Wire", {"source":"dataGrid2.emptySelection","targetId":null,"targetProperty":"disabled"}, {}]
										}]
									}],
									issuecommentSaveButton: ["wm.Button", {"caption":"Save","imageIndex":0,"imageList":"app.silkIconList","margin":"4"}, {"onclick":"issuecommentLiveForm1.saveDataIfValid"}, {
										binding: ["wm.Binding", {}, {}, {
											wire: ["wm.Wire", {"expression":"${issuecommentLiveForm1.invalid} || !${issuecommentLiveForm1.isDirty}","targetId":null,"targetProperty":"disabled"}, {}]
										}]
									}]
								}]
							}],
							applicableToMeEditor1Panel: ["wm.Panel", {"height":"26px","horizontalAlign":"left","layoutKind":"left-to-right","verticalAlign":"top","width":"100%"}, {}, {
								applicableToMeEditor1: ["wm.Checkbox", {"caption":"Applies","captionSize":"140px","desktopHeight":"26px","displayValue":false,"formField":"applicableToMe","height":"26px","width":"100%"}, {}],
								fixedEditor1: ["wm.Checkbox", {"caption":"Fixed","captionSize":"140px","desktopHeight":"26px","displayValue":false,"formField":"fixed","height":"26px","width":"100%"}, {}]
							}],
							descriptionEditor2: ["wm.LargeTextArea", {"border":"0","caption":"Comment","captionAlign":"right","captionPosition":"left","captionSize":"140px","changeOnKey":true,"dataValue":"","emptyValue":"emptyString","formField":"description","height":"50%","width":"100%"}, {}],
							tabLayers5: ["wm.TabLayers", {}, {}, {
								layer13: ["wm.Layer", {"caption":"Affected Flows","horizontalAlign":"left","layoutKind":"left-to-right","themeStyleType":"ContentPanel","verticalAlign":"top"}, {}, {
									dojoGrid7: ["wm.DojoGrid", {"columns":[{"show":true,"field":"flow.fromActor","title":"From","width":"50%","align":"left","formatFunc":"","mobileColumn":false},{"show":true,"field":"flow.toActor","title":"To","width":"50%","align":"left","formatFunc":"","editorProps":{"restrictValues":true},"mobileColumn":false},{"show":true,"field":"flow.name","title":"Flow","width":"100%","align":"left","formatFunc":"","editorProps":{"restrictValues":true},"mobileColumn":false},{"show":false,"field":"PHONE COLUMN","title":"-","width":"100%","align":"left","expression":"\"<div class='MobileRowTitle'>From: \" + ${flow.fromActor} + \"</div>\"\n+ \"<div class='MobileRow'>To: \" + ${flow.toActor} + \"</div>\"\n+ \"<div class='MobileRow'>Flow: \" + ${flow.name} + \"</div>\"\n","mobileColumn":true}],"dsType":"com.analystdb.data.IssueCommentFlows","height":"100%","margin":"4","minDesktopHeight":60,"singleClickEdit":true}, {}, {
										binding: ["wm.Binding", {}, {}, {
											wire: ["wm.Wire", {"expression":undefined,"source":"issueFlowsVariable","targetProperty":"dataSet"}, {}]
										}]
									}],
									button14: ["wm.Button", {"_classes":{"domNode":["iconButton"]},"caption":undefined,"imageIndex":75,"imageList":"app.silkIconList","margin":"4","width":"32px"}, {"onclick":"EditIssueFlows.show"}]
								}],
								layer14: ["wm.Layer", {"caption":"Relevent Approaches","horizontalAlign":"left","layoutKind":"left-to-right","themeStyleType":"ContentPanel","verticalAlign":"top"}, {}, {
									dojoGrid5: ["wm.DojoGrid", {"columns":[{"show":false,"field":"issue.sequence","title":"Sequence","width":"80px","align":"right","formatFunc":"","mobileColumn":false},{"show":false,"field":"issue.description","title":"Description","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":true,"field":"approach.name","title":"Fixed By","width":"50%","align":"left","formatFunc":"","editorProps":{"restrictValues":true},"mobileColumn":false},{"show":true,"field":"approach.description","title":"Description","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"PHONE COLUMN","title":"-","width":"100%","align":"left","expression":"\"<div class='MobileRowTitle'>Fixed By: \" + ${approach.name} + \"</div>\"\n+ \"<div class='MobileRow'>Description: \" + ${approach.description} + \"</div>\"\n","mobileColumn":true}],"height":"100%","margin":"4","minDesktopHeight":60,"singleClickEdit":true}, {}, {
										binding: ["wm.Binding", {}, {}, {
											wire: ["wm.Wire", {"expression":undefined,"source":"issueApproachesVariable","targetProperty":"dataSet"}, {}]
										}]
									}],
									button15: ["wm.Button", {"_classes":{"domNode":["iconButton"]},"caption":undefined,"imageIndex":75,"imageList":"app.silkIconList","margin":"4","width":"32px"}, {"onclick":"EditIssueApproaches.show"}]
								}],
								layer15: ["wm.Layer", {"caption":"Details","horizontalAlign":"left","themeStyleType":"ContentPanel","verticalAlign":"top"}, {}, {
									issueattributeLivePanel1: ["wm.LivePanel", {"horizontalAlign":"left","layoutKind":"left-to-right","verticalAlign":"top"}, {}, {
										binding: ["wm.Binding", {}, {}, {
											wire: ["wm.Wire", {"source":"issueattributeDojoGrid","targetId":null,"targetProperty":"dataGrid"}, {}]
										}],
										issueattributeDojoGrid: ["wm.DojoGrid", {"columns":[{"show":true,"field":"name","title":"Name","width":"25%","align":"left","formatFunc":"","fieldType":"dojox.grid.cells._Widget","mobileColumn":false},{"show":true,"field":"val","title":"Val","width":"100%","align":"left","formatFunc":"","fieldType":"dojox.grid.cells._Widget","mobileColumn":false},{"show":false,"field":"PHONE COLUMN","title":"-","width":"100%","align":"left","expression":"\"<div class='MobileRowTitle'>Name: \" + ${name} + \"</div>\"\n+ \"<div class='MobileRow'>Val: \" + ${val} + \"</div>\"\n","mobileColumn":true},{"show":false,"field":"id","title":"Id","width":"80px","displayType":"Number","align":"right","formatFunc":""},{"show":false,"field":"version","title":"Version","width":"80px","displayType":"Number","align":"right","formatFunc":""}],"deleteColumn":true,"dsType":"com.analystdb.data.IssueAttribute","height":"100%","liveEditing":true,"margin":"4","noHeader":true,"primaryKeyFields":["id"],"singleClickEdit":true}, {}, {
											binding: ["wm.Binding", {}, {}, {
												wire: ["wm.Wire", {"source":"issueAttributesVariable","targetProperty":"dataSet"}, {}]
											}]
										}],
										issueattributeNewButton: ["wm.Button", {"_classes":{"domNode":["iconButton"]},"caption":undefined,"imageIndex":1,"imageList":"app.silkIconList","margin":"4","width":"32px"}, {"onclick":"issueattributeDojoGrid.addEmptyRow"}]
									}]
								}]
							}]
						}]
					}],
					Edit_Flow: ["wm.Layer", {"autoScroll":true,"borderColor":"","caption":"Edit Flow","horizontalAlign":"left","themeStyleType":"ContentPanel","verticalAlign":"top"}, {}, {
						binding: ["wm.Binding", {}, {}, {
							wire: ["wm.Wire", {"expression":undefined,"source":"flowDojoGrid.isRowSelected","targetProperty":"showing"}, {}]
						}],
						flowLiveForm1: ["wm.LiveForm", {"alwaysPopulateEditors":true,"height":"100%","horizontalAlign":"left","liveEditing":false,"margin":"4","verticalAlign":"top"}, {"onSuccess":"flowLivePanel1.popupLiveFormSuccess"}, {
							binding: ["wm.Binding", {}, {}, {
								wire: ["wm.Wire", {"expression":undefined,"source":"flowDojoGrid.selectedItem","targetProperty":"dataSet"}, {}],
								wire1: ["wm.Wire", {"expression":undefined,"source":"relatedEditor6.dataOutput","targetProperty":"dataOutput.documents"}, {}]
							}],
							fromActorEditor1Panel: ["wm.Panel", {"height":"34px","horizontalAlign":"left","layoutKind":"left-to-right","verticalAlign":"middle","width":"100%"}, {}, {
								fromActorEditor1: ["wm.Text", {"border":"0","caption":"From","captionSize":"140px","changeOnKey":true,"dataValue":"","desktopHeight":"26px","emptyValue":"emptyString","formField":"fromActor","height":"26px","width":"100%"}, {}],
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
							toActorEditor1: ["wm.Text", {"border":"0","caption":"To","captionSize":"140px","changeOnKey":true,"dataValue":"","desktopHeight":"26px","emptyValue":"emptyString","formField":"toActor","height":"26px","width":"50%"}, {}],
							nameEditor2: ["wm.Text", {"border":"0","caption":"Subject","captionSize":"140px","changeOnKey":true,"dataValue":"","desktopHeight":"26px","emptyValue":"emptyString","formField":"name","height":"26px","required":true,"width":"100%"}, {}],
							descriptionEditor4: ["wm.LargeTextArea", {"border":"0","caption":"Description","captionAlign":"right","captionPosition":"left","captionSize":"140px","changeOnKey":true,"dataValue":"","emptyValue":"emptyString","formField":"description","width":"100%"}, {}],
							selectMenu1: ["wm.SelectMenu", {"caption":"Document","captionSize":"140px","dataField":"id","dataType":"com.analystdb.data.Documents","desktopHeight":"26px","displayField":"document","displayValue":"","height":"26px","width":"100%"}, {}, {
								binding: ["wm.Binding", {}, {}, {
									wire: ["wm.Wire", {"expression":undefined,"source":"documentsLiveVariable1","targetProperty":"dataSet"}, {}],
									wire1: ["wm.Wire", {"expression":undefined,"source":"flowDojoGrid.selectedItem.documents","targetProperty":"dataValue"}, {}]
								}]
							}],
							panel6: ["wm.Panel", {"height":"100%","horizontalAlign":"left","layoutKind":"left-to-right","verticalAlign":"top","width":"100%"}, {}, {
								dojoGrid8: ["wm.DojoGrid", {"columns":[{"show":true,"field":"issueComment.issue.sequence","title":"Issue","width":"50px","align":"center","formatFunc":"","editorProps":{"restrictValues":true},"expression":"\"I-\" + ${issueComment.issue.sequence}","mobileColumn":false},{"show":true,"field":"issueComment.description","title":"Description","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"issueComment.issue.description","title":"Description","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"PHONE COLUMN","title":"-","width":"100%","align":"left","expression":"\"<div class='MobileRowTitle'>Description: \" + ${issueComment.description} + \"</div>\"\n","mobileColumn":true}],"dsType":"com.analystdb.data.IssueCommentFlows","height":"100%","margin":"4","minDesktopHeight":60,"singleClickEdit":true}, {}, {
									binding: ["wm.Binding", {}, {}, {
										wire: ["wm.Wire", {"expression":undefined,"source":"flowIssuesVariable","targetProperty":"dataSet"}, {}]
									}]
								}],
								button16: ["wm.Button", {"_classes":{"domNode":["iconButton"]},"caption":undefined,"imageIndex":75,"imageList":"app.silkIconList","margin":"4","width":"32px"}, {"onclick":"EditFlowIssues.show"}]
							}]
						}]
					}],
					Edit_Approach: ["wm.Layer", {"autoScroll":true,"borderColor":"","caption":"Edit Approach","horizontalAlign":"left","themeStyleType":"ContentPanel","verticalAlign":"top"}, {}, {
						binding: ["wm.Binding", {}, {}, {
							wire: ["wm.Wire", {"expression":undefined,"source":"approachDojoGrid.isRowSelected","targetProperty":"showing"}, {}]
						}],
						approachLiveForm1: ["wm.LiveForm", {"alwaysPopulateEditors":true,"height":"100%","horizontalAlign":"left","liveEditing":false,"margin":"4","verticalAlign":"top"}, {"onSuccess":"approachLivePanel1.popupLiveFormSuccess"}, {
							binding: ["wm.Binding", {}, {}, {
								wire: ["wm.Wire", {"expression":undefined,"source":"approachDojoGrid.selectedItem","targetProperty":"dataSet"}, {}]
							}],
							nameEditor4Panel: ["wm.Panel", {"height":"26px","horizontalAlign":"left","layoutKind":"left-to-right","verticalAlign":"top","width":"100%"}, {}, {
								nameEditor4: ["wm.Text", {"border":"0","caption":"Name","captionSize":"140px","changeOnKey":true,"dataValue":"","desktopHeight":"26px","emptyValue":"emptyString","formField":"name","height":"26px","required":true,"width":"100%"}, {}],
								approachFormButtonPanel: ["wm.Panel", {"desktopHeight":"32px","enableTouchHeight":true,"height":"32px","horizontalAlign":"right","layoutKind":"left-to-right","mobileHeight":"40px","verticalAlign":"top","width":"100%"}, {}, {
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
							descriptionEditor5: ["wm.LargeTextArea", {"border":"0","caption":"Description","captionAlign":"right","captionPosition":"left","captionSize":"140px","changeOnKey":true,"dataValue":"","emptyValue":"emptyString","formField":"description","height":"100%","width":"100%"}, {}],
							dojoGrid4Panel: ["wm.Panel", {"height":"100%","horizontalAlign":"left","layoutKind":"left-to-right","verticalAlign":"top","width":"100%"}, {}, {
								dojoGrid4: ["wm.DojoGrid", {"columns":[{"show":true,"field":"issue.sequence","title":"Addresses","width":"80px","align":"center","formatFunc":"","editorProps":{"restrictValues":true},"expression":"\"I-\" + ${issue.sequence}","mobileColumn":false},{"show":true,"field":"issue.description","title":"Description","width":"100%","align":"left","formatFunc":"","editorProps":{"restrictValues":true},"mobileColumn":false},{"show":false,"field":"approach.name","title":"Name","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"approach.description","title":"Description","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"PHONE COLUMN","title":"-","width":"100%","align":"left","expression":"\"<div class='MobileRowTitle'>Description: \" + ${issue.description} + \"</div>\"\n","mobileColumn":true}],"dsType":"com.analystdb.data.IssueApproach","height":"100%","margin":"4","minDesktopHeight":60,"singleClickEdit":true}, {}, {
									binding: ["wm.Binding", {}, {}, {
										wire: ["wm.Wire", {"expression":undefined,"source":"approachIssuesVariable","targetProperty":"dataSet"}, {}]
									}]
								}],
								button13: ["wm.Button", {"caption":undefined,"imageIndex":75,"imageList":"app.silkIconList","margin":"4","width":"32px"}, {"onclick":"EditApproachIssues.show"}]
							}]
						}]
					}]
				}]
			}]
		}],
		footer: ["wm.Panel", {"height":"36px","horizontalAlign":"right","layoutKind":"left-to-right","verticalAlign":"top","width":"800px"}, {}, {
			picture1: ["wm.Picture", {"height":"36px","source":"resources/images/powered.png","width":"257px"}, {}]
		}]
	}]
}