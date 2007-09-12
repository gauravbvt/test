package com.mindalliance.channels.model.application
{
	import com.mindalliance.channels.model.ChannelsModelLocator;
	import com.mindalliance.channels.model.EditorModel;
	
	[Bindable]
	public class EventNodeEditorModel
	{
		
        public var eventModel : EditorModel = ChannelsModelLocator.getInstance().getEditorModel();
	}
}