// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.commands.scenario
{
	import com.adobe.cairngorm.control.CairngormEvent;
	import com.mindalliance.channels.business.scenario.AcquirementDelegate;
	import com.mindalliance.channels.commands.BaseDelegateCommand;
	import com.mindalliance.channels.events.scenario.*;
	
	import mx.collections.ArrayCollection;
	
	public class GetAcquirementListByTaskCommand extends BaseDelegateCommand
	{
	
		override public function execute(event:CairngormEvent):void
		{
			var evt:GetAcquirementListByTaskEvent = event as GetAcquirementListByTaskEvent;
			if (evt.taskId != null) {
                var delegate:AcquirementDelegate = new AcquirementDelegate( this );
                delegate.getAcquirementListByTask(evt.taskId);
            }
		}
		
		override public function result(data:Object):void
		{
            channelsModel.getElementListModel("acquirements" + data["taskId"]).data = (data["data"] as ArrayCollection);
            log.debug("Successfully retrieved task acquirement list");
		}
	}
}