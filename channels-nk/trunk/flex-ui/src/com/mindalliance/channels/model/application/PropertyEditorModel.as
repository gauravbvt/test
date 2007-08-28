package com.mindalliance.channels.model.application
{
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
	import com.mindalliance.channels.model.IChannelsModel;
	
	[Bindable]
	public class PropertyEditorModel implements IChannelsModel
	{

		public var taskEditorModel : TaskEditorModel = new TaskEditorModel();
		public var taskChooserModel : TaskChooserModel  = new TaskChooserModel();
		public var eventEditorModel : EventEditorModel = new EventEditorModel();
		public var eventChooserModel : EventChooserModel = new EventChooserModel();
		public var artifactEditorModel : ArtifactEditorModel = new ArtifactEditorModel();
		public var artifactChooserModel : ArtifactChooserModel = new ArtifactChooserModel();
		public var roleEditorModel : RoleEditorModel = new RoleEditorModel();
		public var roleChooserModel : RoleChooserModel = new RoleChooserModel();
		public var repositoryEditorModel : RepositoryEditorModel = new RepositoryEditorModel();
		public var repositoryChooserModel : RepositoryChooserModel = new RepositoryChooserModel();
	}
}