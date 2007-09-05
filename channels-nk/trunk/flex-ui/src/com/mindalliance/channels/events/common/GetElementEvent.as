// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.events.common
{
	import com.adobe.cairngorm.control.CairngormEvent;
    import com.mindalliance.channels.model.EditorModel;

	public class GetElementEvent extends CairngormEvent
	{
		
        public var id : String;
        public var model : EditorModel;
		public function GetElementEvent(event : String, id : String, model : EditorModel = null) 
		{
			super( event );            
			this.id = id;
            this.model = model;
		}
	}
}