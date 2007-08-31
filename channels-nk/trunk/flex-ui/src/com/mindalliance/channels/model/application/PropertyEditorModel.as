package com.mindalliance.channels.model.application
{
	import com.mindalliance.channels.model.BaseChannelsModel;
	import com.mindalliance.channels.model.ChooserModel;
	import com.mindalliance.channels.model.EditorModel;

	
	[Bindable]
	public class PropertyEditorModel extends  BaseChannelsModel
    {
        
        public function PropertyEditorModel(elements : Object, elementLists : Object) {
            super(elements, elementLists);
            
        }

		public var taskEditorModel : EditorModel = new EditorModel(elements, elementLists);
		public var taskChooserModel : ChooserModel  = new ChooserModel(elements, elementLists);
		public var eventEditorModel : EditorModel = new EditorModel(elements, elementLists);
		public var eventChooserModel : ChooserModel = new ChooserModel(elements, elementLists);
		public var artifactEditorModel : EditorModel = new EditorModel(elements, elementLists);
		public var artifactChooserModel : ChooserModel = new ChooserModel(elements, elementLists);
		public var roleEditorModel : EditorModel = new EditorModel(elements, elementLists);
		public var roleChooserModel : ChooserModel = new ChooserModel(elements, elementLists);
		public var repositoryEditorModel : EditorModel = new EditorModel(elements, elementLists);
		public var repositoryChooserModel : ChooserModel = new ChooserModel(elements, elementLists);
	}
}