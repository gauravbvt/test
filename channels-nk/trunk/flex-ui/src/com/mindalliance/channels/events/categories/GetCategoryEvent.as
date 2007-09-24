// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.events.categories
{
	import com.mindalliance.channels.events.common.GetElementEvent;
	import com.mindalliance.channels.model.EditorModel;

	public class GetCategoryEvent extends GetElementEvent
	{
		public static const GetCategory_Event:String = "<GetCategoryEvent>";
		public function GetCategoryEvent(id : String, model : EditorModel) 
        {
            super( GetCategory_Event, id, model );
        }
	}
}