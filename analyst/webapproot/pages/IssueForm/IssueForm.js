dojo.declare("IssueForm", wm.Page, {
	start: function() {
		
	},
	"preferredDevice": "desktop",

	sequenceEditor1ReadOnlyNodeFormat: function(inSender, inValue) {
		return "I-" + inValue;
	},
	selectButtonClick: function(inSender) {
        var approach = this.availableGrid.selectedItem.getData(true);
        
        this.availableGrid.deleteRow(this.availableGrid.getSelectedIndex());
        this.issueApproachesVariable.addItem(
            {id:{issueId:this.issueForm.dataOutput.id,approachId:approach.id},
             approach:approach});

	},
	deselectButtonClick: function(inSender) {
        var form = this.issueForm;
	},
	updateButton1Click1: function(inSender) {
    	this.addCategoryButton.show();
    	this.addApproachButton.show();
	},
    issueFormHide1: function(inSender) {
        this.addCategoryButton.hide();
        this.addApproachButton.hide();
	},
	saveButton1Click1: function(inSender) {
        this.issueFormHide1(inSender);		
	},
	cancelButton1Click1: function(inSender) {
        this.issueFormHide1(inSender);
	},
	selectButton1Click: function(inSender) {
        var cat = this.availableGrid1.selectedItem.getData(true);
        
        this.availableGrid1.deleteRow(this.availableGrid1.getSelectedIndex());
//        this.issueApproachesVariable.addItem(
//            {id:{issueId:this.issueForm.dataOutput.id,approachId:approach.id},
//             approach:approach});

	},
    deselectButton1Click: function(inSender) {
	  this.deselectButtonClick(inSender);
	},
  _end: 0
});