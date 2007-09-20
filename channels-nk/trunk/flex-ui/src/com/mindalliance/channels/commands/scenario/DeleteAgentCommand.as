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
	
	import mx.collections.ArrayCollection;
	
	public class DeleteAgentCommand extends BaseDelegateCommand
	{
	
        override public function execute(event:CairngormEvent):void
        {
            var evt:DeleteAgentEvent = event as DeleteAgentEvent;
            var delegate:AgentDelegate = new AgentDelegate( this );
            var param : Array = new Array();
            param["taskId"] = evt.taskId;
            delegate.deleteElement(evt.id);
        }
        
        override public function result(data:Object):void
        {
            var result:Boolean = data["data"] as Boolean;
            if (result == true) {
                var col : ArrayCollection = channelsModel.getElementListModel("agents").data;
                if (col != null) {
                    var inx: int = ElementHelper.findElementIndexById(data["id"], col);
                    col.removeItemAt(inx);
                }
                col  = channelsModel.getElementListModel("agents" + data["taskId"]).data;
                if (col != null) {
                    var inx: int = ElementHelper.findElementIndexById(data["id"], col);
                    col.removeItemAt(inx);
                }
                CairngormEventDispatcher.getInstance().dispatchEvent( new GetAgentListEvent(data["taskId"]) );
                log.info("Agent successfully deleted");
            } else {
                log.warn("Agent Deletion failed");   
            }           
        }
	}
}