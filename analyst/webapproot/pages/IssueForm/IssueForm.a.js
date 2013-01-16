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

IssueForm.widgets = {
liveForm1DataSet: ["wm.Property", {"bindSource":undefined,"bindTarget":1,"property":"issueForm.dataSet","readonly":true,"type":"com.analystdb.data.Issue"}, {}],
issueFormReadonly: ["wm.Property", {"bindSource":undefined,"bindTarget":undefined,"property":"issueForm.readonly","type":"boolean"}, {}],
issueVariable: ["wm.LiveVariable", {"autoUpdate":false,"inFlightBehavior":"executeLast","startUpdate":false,"type":"com.analystdb.data.Issue"}, {}, {
liveView: ["wm.LiveView", {"dataType":"com.analystdb.data.Issue","related":["project","issueComments","issueIssueCategories","issueApproachs"],"view":[{"caption":"Id","sortable":true,"dataIndex":"project.id","type":"java.lang.Long","displayType":"Number","required":true,"readonly":true,"includeLists":true,"includeForms":true,"order":0,"subType":null},{"caption":"Id","sortable":true,"dataIndex":"issueComments.id","type":"java.lang.Long","displayType":"Number","required":true,"readonly":true,"includeLists":true,"includeForms":true,"order":0,"subType":null},{"caption":"Id","sortable":true,"dataIndex":"id","type":"java.lang.Long","displayType":"Number","required":true,"readonly":true,"includeLists":true,"includeForms":true,"order":0,"subType":null},{"caption":"Name","sortable":true,"dataIndex":"project.name","type":"java.lang.String","displayType":"Text","required":true,"readonly":false,"includeLists":true,"includeForms":true,"order":1,"subType":null},{"caption":"Description","sortable":true,"dataIndex":"issueComments.description","type":"java.lang.String","displayType":"Text","required":false,"readonly":false,"includeLists":true,"includeForms":true,"order":1,"subType":null},{"caption":"Description","sortable":true,"dataIndex":"project.description","type":"java.lang.String","displayType":"Text","required":false,"readonly":false,"includeLists":true,"includeForms":true,"order":2,"subType":null},{"caption":"ApplicableToMe","sortable":true,"dataIndex":"issueComments.applicableToMe","type":"java.lang.Boolean","displayType":"CheckBox","required":false,"readonly":false,"includeLists":true,"includeForms":true,"order":2,"subType":null},{"caption":"Name","sortable":true,"dataIndex":"name","type":"java.lang.String","displayType":"Text","required":true,"readonly":false,"includeLists":true,"includeForms":true,"order":2,"subType":null},{"caption":"Version","sortable":true,"dataIndex":"project.version","type":"java.lang.Integer","displayType":"Number","required":false,"readonly":false,"includeLists":true,"includeForms":true,"order":3,"subType":null},{"caption":"Fixed","sortable":true,"dataIndex":"issueComments.fixed","type":"java.lang.Boolean","displayType":"CheckBox","required":false,"readonly":false,"includeLists":true,"includeForms":true,"order":3,"subType":null},{"caption":"Version","sortable":true,"dataIndex":"issueComments.version","type":"java.lang.Integer","displayType":"Number","required":false,"readonly":false,"includeLists":true,"includeForms":true,"order":4,"subType":null},{"caption":"Version","sortable":true,"dataIndex":"version","type":"java.lang.Integer","displayType":"Number","required":false,"readonly":false,"includeLists":true,"includeForms":true,"order":4,"subType":null},{"caption":"Sequence","sortable":true,"dataIndex":"sequence","type":"java.lang.Integer","displayType":"Number","required":true,"readonly":false,"includeLists":true,"includeForms":true,"order":7001,"subType":null,"widthUnits":"px"},{"caption":"Description","sortable":true,"dataIndex":"description","type":"java.lang.String","displayType":"Text","required":false,"readonly":false,"includeLists":true,"includeForms":true,"order":7003,"subType":null,"widthUnits":"px"},{"caption":"Id","sortable":true,"dataIndex":"issueIssueCategories.id","type":"com.analystdb.data.IssueIssueCategoryId","displayType":"Text","required":true,"readonly":true,"includeLists":true,"includeForms":true,"order":0,"subType":null},{"caption":"IssueId","sortable":true,"dataIndex":"issueIssueCategories.id.issueId","type":"java.lang.Long","displayType":"Number","required":true,"readonly":true,"includeLists":true,"includeForms":true,"order":0,"subType":null},{"caption":"IssueCategoryId","sortable":true,"dataIndex":"issueIssueCategories.id.issueCategoryId","type":"java.lang.Long","displayType":"Number","required":true,"readonly":true,"includeLists":true,"includeForms":true,"order":1,"subType":null},{"caption":"Id","sortable":true,"dataIndex":"issueApproachs.id","type":"com.analystdb.data.IssueApproachId","displayType":"Text","required":true,"readonly":true,"includeLists":true,"includeForms":true,"order":0,"subType":null},{"caption":"IssueId","sortable":true,"dataIndex":"issueApproachs.id.issueId","type":"java.lang.Long","displayType":"Number","required":true,"readonly":true,"includeLists":true,"includeForms":true,"order":0,"subType":null},{"caption":"ApproachId","sortable":true,"dataIndex":"issueApproachs.id.approachId","type":"java.lang.Long","displayType":"Number","required":true,"readonly":true,"includeLists":true,"includeForms":true,"order":1,"subType":null}]}, {}]
}],
issueCommentsVariable1: ["wm.LiveVariable", {"inFlightBehavior":"executeLast","startUpdate":false,"type":"com.analystdb.data.IssueComment"}, {}, {
binding: ["wm.Binding", {}, {}, {
wire: ["wm.Wire", {"expression":undefined,"source":"issueForm.dataOutput","targetProperty":"filter.issue"}, {}]
}],
liveView: ["wm.LiveView", {"dataType":"com.analystdb.data.IssueComment","related":["interview","interview.resource"],"view":[{"caption":"Id","sortable":true,"dataIndex":"id","type":"java.lang.Long","displayType":"Number","required":true,"readonly":true,"includeLists":true,"includeForms":true,"order":0,"subType":null},{"caption":"Version","sortable":true,"dataIndex":"version","type":"java.lang.Integer","displayType":"Number","required":false,"readonly":false,"includeLists":true,"includeForms":true,"order":4,"subType":null},{"caption":"Description","sortable":true,"dataIndex":"description","type":"java.lang.String","displayType":"Text","required":false,"readonly":false,"includeLists":true,"includeForms":true,"order":9001,"subType":null,"widthUnits":"px"},{"caption":"ApplicableToMe","sortable":true,"dataIndex":"applicableToMe","type":"java.lang.Boolean","displayType":"CheckBox","required":false,"readonly":false,"includeLists":true,"includeForms":true,"order":9002,"subType":null,"widthUnits":"px"},{"caption":"Fixed","sortable":true,"dataIndex":"fixed","type":"java.lang.Boolean","displayType":"CheckBox","required":false,"readonly":false,"includeLists":true,"includeForms":true,"order":9003,"subType":null,"widthUnits":"px"},{"caption":"Name","sortable":true,"dataIndex":"interview.resource.name","type":"java.lang.String","displayType":"Text","required":true,"widthUnits":"px","includeLists":true,"includeForms":true,"order":11001}]}, {}]
}],
allCategoriesVariable: ["wm.LiveVariable", {"inFlightBehavior":"executeLast","orderBy":"asc: name","startUpdate":false,"type":"com.analystdb.data.IssueCategory"}, {}, {
liveView: ["wm.LiveView", {"dataType":"com.analystdb.data.IssueCategory","view":[{"caption":"Id","sortable":true,"dataIndex":"id","type":"java.lang.Long","displayType":"Number","required":true,"readonly":true,"includeLists":true,"includeForms":true,"order":0,"subType":null},{"caption":"Version","sortable":true,"dataIndex":"version","type":"java.lang.Integer","displayType":"Number","required":false,"readonly":false,"includeLists":true,"includeForms":true,"order":2,"subType":null},{"caption":"Name","sortable":true,"dataIndex":"name","type":"java.lang.String","displayType":"Text","required":true,"readonly":false,"includeLists":true,"includeForms":true,"order":1001,"subType":null,"widthUnits":"px"}]}, {}],
binding: ["wm.Binding", {}, {}, {
wire: ["wm.Wire", {"expression":undefined,"source":"issueForm.dataOutput.project","targetProperty":"filter.project"}, {}]
}]
}],
issue2catVariable1: ["wm.LiveVariable", {"inFlightBehavior":"executeLast","startUpdate":false,"type":"com.analystdb.data.IssueIssueCategory"}, {}, {
binding: ["wm.Binding", {}, {}, {
wire: ["wm.Wire", {"expression":undefined,"source":"issueForm.dataOutput","targetProperty":"filter.issue"}, {}]
}],
liveView: ["wm.LiveView", {"dataType":"com.analystdb.data.IssueIssueCategory","related":["issueCategory"],"view":[{"caption":"Id","sortable":true,"dataIndex":"issueCategory.id","type":"java.lang.Long","displayType":"Number","required":true,"widthUnits":"px","includeLists":true,"includeForms":true,"order":15000},{"caption":"Name","sortable":true,"dataIndex":"issueCategory.name","type":"java.lang.String","displayType":"Text","required":true,"widthUnits":"px","includeLists":true,"includeForms":true,"order":15001}]}, {}]
}],
issueApproachesVariable: ["wm.LiveVariable", {"inFlightBehavior":"executeLast","startUpdate":false,"type":"com.analystdb.data.IssueApproach"}, {}, {
binding: ["wm.Binding", {}, {}, {
wire: ["wm.Wire", {"expression":undefined,"source":"issueForm.dataOutput","targetProperty":"filter.issue"}, {}]
}],
liveView: ["wm.LiveView", {"dataType":"com.analystdb.data.IssueApproach","related":["id","approach"],"view":[{"caption":"IssueId","sortable":true,"dataIndex":"id.issueId","type":"java.lang.Long","displayType":"Number","required":true,"readonly":true,"includeLists":true,"includeForms":true,"order":2000,"subType":null,"widthUnits":"px"},{"caption":"ApproachId","sortable":true,"dataIndex":"id.approachId","type":"java.lang.Long","displayType":"Number","required":true,"readonly":true,"includeLists":true,"includeForms":true,"order":2001,"subType":null,"widthUnits":"px"},{"caption":"Name","sortable":true,"dataIndex":"approach.name","type":"java.lang.String","displayType":"Text","required":true,"widthUnits":"px","includeLists":true,"includeForms":true,"order":3001},{"caption":"Description","sortable":true,"dataIndex":"approach.description","type":"java.lang.String","displayType":"Text","required":false,"widthUnits":"px","includeLists":true,"includeForms":true,"order":3002}]}, {}]
}],
availableApproachesVariable1: ["wm.ServiceVariable", {"autoUpdate":true,"inFlightBehavior":"executeLast","operation":"availableApproaches","service":"analystDB"}, {}, {
input: ["wm.ServiceInput", {"type":"availableApproachesInputs"}, {}, {
binding: ["wm.Binding", {}, {}, {
wire: ["wm.Wire", {"expression":undefined,"source":"issueForm.dataOutput.id","targetProperty":"issue"}, {}],
wire1: ["wm.Wire", {"expression":undefined,"source":"issueForm.dataOutput.project.id","targetProperty":"project"}, {}]
}]
}]
}],
approachDialog1: ["wm.DesignableDialog", {"buttonBarId":"buttonBar","containerWidgetId":"containerWidget","title":"Manage approaches"}, {}, {
containerWidget: ["wm.Container", {"_classes":{"domNode":["wmdialogcontainer","MainContent"]},"autoScroll":true,"height":"100%","horizontalAlign":"left","layoutKind":"left-to-right","padding":"5","verticalAlign":"top","width":"100%"}, {}, {
selectedGrid: ["wm.DojoGrid", {"columns":[{"show":false,"field":"id.issueId","title":"IssueId","width":"80px","align":"right","formatFunc":"","mobileColumn":false},{"show":false,"field":"id.approachId","title":"ApproachId","width":"80px","align":"right","formatFunc":"","mobileColumn":false},{"show":false,"field":"PHONE COLUMN","title":"-","width":"100%","align":"left","expression":"\"<div class='MobileRowTitle'>Selected: \" + ${approach.name} + \"</div>\"\n","mobileColumn":true},{"show":true,"field":"approach.name","title":"Selected","width":"100%","align":"left","formatFunc":"","editorProps":{"restrictValues":true},"mobileColumn":false},{"show":false,"field":"approach.description","title":"Approach.description","width":"100%","align":"left","formatFunc":"","mobileColumn":false}],"height":"100%","margin":"4","minDesktopHeight":60,"singleClickEdit":true}, {}, {
binding: ["wm.Binding", {}, {}, {
wire: ["wm.Wire", {"expression":undefined,"source":"issueApproachesVariable","targetProperty":"dataSet"}, {}]
}]
}],
buttonPanel: ["wm.Panel", {"fitToContentWidth":true,"height":"100%","horizontalAlign":"left","verticalAlign":"middle","width":"82px"}, {}, {
selectButton: ["wm.Button", {"caption":"Add","imageIndex":3,"imageList":"app.silkIconList","margin":"4"}, {"onclick":"selectButtonClick"}, {
binding: ["wm.Binding", {}, {}, {
wire: ["wm.Wire", {"expression":undefined,"source":"availableGrid.emptySelection","targetProperty":"disabled"}, {}]
}]
}],
deselectButton: ["wm.Button", {"caption":"Remove","imageIndex":5,"imageList":"app.silkIconList","margin":"4"}, {"onclick":"deselectButtonClick"}, {
binding: ["wm.Binding", {}, {}, {
wire: ["wm.Wire", {"expression":undefined,"source":"selectedGrid.emptySelection","targetProperty":"disabled"}, {}]
}]
}]
}],
availableGrid: ["wm.DojoGrid", {"columns":[{"show":false,"field":"PHONE COLUMN","title":"-","width":"100%","align":"left","editorProps":{"restrictValues":true},"expression":"\"<div class='MobileRowTitle'>Available: \" + ${name} + \"</div>\"\n","mobileColumn":true},{"show":false,"field":"id","title":"Id","width":"80px","align":"right","formatFunc":"","mobileColumn":false},{"show":true,"field":"name","title":"Available","width":"100%","align":"left","formatFunc":"","editorProps":{"restrictValues":true},"mobileColumn":false},{"show":false,"field":"description","title":"Description","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"version","title":"Version","width":"80px","align":"right","formatFunc":"","mobileColumn":false}],"height":"100%","margin":"4","minDesktopHeight":60,"singleClickEdit":true}, {}, {
binding: ["wm.Binding", {}, {}, {
wire: ["wm.Wire", {"expression":undefined,"source":"availableApproachesVariable1","targetProperty":"dataSet"}, {}]
}]
}]
}],
buttonBar: ["wm.Panel", {"_classes":{"domNode":["dialogfooter"]},"border":"1","desktopHeight":"32px","enableTouchHeight":true,"height":"32px","horizontalAlign":"right","layoutKind":"left-to-right","mobileHeight":"40px","verticalAlign":"top","width":"100%"}, {}, {
cancelButton: ["wm.Button", {"caption":"Close","imageIndex":0,"imageList":"app.silkIconList","margin":"4"}, {"onclick":"approachDialog1.hide"}]
}]
}],
categoriesDialog: ["wm.DesignableDialog", {"buttonBarId":"buttonBar","containerWidgetId":"containerWidget","title":"Manage categories"}, {}, {
containerWidget1: ["wm.Container", {"_classes":{"domNode":["wmdialogcontainer","MainContent"]},"autoScroll":true,"height":"100%","horizontalAlign":"left","layoutKind":"left-to-right","padding":"5","verticalAlign":"top","width":"100%"}, {}, {
selectedGrid1: ["wm.DojoGrid", {"columns":[{"show":false,"field":"PHONE COLUMN","title":"-","width":"100%","align":"left","editorProps":{"restrictValues":true},"expression":"\"<div class='MobileRowTitle'>Selected: \" + ${issueCategory.name} + \"</div>\"\n","mobileColumn":true},{"show":false,"field":"issueCategory.id","title":"IssueCategory.id","width":"80px","align":"right","formatFunc":"","mobileColumn":false},{"show":true,"field":"issueCategory.name","title":"Selected","width":"100%","align":"left","formatFunc":"","editorProps":{"restrictValues":true},"mobileColumn":false}],"height":"100%","margin":"4","minDesktopHeight":60,"singleClickEdit":true}, {}, {
binding: ["wm.Binding", {}, {}, {
wire: ["wm.Wire", {"expression":undefined,"source":"issue2catVariable1","targetProperty":"dataSet"}, {}]
}]
}],
buttonPanel1: ["wm.Panel", {"fitToContentWidth":true,"height":"100%","horizontalAlign":"left","verticalAlign":"middle","width":"82px"}, {}, {
selectButton1: ["wm.Button", {"caption":"Add","imageIndex":3,"imageList":"app.silkIconList","margin":"4"}, {"onclick":"selectButton1Click"}, {
binding: ["wm.Binding", {}, {}, {
wire: ["wm.Wire", {"expression":undefined,"source":"availableGrid.emptySelection","targetProperty":"disabled"}, {}]
}]
}],
deselectButton1: ["wm.Button", {"caption":"Remove","imageIndex":5,"imageList":"app.silkIconList","margin":"4"}, {"onclick":"deselectButton1Click"}, {
binding: ["wm.Binding", {}, {}, {
wire: ["wm.Wire", {"expression":undefined,"source":"selectedGrid.emptySelection","targetProperty":"disabled"}, {}]
}]
}]
}],
availableGrid1: ["wm.DojoGrid", {"columns":[{"show":false,"field":"PHONE COLUMN","title":"-","width":"100%","align":"left","editorProps":{"restrictValues":true},"expression":"\"<div class='MobileRowTitle'>Available: \" + ${name} + \"</div>\"\n","mobileColumn":true},{"show":true,"field":"name","title":"Available","width":"100%","align":"left","formatFunc":"","editorProps":{"restrictValues":true},"mobileColumn":false},{"show":false,"field":"id","title":"Id","width":"80px","displayType":"Number","align":"right","formatFunc":""},{"show":false,"field":"version","title":"Version","width":"80px","displayType":"Number","align":"right","formatFunc":""}],"height":"100%","margin":"4","minDesktopHeight":60,"singleClickEdit":true}, {}, {
binding: ["wm.Binding", {}, {}, {
wire: ["wm.Wire", {"expression":undefined,"source":"allCategoriesVariable","targetProperty":"dataSet"}, {}]
}]
}]
}],
buttonBar1: ["wm.Panel", {"_classes":{"domNode":["dialogfooter"]},"border":"1","desktopHeight":"32px","enableTouchHeight":true,"height":"32px","horizontalAlign":"right","layoutKind":"left-to-right","mobileHeight":"40px","verticalAlign":"top","width":"100%"}, {}, {
cancelButton2: ["wm.Button", {"caption":"Close","imageIndex":0,"imageList":"app.silkIconList","margin":"4"}, {"onclick":"categoriesDialog.hide"}]
}]
}],
layoutBox1: ["wm.Layout", {"borderColor":"#fbfbfb","horizontalAlign":"left","padding":"5","verticalAlign":"top"}, {}, {
issueForm: ["wm.LiveForm", {"captionSize":"100px","height":"100%","horizontalAlign":"left","readonly":true,"verticalAlign":"top"}, {"onHide":"issueForm.cancelEdit","onHide1":"issueFormHide1"}, {
binding: ["wm.Binding", {}, {}, {
wire: ["wm.Wire", {"expression":undefined,"source":"issueVariable","targetProperty":"dataSet"}, {}]
}],
sequenceEditor1Panel: ["wm.Panel", {"fitToContentHeight":true,"height":"34px","horizontalAlign":"left","layoutKind":"left-to-right","verticalAlign":"middle","width":"100%"}, {}, {
sequenceEditor1: ["wm.Number", {"border":"0","caption":"Issue","changeOnKey":true,"dataValue":0,"desktopHeight":"26px","emptyValue":"zero","formField":"sequence","formatter":"sequenceEditor1ReadOnlyNodeFormat","height":"26px","readonly":true,"required":true,"width":"100%"}, {}],
issueFormEditPanel: ["wm.EditPanel", {"desktopHeight":"32px","height":"32px","isCustomized":true,"liveForm":"issueForm","lock":false,"operationPanel":"operationPanel1","savePanel":"savePanel1"}, {}, {
savePanel1: ["wm.Panel", {"height":"100%","horizontalAlign":"right","layoutKind":"left-to-right","showing":false,"verticalAlign":"top","width":"100%"}, {}, {
saveButton1: ["wm.Button", {"caption":"Save","height":"100%","imageIndex":0,"imageList":"app.silkIconList","margin":"4"}, {"onclick":"issueFormEditPanel.saveData","onclick1":"saveButton1Click1"}, {
binding: ["wm.Binding", {}, {}, {
wire: ["wm.Wire", {"expression":undefined,"source":"issueFormEditPanel.formInvalid","targetProperty":"disabled"}, {}]
}]
}],
cancelButton1: ["wm.Button", {"caption":"Cancel","height":"100%","imageIndex":21,"imageList":"app.silkIconList","margin":"4"}, {"onclick":"issueFormEditPanel.cancelEdit","onclick1":"cancelButton1Click1"}]
}],
operationPanel1: ["wm.Panel", {"height":"100%","horizontalAlign":"right","layoutKind":"left-to-right","verticalAlign":"top","width":"100%"}, {}, {
updateButton1: ["wm.Button", {"caption":"Edit","height":"100%","imageIndex":75,"imageList":"app.silkIconList","margin":"4"}, {"onclick":"issueFormEditPanel.beginDataUpdate","onclick1":"updateButton1Click1"}, {
binding: ["wm.Binding", {}, {}, {
wire: ["wm.Wire", {"expression":undefined,"source":"issueFormEditPanel.formUneditable","targetProperty":"disabled"}, {}]
}]
}],
deleteButton1: ["wm.Button", {"caption":"Delete","height":"100%","imageIndex":21,"imageList":"app.silkIconList","margin":"4","showing":false}, {"onclick":"issueFormEditPanel.deleteData"}, {
binding: ["wm.Binding", {}, {}, {
wire: ["wm.Wire", {"expression":undefined,"source":"issueFormEditPanel.formUneditable","targetProperty":"disabled"}, {}]
}]
}]
}]
}]
}],
commentGridPanel: ["wm.Panel", {"height":"100%","horizontalAlign":"left","layoutKind":"left-to-right","verticalAlign":"top","width":"100%"}, {}, {
commenPanel: ["wm.Panel", {"height":"100%","horizontalAlign":"left","verticalAlign":"top","width":"100%"}, {}, {
descriptionEditor1: ["wm.LargeTextArea", {"border":"0","caption":"Description","captionAlign":"right","captionPosition":"left","captionSize":"100px","changeOnKey":true,"dataValue":"","emptyValue":"emptyString","formField":"description","maxHeight":0,"readonly":true,"width":"100%"}, {}],
commentGrid: ["wm.DojoGrid", {"columns":[{"show":true,"field":"interview.resource.name","title":"From","width":"25%","align":"left","formatFunc":"","editorProps":null,"mobileColumn":false},{"show":true,"field":"description","title":"Comment","width":"100%","align":"left","formatFunc":"","editorProps":{"restrictValues":true},"mobileColumn":false},{"show":false,"field":"applicableToMe","title":"ApplicableToMe","width":"100%","align":"left","formatFunc":"","editorProps":{"restrictValues":true},"mobileColumn":false},{"show":false,"field":"fixed","title":"Fixed","width":"100%","align":"left","formatFunc":"","mobileColumn":false},{"show":false,"field":"PHONE COLUMN","title":"-","width":"100%","align":"left","expression":"\"<div class='MobileRowTitle'>From: \" + ${interview.resource.name} + \"</div>\"\n+ \"<div class='MobileRow'>Comment: \" + ${description} + \"</div>\"\n","mobileColumn":true},{"show":false,"field":"id","title":"Id","width":"80px","align":"right","formatFunc":"","mobileColumn":false},{"show":false,"field":"version","title":"Version","width":"80px","align":"right","formatFunc":"","mobileColumn":false}],"height":"100%","localizationStructure":{},"margin":"4","minDesktopHeight":60,"primaryKeyFields":["id"],"selectionMode":"none"}, {}, {
binding: ["wm.Binding", {}, {}, {
wire: ["wm.Wire", {"expression":undefined,"source":"issueCommentsVariable1","targetProperty":"dataSet"}, {}]
}]
}]
}],
relatedEditor2Panel: ["wm.Panel", {"height":"100%","horizontalAlign":"left","layoutKind":"left-to-right","verticalAlign":"top","width":"50%"}, {}, {
categoriesGrid: ["wm.DojoGrid", {"columns":[{"show":false,"field":"PHONE COLUMN","title":"-","width":"100%","align":"left","expression":"\"<div class='MobileRowTitle'>Categories: \" + ${issueCategory.name} + \"</div>\"\n","mobileColumn":true},{"show":false,"field":"issueCategory.id","title":"IssueCategory.id","width":"80px","align":"right","formatFunc":"","mobileColumn":false},{"show":true,"field":"issueCategory.name","title":"Categories","width":"100%","align":"left","formatFunc":"","editorProps":{"restrictValues":true},"mobileColumn":false}],"height":"100%","margin":"4","minDesktopHeight":60}, {}, {
binding: ["wm.Binding", {}, {}, {
wire: ["wm.Wire", {"expression":undefined,"source":"issue2catVariable1","targetProperty":"dataSet"}, {}]
}]
}],
addCategoryButton: ["wm.Button", {"caption":undefined,"hint":"Edit categories","imageIndex":75,"imageList":"app.silkIconList","margin":"4","showing":false,"width":"32px"}, {"onclick":"categoriesDialog.show"}]
}]
}],
relatedEditor1Panel: ["wm.Panel", {"height":"100%","horizontalAlign":"left","layoutKind":"left-to-right","verticalAlign":"top","width":"100%"}, {}, {
approachGrid1: ["wm.DojoGrid", {"columns":[{"show":false,"field":"id.issueId","title":"IssueId","width":"100%","align":"left","formatFunc":"","editorProps":{"restrictValues":true},"mobileColumn":false},{"show":false,"field":"id.approachId","title":"ApproachId","width":"100%","align":"left","formatFunc":"","editorProps":{"restrictValues":true},"mobileColumn":false},{"show":false,"field":"PHONE COLUMN","title":"-","width":"100%","align":"left","expression":"\"<div class='MobileRowTitle'>Approach: \" + ${approach.name} + \"</div>\"\n+ \"<div class='MobileRow'>Description: \" + ${approach.description} + \"</div>\"\n","mobileColumn":true},{"show":true,"field":"approach.name","title":"Approach","width":"25%","align":"left","formatFunc":"","editorProps":{"restrictValues":true},"mobileColumn":false},{"show":true,"field":"approach.description","title":"Description","width":"100%","align":"left","formatFunc":"","editorProps":{"restrictValues":true},"mobileColumn":false}],"height":"100%","liveEditing":true,"margin":"4","minDesktopHeight":60}, {"onShow":"approachGrid1.deselectAll"}, {
binding: ["wm.Binding", {}, {}, {
wire: ["wm.Wire", {"expression":undefined,"source":"issueApproachesVariable","targetProperty":"dataSet"}, {}]
}]
}],
addApproachButton: ["wm.Button", {"caption":undefined,"hint":"Add relevent approach","imageIndex":75,"imageList":"app.silkIconList","margin":"4","showing":false,"width":"32px"}, {"onclick":"approachDialog1.show"}]
}]
}]
}]
};

IssueForm.prototype._cssText = '';
IssueForm.prototype._htmlText = '';