// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.events.people
{
	import com.adobe.cairngorm.control.CairngormEvent;
	import com.mindalliance.channels.model.EditorModel;

	public class ChangePasswordEvent extends CairngormEvent
	{
		public static const ChangePassword_Event:String = "<ChangePasswordEvent>";
		
		public var model : EditorModel;
		public var oldPassword : String;
		public var newPassword : String;
		
		public function ChangePasswordEvent(model : EditorModel, oldPassword : String, newPassword : String) 
		{
			super( ChangePassword_Event );
			this.model = model;
			this.oldPassword = oldPassword;
			this.newPassword = newPassword;
		}
	}
}