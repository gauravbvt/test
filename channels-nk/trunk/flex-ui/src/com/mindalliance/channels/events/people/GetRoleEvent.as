// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.events.people
{
	import com.adobe.cairngorm.control.CairngormEvent;
	import com.mindalliance.channels.model.EditorModel;

	public class GetRoleEvent extends CairngormEvent
	{
		public static const GetRole_Event:String = "<GetRoleEvent>";
		public var id : String;
		public var model : EditorModel;
		public function GetRoleEvent(id : String, model : EditorModel) 
		{
			super( GetRole_Event );
			this.id = id;
			this.model = model;
		}
	}
}