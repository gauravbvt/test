// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.commands.scenario
{
	import com.adobe.cairngorm.control.CairngormEvent;
	import com.adobe.cairngorm.control.CairngormEventDispatcher;
	import com.mindalliance.channels.business.scenario.AgentDelegate;
	import com.mindalliance.channels.commands.BaseDelegateCommand;
	import com.mindalliance.channels.events.scenario.*;
	import com.mindalliance.channels.util.ElementHelper;
	import com.mindalliance.channels.vo.AgentVO;
	
	public class CreateAgentCommand extends BaseDelegateCommand
	{
	
    
        override public function execute(event:CairngormEvent):void
        {
            var evt:CreateAgentEvent = event as CreateAgentEvent;
            var delegate:AgentDelegate = new AgentDelegate( this );
            if (evt.taskId != null && evt.roleId != null) {
                delegate.create(evt.name, evt.taskId, evt.roleId);
            }
        }
        
        override public function result(data:Object):void
        {
            var result:AgentVO = data["data"] as AgentVO;
            if (result!=null) {
                log.info("Agent created");
                
                CairngormEventDispatcher.getInstance().dispatchEvent( new GetAgentListByScenarioEvent(channelsModel.currentScenario.id) );
                CairngormEventDispatcher.getInstance().dispatchEvent( new GetAgentListEvent(data["taskId"]) );
                
                //CairngormEventDispatcher.getInstance().dispatchEvent( new GetOrganizationListEvent() );
            }
        }
	}
}