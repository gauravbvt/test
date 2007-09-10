// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.commands.scenario
{
	import com.adobe.cairngorm.control.CairngormEvent;
	import com.adobe.cairngorm.control.CairngormEventDispatcher;
	import com.mindalliance.channels.business.scenario.EventDelegate;
	import com.mindalliance.channels.events.scenario.*;
	import com.mindalliance.channels.commands.BaseDelegateCommand;
	
	public class CreateEventCommand extends BaseDelegateCommand
	{
	
		override public function execute(event:CairngormEvent):void
		{
			var evt:CreateEventEvent = event as CreateEventEvent;
			var delegate:EventDelegate = new EventDelegate( this );
		}
		
		override public function result(data:Object):void
		{
			
		}
	}
}