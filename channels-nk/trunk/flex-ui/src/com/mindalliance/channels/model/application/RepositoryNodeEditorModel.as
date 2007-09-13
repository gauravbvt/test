package com.mindalliance.channels.model.application
{
	import com.mindalliance.channels.model.ChannelsModelLocator;
	import com.mindalliance.channels.model.EditorModel;
	
	[Bindable]
	public class RepositoryNodeEditorModel
	{
		public function RepositoryNodeEditorModel(organizationModel : EditorModel, repositoryModel : EditorModel)
		{
		  this.organizationModel = organizationModel;
		  this.repositoryModel = repositoryModel;	
		}
		
		public var organizationModel : EditorModel;
		public var repositoryModel : EditorModel;
	}
}