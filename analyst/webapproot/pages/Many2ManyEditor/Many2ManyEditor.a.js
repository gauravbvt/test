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

Many2ManyEditor.widgets = {
availableGridDataSet: ["wm.Property", {"bindSource":undefined,"bindTarget":1,"property":"availableGrid.dataSet","type":"Object"}, {}],
selectedGridDataSet: ["wm.Property", {"bindSource":undefined,"bindTarget":1,"property":"selectedGrid.dataSet","type":"Object"}, {}],
layoutBox1: ["wm.Layout", {"horizontalAlign":"left","layoutKind":"left-to-right","verticalAlign":"top"}, {}, {
selectedGrid: ["wm.DojoGrid", {"columns":[{"show":false,"field":"PHONE COLUMN","title":"-","width":"100%","align":"left","expression":"\"<div class='MobileRowTitle'>Selected: \" + ${name} + \"</div>\"\n","mobileColumn":true},{"show":true,"field":"name","title":"Selected","width":"100%","align":"left","editorProps":{"restrictValues":true},"isCustomField":true}],"height":"100%","margin":"4","minDesktopHeight":60,"singleClickEdit":true}, {}],
panel1: ["wm.Panel", {"fitToContentWidth":true,"height":"100%","horizontalAlign":"left","verticalAlign":"middle","width":"82px"}, {}, {
addButton: ["wm.Button", {"caption":"Add","imageIndex":3,"imageList":"app.silkIconList","margin":"4"}, {"onclick":"addButtonClick"}, {
binding: ["wm.Binding", {}, {}, {
wire: ["wm.Wire", {"expression":undefined,"source":"availableGrid.emptySelection","targetProperty":"disabled"}, {}]
}]
}],
removeButton: ["wm.Button", {"caption":"Remove","imageIndex":5,"imageList":"app.silkIconList","margin":"4"}, {"onclick":"removeButtonClick"}, {
binding: ["wm.Binding", {}, {}, {
wire: ["wm.Wire", {"expression":undefined,"source":"selectedGrid.emptySelection","targetProperty":"disabled"}, {}]
}]
}]
}],
availableGrid: ["wm.DojoGrid", {"columns":[{"show":false,"field":"PHONE COLUMN","title":"-","width":"100%","align":"left","expression":"\"<div class='MobileRowTitle'>Available: \" + ${name} + \"</div>\"\n","mobileColumn":true},{"show":true,"field":"name","title":"Available","width":"100%","align":"left","editorProps":{"restrictValues":true},"isCustomField":true}],"height":"100%","margin":"4","minDesktopHeight":60,"singleClickEdit":true}, {}]
}]
};

Many2ManyEditor.prototype._cssText = '';
Many2ManyEditor.prototype._htmlText = '';