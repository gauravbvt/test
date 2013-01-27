var interviewing = false;

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

        var live = this.issueFlowsVariable;
        live.operation = "insert";
        live.sourceData.setData(data);
        live.update();
        live.sourceData.setData(null);
        live.operation = "read";
        live.update();
	},
	removeButton3Click: function(inSender) {
        this.allRemoveButtonClick(inSender,this.availableGrid3,this.selectedGrid3,this.issueFlowsVariable);		
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
            flow = flow.flow;
        }
        alert(flow);
	},
	_end: 0
});