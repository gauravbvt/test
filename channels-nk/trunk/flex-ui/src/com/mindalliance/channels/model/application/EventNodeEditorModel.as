package com.mindalliance.channels.model.application
{
	import com.mindalliance.channels.model.ChannelsModelLocator;
	import com.mindalliance.channels.model.EditorModel;
	
	[Bindable]
	public class EventNodeEditorModel
	{
		public function EventNodeEditorModel(eventModel : EditorModel) {
			this.eventModel = eventModel;
		}
        public var eventModel : EditorModel;
	}
}