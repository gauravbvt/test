// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.commands.scenario
{
	import com.adobe.cairngorm.control.CairngormEvent;
	import com.mindalliance.channels.business.scenario.AcquirementDelegate;
	import com.mindalliance.channels.commands.BaseDelegateCommand;
	import com.mindalliance.channels.events.scenario.*;
	
	import mx.collections.ArrayCollection;
	
	public class GetAcquirementListCommand extends BaseDelegateCommand
	{
        override public function execute(event:CairngormEvent):void
        {
            var evt:GetAcquirementListEvent = event as GetAcquirementListEvent;
            var delegate:AcquirementDelegate = new AcquirementDelegate( this );
            log.debug("Retrieving acquirement list");
            delegate.getAcquirementList(evt.scenarioId);
        }
        
        override public function result(data:Object):void
        {
            channelsModel.getElementListModel("acquirements").data = (data["data"] as ArrayCollection);
            log.debug("Successfully retrieved acquirement list");
        }
        
        override public function fault(info:Object):void
        {
            channelsModel.getElementListModel("acquirements").data  = null;
            super.fault(info);
        }
	}
}