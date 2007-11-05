package com.mindalliance.channels.common.events
{
	import com.adobe.cairngorm.control.CairngormEvent;
	import com.mindalliance.channels.model.EditorModel;

	public class UpdateElementEvent extends CairngormEvent
	{
        public static const UpdateElement_Event:String = "<UpdateElementEvent>";
        public var model : EditorModel;
        public var parameters : Object;
        
		public function UpdateElementEvent(model : EditorModel, parameters:Object, event : String = UpdateElement_Event)
		{
			super(event);
			this.model = model;
			this.parameters = parameters;
		}
		
	}
}