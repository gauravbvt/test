// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.events.categories
{
	import com.adobe.cairngorm.control.CairngormEvent;
	import com.mindalliance.channels.model.categories.CategoryViewerModel;

	public class RemoveCategoriesFromSetEvent extends CairngormEvent
	{
		public static const RemoveCategoriesFromSet_Event:String = "<RemoveCategoriesFromSetEvent>";
		
		public var categories : Array;
		public var model : CategoryViewerModel;
		
		public function RemoveCategoriesFromSetEvent(categories : Array, model : CategoryViewerModel) 
		{
			super( RemoveCategoriesFromSet_Event );
			this.categories = categories;
			this.model = model;
		}
	}
}