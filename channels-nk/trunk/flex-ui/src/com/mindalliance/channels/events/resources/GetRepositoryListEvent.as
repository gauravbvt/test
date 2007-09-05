// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.events.resources
{
	import com.adobe.cairngorm.control.CairngormEvent;

	public class GetRepositoryListEvent extends CairngormEvent
	{
		public static const GetRepositoryList_Event:String = "<GetRepositoryListEvent>";
		
		public function GetRepositoryListEvent() 
		{
			super( GetRepositoryList_Event );
		}
	}
}