// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.commands.scenario
{
	import com.adobe.cairngorm.control.CairngormEvent;
	import com.adobe.cairngorm.control.CairngormEventDispatcher;
	import com.mindalliance.channels.business.scenario.EventDelegate;
	import com.mindalliance.channels.events.scenario.*;
	import com.mindalliance.channels.commands.BaseDelegateCommand;
	
	public class DeleteEventCommand extends BaseDelegateCommand
	{
	
		override public function execute(event:CairngormEvent):void
		{
			var evt:DeleteEventEvent = event as DeleteEventEvent;
			var delegate:EventDelegate = new EventDelegate( this );
			delegate.deleteElement(evt.id);
		}
		
		override public function result(data:Object):void
		{
            var result:Boolean = data["data"] as Boolean;
            if (result == true) {
                CairngormEventDispatcher.getInstance().dispatchEvent( new GetEventListEvent(channelsModel.currentScenario.id) );
                log.info("Event successfully deleted");
            } else {
                log.warn("Event Deletion failed");   
            }       			
		}
	}
}