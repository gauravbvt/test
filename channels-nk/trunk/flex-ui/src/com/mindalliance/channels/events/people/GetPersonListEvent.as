// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.events.people
{
	import com.adobe.cairngorm.control.CairngormEvent;

	public class GetPersonListEvent extends CairngormEvent
	{
		public static const GetPersonList_Event:String = "<GetPersonListEvent>";
		
		public function GetPersonListEvent() 
		{
			super( GetPersonList_Event );
		}
	}
}