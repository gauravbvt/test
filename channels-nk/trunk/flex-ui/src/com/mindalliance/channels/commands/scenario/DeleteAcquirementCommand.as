// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.commands.scenario
{
	import com.adobe.cairngorm.control.CairngormEvent;
	import com.adobe.cairngorm.control.CairngormEventDispatcher;
	import com.mindalliance.channels.business.scenario.AcquirementDelegate;
	import com.mindalliance.channels.events.scenario.*;
	import com.mindalliance.channels.commands.BaseDelegateCommand;
	
	public class DeleteAcquirementCommand extends BaseDelegateCommand
	{
	
		override public function execute(event:CairngormEvent):void
		{
			var evt:DeleteAcquirementEvent = event as DeleteAcquirementEvent;
			var delegate:AcquirementDelegate = new AcquirementDelegate( this );
			delegate.deleteElement(evt.id);
		}
		
		override public function result(data:Object):void
		{
            var result:Boolean = data["data"] as Boolean;
            if (result == true) {
                CairngormEventDispatcher.getInstance().dispatchEvent( new GetAcquirementListEvent(channelsModel.currentScenario.id) );
                log.info("Acquirement successfully deleted");
            } else {
                log.warn("Acquirement Deletion failed");   
            }			
		}
	}
}