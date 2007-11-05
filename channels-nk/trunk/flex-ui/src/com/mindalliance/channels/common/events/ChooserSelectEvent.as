// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.common.events
{
	import com.adobe.cairngorm.control.CairngormEvent;
	import com.mindalliance.channels.model.ChooserModel;
	
	import mx.collections.ArrayCollection;

	public class ChooserSelectEvent extends CairngormEvent
	{
		public static const ChooserSelect_Event:String = "<ChooserSelectEvent>";
		
		public var model : ChooserModel;
		public var selection : Array;
		
		public function ChooserSelectEvent(model : ChooserModel, selection : Array) 
		{
			super( ChooserSelect_Event );
			this.model = model ;
			this.selection = selection;
		}
	}
}