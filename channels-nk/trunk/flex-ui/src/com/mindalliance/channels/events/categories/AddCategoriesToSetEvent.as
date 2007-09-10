// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.events.categories
{
	import com.adobe.cairngorm.control.CairngormEvent;
	import com.mindalliance.channels.model.categories.CategoryViewerModel;

	public class AddCategoriesToSetEvent extends CairngormEvent
	{
		public static const AddCategoriesToSet_Event:String = "<AddCategoriesToSetEvent>";
		public var categories : Array;
        public var model : CategoryViewerModel;
		public function AddCategoriesToSetEvent(categories : Array, model :CategoryViewerModel) 
		{
			super( AddCategoriesToSet_Event );
			this.categories = categories;
			this.model = model;
		}
	}
}