// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.events.categories
{
	import com.adobe.cairngorm.control.CairngormEvent;

	public class GetCategoryListEvent extends CairngormEvent
	{
		public static const GetCategoryList_Event:String = "<GetCategoryListEvent>";
		public var taxonomy : String;
		public function GetCategoryListEvent(taxonomy : String) 
		{
			super( GetCategoryList_Event );
			this.taxonomy = taxonomy;
		}
	}
}