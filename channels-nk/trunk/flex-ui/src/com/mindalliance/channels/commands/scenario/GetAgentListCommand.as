// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.commands.scenario
{
	import com.adobe.cairngorm.control.CairngormEvent;
	import com.mindalliance.channels.business.scenario.AgentDelegate;
	import com.mindalliance.channels.commands.BaseDelegateCommand;
	import com.mindalliance.channels.events.scenario.*;
	
	import mx.collections.ArrayCollection;
	
	public class GetAgentListCommand extends BaseDelegateCommand
	{
	
        override public function execute(event:CairngormEvent):void
        {
            var evt:GetAgentListEvent = event as GetAgentListEvent;
            if (evt.taskId != null) {
	            var delegate:AgentDelegate = new AgentDelegate( this );
	            log.debug("Retrieving artifact list");
	            delegate.getAgentList(evt.taskId);
            }
        }
        
        override public function result(data:Object):void
        {
            channelsModel.getElementListModel("agents" + data["taskId"]).data = (data["data"] as ArrayCollection);
            log.debug("Successfully retrieved artifact list");
        }

	}
}