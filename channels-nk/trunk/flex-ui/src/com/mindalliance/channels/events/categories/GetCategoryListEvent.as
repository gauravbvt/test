// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.events.categories
{
	import com.adobe.cairngorm.control.CairngormEvent;

	public class GetCategoryListEvent extends CairngormEvent
	{
		public static const GetCategoryList_Event:String = "<GetCategoryListEvent>";
		
		public function GetCategoryListEvent() 
		{
			super( GetCategoryList_Event );
		}
	}
}