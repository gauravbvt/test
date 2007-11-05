// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.common.events
{
	import com.adobe.cairngorm.control.CairngormEvent;
	import com.mindalliance.channels.model.EditorModel;

	public class GetElementEvent extends CairngormEvent
	{
		public static const GetElement_Event : String = "<GetElementEvent>";
        public var id : String;
        public var model : EditorModel;
		public function GetElementEvent( id : String, model : EditorModel = null, event : String = GetElement_Event) 
		{
			super( event );            
			this.id = id;
            this.model = model;
		}
	}
}