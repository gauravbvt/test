package com.mindalliance.channels.model.application
{
	import com.mindalliance.channels.model.ChannelsModelLocator;
	import com.mindalliance.channels.model.EditorModel;
	
	[Bindable]
	public class TaskNodeEditorModel
	{
		public var taskModel : EditorModel = ChannelsModelLocator.getInstance().getEditorModel();
		public var roleModel : EditorModel = ChannelsModelLocator.getInstance().getEditorModel();
	}
}