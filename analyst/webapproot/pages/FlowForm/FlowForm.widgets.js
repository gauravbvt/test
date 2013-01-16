FlowForm.widgets = {
	flowFormDataSet: ["wm.Property", {"bindSource":1,"bindTarget":1,"property":"flowForm.dataSet","readonly":true,"type":"com.analystdb.data.Flow"}, {}],
	layoutBox1: ["wm.Layout", {"horizontalAlign":"left","verticalAlign":"top"}, {}, {
		flowForm: ["wm.DataForm", {"height":"100%","isCompositeKey":false,"type":"com.analystdb.data.Flow"}, {}, {
			dataForm1EditorFormPanel: ["wm.FormPanel", {"desktopHeight":"100%","fitToContentHeight":true,"height":"206px","mobileHeight":"100%"}, {}, {
				fromActorEditor1: ["wm.Text", {"border":"0","caption":"FromActor","captionSize":"120px","changeOnKey":true,"dataValue":"","desktopHeight":"26px","emptyValue":"emptyString","formField":"fromActor","height":"26px","width":"100%"}, {}],
				toActorEditor1: ["wm.Text", {"border":"0","caption":"ToActor","captionSize":"120px","changeOnKey":true,"dataValue":"","desktopHeight":"26px","emptyValue":"emptyString","formField":"toActor","height":"26px","width":"100%"}, {}],
				nameEditor1: ["wm.Text", {"border":"0","caption":"Name","captionSize":"120px","changeOnKey":true,"dataValue":"","desktopHeight":"26px","emptyValue":"emptyString","formField":"name","height":"26px","required":true,"width":"100%"}, {}],
				descriptionEditor1: ["wm.LargeTextArea", {"border":"0","caption":"Description","captionAlign":"right","captionPosition":"left","captionSize":"120px","changeOnKey":true,"dataValue":"","emptyValue":"emptyString","formField":"description","width":"100%"}, {}],
				documentsLookup1: ["wm.Lookup", {"caption":"Documents","captionSize":"120px","dataType":"com.analystdb.data.Documents","dataValue":"","desktopHeight":"26px","displayField":"document","formField":"documents","height":"26px","width":"100%"}, {}]
			}],
			dataForm1OneToManyFormPanel: ["wm.FormPanel", {"captionAlign":"left","captionPosition":"top","captionSize":"20px","height":"100%","layoutKind":"left-to-right"}, {}, {
				flowAttributesOneToMany1: ["wm.OneToMany", {"caption":"FlowAttributes","captionAlign":"left","captionPosition":"top","captionSize":"20px","dataValue":undefined,"displayField":"name","formField":"flowAttributes","height":"100%","minDesktopHeight":100,"width":"100%"}, {}],
				issueCommentFlowsesOneToMany1: ["wm.OneToMany", {"caption":"IssueCommentFlowses","captionAlign":"left","captionPosition":"top","captionSize":"20px","dataValue":undefined,"displayField":"id","formField":"issueCommentFlowses","height":"100%","minDesktopHeight":100,"width":"100%"}, {}]
			}]
		}]
	}]
}