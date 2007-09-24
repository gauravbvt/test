// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.events.categories
{
	import com.adobe.cairngorm.control.CairngormEvent;

	public class GetDisciplineListEvent extends CairngormEvent
	{
		public static const GetDisciplineList_Event:String = "<GetDisciplineListEvent>";
		public var taxonomy : String;
		public function GetDisciplineListEvent(taxonomy : String) 
		{
			super( GetDisciplineList_Event );
			this.taxonomy = taxonomy;
		}
	}
}