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
	_end: 0
});