package com.mindalliance.channels.model.application
{
	import com.mindalliance.channels.model.ChannelsModelLocator;
	import com.mindalliance.channels.model.EditorModel;
	
	[Bindable]
	public class RepositoryNodeEditorModel
	{
		public var organizationModel : EditorModel = ChannelsModelLocator.getInstance().getEditorModel();
		public var repositoryModel : EditorModel = ChannelsModelLocator.getInstance().getEditorModel();
	}
}