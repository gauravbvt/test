// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.events.people
{
	import com.adobe.cairngorm.control.CairngormEvent;

	public class GetRoleListEvent extends CairngormEvent
	{
		public static const GetRoleList_Event:String = "<GetRoleListEvent>";
		
		public function GetRoleListEvent() 
		{
			super( GetRoleList_Event );
		}
	}
}