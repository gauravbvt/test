dojo.declare("Many2ManyEditor", wm.Page, {
	start: function() {
		
	},
	"preferredDevice": "desktop",

	addButtonClick: function(inSender) {
        var available = this.availableGrid.selectedItem.getData(true);
        this.availableGrid.deleteRow(this.availableGrid.getSelectedIndex());
        
        //this.selectedGrid.dataSet.addItem();


//        this.issueApproachesVariable.addItem(
//            {id:{issueId:this.issueForm.dataOutput.id,approachId:approach.id},
//             approach:approach});

	},
	removeButtonClick: function(inSender) {
        var selected = this.selectedGrid.selectedItem.getData(true);
        this.selectedGrid.deleteRow(this.selectedGrid.getSelectedIndex());		
	},
	_end: 0
});