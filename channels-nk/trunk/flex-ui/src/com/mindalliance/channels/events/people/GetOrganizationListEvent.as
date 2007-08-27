// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.events.people
{
	import com.adobe.cairngorm.control.CairngormEvent;

	public class GetOrganizationListEvent extends CairngormEvent
	{
		public static const GetOrganizationList_Event:String = "<GetOrganizationListEvent>";
		
		public function GetOrganizationListEvent() 
		{
			super( GetOrganizationList_Event );
		}
	}
}