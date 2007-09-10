// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.events.categories
{
	import com.adobe.cairngorm.control.CairngormEvent;

	public class GetDisciplineListEvent extends CairngormEvent
	{
		public static const GetDisciplineList_Event:String = "<GetDisciplineListEvent>";
		
		public function GetDisciplineListEvent() 
		{
			super( GetDisciplineList_Event );
		}
	}
}