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
    	var interview = this.upcomingGrid1.selectedItem.data.interview;
        interview.data.done = true;
        var live = this.interviewLiveVariable1;
        live.setOperation("update");
        live.sourceData.setData( interview.data );
        live.update();
        this.interviewForm1.setDataSet(interview);
	},
	button12Click: function(inSender) {
		this.dataGrid1.deleteRow(this.dataGrid1.getSelectedIndex());
	},
	text1ReadOnlyNodeFormat: function(inSender, inValue) {
		return "I-" + inValue;
	},
	_end: 0
});