var interviewing = false;
var switchInterview = null;
var switchFlow = null;
var switchIssue = null;
var switchApproach = null;

dojo.declare("wizard", wm.Page, {
	"preferredDevice": "desktop",
	start: function() {
		
	},
 	logoutButtonClick: function(inSender) {
	},
    insertInterview: function(resource,scheduled) {
            var live = this.interviewLiveVariable1;
            live.setOperation("insert");
            live.sourceData.setData( { scheduled: scheduled, resource: resource, done: false } );
            live.update();
        
            this.resourceLookup1.clear();
            this.scheduleDateTime1.clear();
    },
	scheduleButtonClick: function(inSender) {
        var who  = this.resourceLookup1.dataValue;
        if ( !who ) {
            // Create a resource
            var resVar = this.resourceInsert1;
            resVar.sourceData.setData( { name: this.resourceLookup1.editor.displayedValue, 
                                         project: this.projectDojoGrid.selectedItem.getData() } );
            resVar.update();
        } else {
            this.insertInterview( who, this.scheduleDateTime1.dataValue );
        }        
	},
    resourceInsert1Result: function(inSender, inDeprecated) {
        this.insertInterview( inSender.data, this.scheduleDateTime1.dataValue );
	},
	resourceLookup1Change: function(inSender, inDisplayValue, inDataValue, inSetByCode) {
        this.scheduleButton.setDisabled( 
            this.resourceLookup1.displayValue.length === 0 || this.scheduleDateTime1.dataValue === null );		
	},
	scheduleDateTime1Change: function(inSender, inDisplayValue, inDataValue, inSetByCode) {
        this.scheduleButton.setDisabled( 
            this.resourceLookup1.editor.displayedValue.length === 0 || this.scheduleDateTime1.dataValue === null );    	
	},
	deleteScheduled9Click: function(inSender) {
		var interview = this.upcomingGrid1.selectedItem.data.interview.data;
        var live = this.interviewLiveVariable1;
        live.setOperation("delete");
        live.sourceData.setData( { id: interview.id } );
        live.update();
	},
	interviewButtonClick: function(inSender) {
    	var interview = this.upcomingGrid1.selectedItem.data.interview.data;
        interviewing = true;
        interview.done = true;
        var live = this.interviewLiveVariable1;
        live.setOperation("update");
        live.sourceData.setData( interview );
        live.update();
	},
	button12Click: function(inSender) {
        this.resourceattributeDojoGrid.deleteRow(this.resourceattributeDojoGrid.getSelectedIndex());
        inSender.disable();
	},
	text1ReadOnlyNodeFormat: function(inSender, inValue) {
		return "I-" + inValue;
	},
	interviewLiveVariable1Success2: function(inSender, inDeprecated) {
        if ( interviewing ) {
            this.recentInterviewsVariable1.update();            
        }		
	},
	recentInterviewsVariable1Success: function(inSender, inDeprecated) {
		if ( interviewing ) {
            var id = this.interviewLiveVariable1.sourceData.data.id;
            this.interviewing = false;
            var recents = this.interviewDojoGrid1.dataSet.data._list;
            for ( i=0; i<recents.length && id !== recents[i].data.id ; i++){                
            }
            this.interviewDojoGrid1.select(i);
		}
	},
	resourceattributeNewButtonClick1: function(inSender) {
        var variable = this.interviewForm1.dataOutput.data.resource;
        var resource = variable.data;
		this.resourceattributeDojoGrid.addRow({id:0,resource:resource,name:'',val:''}, true)      
	},
	flowLiveForm1BeforeServiceCall: function(inSender, inOperation, inData) {
		if ( "insert" == inOperation ) {
            inData.id = 0;
            inData.interview = this.interviewDojoGrid1.selectedItem.data.interview;
		}
	},
	addButton2Click: function(inSender) {
        var availableGrid = this.availableGrid2;
        var selected = availableGrid.selectedItem.dataSet.data;
        availableGrid.deleteRow(availableGrid.getSelectedIndex());

        var flow = this.flowDojoGrid.selectedItem.dataSet.data;
        var data = {id:{issues:selected.id,flows:flow.id},flow:flow,issueComment:selected};

        var live = this.flowIssuesVariable;
        live.operation = "insert";
        live.sourceData.setData(data);
        live.update();
        live.sourceData.setData(null);
        live.operation = "read";
        live.update();
	},
    allRemoveButtonClick: function(inSender,availableGrid,selectedGrid,live) {
        var selected = selectedGrid.selectedItem.dataSet.data;
        live.operation = "delete";
        live.sourceData.setData(selected);
        live.update();
        live.sourceData.setData(null);
        live.operation = "read";
        live.update();
        inSender.disable();
	},
    removeButton2Click: function(inSender) {
        this.allRemoveButtonClick(inSender,this.availableGrid2,this.selectedGrid2,this.flowIssuesVariable);
    },
    removeButtonClick: function(inSender) {
        this.allRemoveButtonClick(inSender,this.availableGrid,this.selectedGrid,this.approachIssuesVariable);
    },
	addButtonClick: function(inSender) {
        var availableGrid = this.availableGrid;
        var selected = availableGrid.selectedItem.dataSet.data;
        availableGrid.deleteRow(availableGrid.getSelectedIndex());

        var approach = this.approachDojoGrid.selectedItem.dataSet.data;
        var data = {id:{issueId:selected.id,approachId:approach.id},approach:approach,issue:selected};

        var live = this.approachIssuesVariable;
        live.operation = "insert";
        live.sourceData.setData(data);
        live.update();
        live.sourceData.setData(null);
        live.operation = "read";
        live.update();
	},
	issueattributeNewButtonClick: function(inSender) {
        var issueComment = this.dataGrid2.selectedItem.dataSet.data;
    	this.issueattributeDojoGrid.addRow({id:0,issueComment:issueComment,name:'',val:''}, true)      		
	},
	addButton1Click: function(inSender) {
        var availableGrid = this.availableGrid1;
        var selected = availableGrid.selectedItem.dataSet.data;
        availableGrid.deleteRow(availableGrid.getSelectedIndex());

        var issue = this.dataGrid2.selectedItem.dataSet.data.issue.data;
        var data = {id:{issueId:issue.id,approachId:selected.id},approach:selected,issue:issue};

        var live = this.issueApproachesVariable;
        live.operation = "insert";
        live.sourceData.setData(data);
        live.update();
        live.sourceData.setData(null);
        live.operation = "read";
        live.update();
	},
	removeButton1Click: function(inSender) {
        this.allRemoveButtonClick(inSender,this.availableGrid1,this.selectedGrid1,this.issueApproachesVariable);
	},
	addButton3Click: function(inSender) {
        var availableGrid = this.availableGrid3;
        var selected = availableGrid.selectedItem.dataSet.data;
        availableGrid.deleteRow(availableGrid.getSelectedIndex());

        var issueComment = this.dataGrid2.selectedItem.dataSet.data;
        var data = {id:{issues:issueComment.id,flows:selected.id},flow:selected,issueComment:issueComment};

        var live = this.issueCommentFlowsVariable;
        live.operation = "insert";
        live.sourceData.setData(data);
        live.update();
        live.sourceData.setData(null);
        live.operation = "read";
        live.update();
	},
	removeButton3Click: function(inSender) {
        this.allRemoveButtonClick(inSender,this.availableGrid3,this.selectedGrid3,this.issueCommentFlowsVariable);		
	},
    addButton4Click: function(inSender) {
        var availableGrid = this.availableGrid4;
        var selected = availableGrid.selectedItem.dataSet.data;
        availableGrid.deleteRow(availableGrid.getSelectedIndex());

        var issue = this.issueForm.dataOutput.data;
        var data = {id:{issueId:issue.id,issueCategoryId:selected.id},issueCategory:selected,issue:issue};

        var live = this.issueCategoriesVariable1;
        live.operation = "insert";
        live.sourceData.setData(data);
        live.update();
        live.sourceData.setData(null);
        live.operation = "read";
        live.update();
	},
    removeButton4Click: function(inSender) {
        this.allRemoveButtonClick(inSender,this.availableGrid4,this.selectedGrid4,this.issueCategoriesVariable1);    	
	},
	issueInsertSuccess: function(inSender, inDeprecated) {
        var issueComment = this.issuecommentLiveForm1.dataOutput.data;
        issueComment.id = 0;
        issueComment.interview = this.interviewDojoGrid1.selectedItem.data.interview; 
        issueComment.issue = inDeprecated;
		this.issuecommentLiveForm1.insertData();
 	},
	issuecommentSaveButtonClick: function(inSender) {
		if ( this.dataGrid2.isRowSelected ) {
    	    this.issuecommentLiveForm1.saveDataIfValid();
		} else {
            this.issueInsert.update();
		}
	},
    switchToFlow: function(inSender) {
		var flow = inSender.selectedItem.data;
        if ( flow.flow ) {
            flow = flow.flow.data;
        }
        switchFlow = flow;
        switchInterview = flow.interview.data;
        if ( this.interviewDojoGrid1.getRowCount() == 0 )
            this.button2.click(); 
        else
            this.showInterviews.update();
 	},
    switchToIssue: function(inSender) {
		var issueComment = inSender.selectedItem.data;
        if ( issueComment.issueComment ) {
            issueComment = issueComment.issueComment.data;
        }
        switchIssue = issueComment;
        switchInterview = issueComment.interview.data;
        if ( this.interviewDojoGrid1.getRowCount() == 0 )
            this.button2.click(); 
        else
            this.showInterviews.update();
 	},
    switchToApproach: function(inSender) {
    	var approach = inSender.selectedItem.data;
        if ( approach.approach ) {
            approach = approach.approach.data;
        }
        switchApproach = approach;
        switchInterview = approach.interview.data;
        if ( this.interviewDojoGrid1.getRowCount() == 0 )
            this.button2.click(); 
        else
            this.showInterviews.update();
 	},
	Edit_InterviewsShow1: function(inSender) {
        if ( switchInterview ) {
            var id = switchInterview.id;
            switchInterview = null;
            var interviews = this.interviewDojoGrid1.dataSet.data._list;
            for ( i=0; i<interviews.length && interviews[i].data.id != id; i++ );
            if ( i !== interviews.length )
                this.interviewDojoGrid1.select( i );
    	}
	},
	interviewFlowsVariable1Success: function(inSender, inDeprecated) {
        if ( switchFlow ) {
            var id = switchFlow.id;
            switchFlow = null;
            var flows = this.flowDojoGrid.dataSet.data._list;
            for ( i=0; i<flows.length && flows[i].data.id != id; i++ );
            if ( i !== flows.length ) {
                this.flowDojoGrid.select( i );
            }
    	}
	},
	interviewIssuesVariable1Success: function(inSender, inDeprecated) {
        if ( switchIssue ) {
            var id = switchIssue.id;
            switchIssue = null;
            var issues = this.dataGrid2.dataSet.data._list;
            for ( i=0; i<issues.length && issues[i].data.id != id; i++ );
            if ( i !== issues.length ) {
                this.dataGrid2.select( i );
            }
        }
	},
	sequenceEditor1ReadOnlyNodeFormat: function(inSender, inValue) {
    	return "I-" + inValue;		
	},
	button19Click: function(inSender) {
		this.issueForm.setDataSet( this.dataGrid2.selectedItem.data.issue );        
        this.showIssue.update();
	},
	summaryDocIssuesGrid1Select: function(inSender) {
    	this.issueForm.setDataSet( this.summaryDocIssuesGrid1.selectedItem.data.issue );        
        this.showIssue.update();
	},
	summaryIssuesGrid1Select: function(inSender) {
        this.issueForm.setDataSet( this.summaryIssuesGrid1.selectedItem.data.issue );        
        this.showIssue.update();
	},
	summaryAppIssuesGrid1Select: function(inSender) {
        this.issueForm.setDataSet( this.summaryAppIssuesGrid1.selectedItem.data.issue );        
        this.showIssue.update();
	},
    delCategoryButton6Click: function(inSender) {
        var live = this.allIssueCategories;
        live.setOperation("delete");
        live.sourceData.setData( { id: this.availableGrid4.selectedItem.data.id } );
        live.update();
        inSender.disable();
	},
    genericDialog1Button1Click: function(inSender, inButton, inText) {
        var data = {name:inText,project:this.projectDojoGrid.selectedItem.data};

        var live = this.allIssueCategories;
        live.operation = "insert";
        live.sourceData.setData(data);
        live.update();
	},
	interviewApproachesVariable1Success: function(inSender, inDeprecated) {
        if ( switchApproach ) {
            var id = switchApproach.id;
            switchApproach = null;
            var approaches = this.approachDojoGrid.dataSet.data._list;
            for ( i=0; i<approaches.length && approaches[i].data.id != id; i++ );
            if ( i !== approaches.length ) {
                this.approachDojoGrid.select( i );
            }
        }		
	},
	documentsNewButtonClick: function(inSender) {
        var cc = this.documentcategoryLiveVariable1.getData()[0];
		this.documentsDojoGrid.addRow({documentCategory: cc,document:"(New Document)"}, true, false)
	},
	_end: 0
});