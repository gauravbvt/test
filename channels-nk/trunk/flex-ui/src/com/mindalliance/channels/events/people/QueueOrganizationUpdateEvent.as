// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.events.people
{
	import com.adobe.cairngorm.control.CairngormEvent;
	import com.mindalliance.channels.model.EditorModel;

	public class QueueOrganizationUpdateEvent extends CairngormEvent
	{
		public static const QueueOrganizationUpdate_Event:String = "<QueueOrganizationUpdateEvent>";
		
		public var model : EditorModel;
		
		public function QueueOrganizationUpdateEvent(model : EditorModel) 
		{
			super( QueueOrganizationUpdate_Event );
			this.model = model;
		}
	}
}