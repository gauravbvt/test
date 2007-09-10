// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.events.categories
{
	import com.adobe.cairngorm.control.CairngormEvent;

	public class UpdateCategoryEvent extends CairngormEvent
	{
		public static const UpdateCategory_Event:String = "<UpdateCategoryEvent>";
		
		public function UpdateCategoryEvent() 
		{
			super( UpdateCategory_Event );
		}
	}
}