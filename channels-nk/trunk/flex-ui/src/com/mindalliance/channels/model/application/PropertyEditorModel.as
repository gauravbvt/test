package com.mindalliance.channels.model.application
{
	import com.mindalliance.channels.model.BaseChannelsModel;
	import com.mindalliance.channels.model.people.RoleChooserModel;
	import com.mindalliance.channels.model.people.RoleEditorModel;
	import com.mindalliance.channels.model.resources.EventChooserModel;
	import com.mindalliance.channels.model.resources.EventEditorModel;
	import com.mindalliance.channels.model.resources.RepositoryChooserModel;
	import com.mindalliance.channels.model.resources.RepositoryEditorModel;
	import com.mindalliance.channels.model.resources.TaskChooserModel;
	import com.mindalliance.channels.model.resources.TaskEditorModel;
	import com.mindalliance.channels.model.scenario.ArtifactChooserModel;
	import com.mindalliance.channels.model.scenario.ArtifactEditorModel;
	
	[Bindable]
	public class PropertyEditorModel extends  BaseChannelsModel
    {
        
        public function PropertyEditorModel(elements : Object, elementLists : Object) {
            super(elements, elementLists);
            
        }

		public var taskEditorModel : TaskEditorModel = new TaskEditorModel(elements, elementLists);
		public var taskChooserModel : TaskChooserModel  = new TaskChooserModel(elements, elementLists);
		public var eventEditorModel : EventEditorModel = new EventEditorModel(elements, elementLists);
		public var eventChooserModel : EventChooserModel = new EventChooserModel(elements, elementLists);
		public var artifactEditorModel : ArtifactEditorModel = new ArtifactEditorModel(elements, elementLists);
		public var artifactChooserModel : ArtifactChooserModel = new ArtifactChooserModel(elements, elementLists);
		public var roleEditorModel : RoleEditorModel = new RoleEditorModel(elements, elementLists);
		public var roleChooserModel : RoleChooserModel = new RoleChooserModel(elements, elementLists);
		public var repositoryEditorModel : RepositoryEditorModel = new RepositoryEditorModel(elements, elementLists);
		public var repositoryChooserModel : RepositoryChooserModel = new RepositoryChooserModel(elements, elementLists);
	}
}