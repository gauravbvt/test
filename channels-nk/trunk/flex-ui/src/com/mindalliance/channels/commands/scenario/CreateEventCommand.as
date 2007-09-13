// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.commands.scenario
{
	import com.adobe.cairngorm.control.CairngormEvent;
	import com.mindalliance.channels.business.scenario.EventDelegate;
	import com.mindalliance.channels.commands.BaseDelegateCommand;
	import com.mindalliance.channels.events.scenario.*;
	import com.mindalliance.channels.vo.EventVO;
	import com.mindalliance.channels.vo.common.ElementVO;
	
	public class CreateEventCommand extends BaseDelegateCommand
	{
	
		override public function execute(event:CairngormEvent):void
		{
			var evt:CreateEventEvent = event as CreateEventEvent;
			var delegate:EventDelegate = new EventDelegate( this );
			delegate.create(evt.name, evt.scenarioId);
		}
		
		override public function result(data:Object):void
		{
			var result:EventVO = data["data"] as EventVO;
            if (result!=null) {
                log.info("Event created");
                channelsModel.getElementListModel('events').data.addItem(new ElementVO(result.id, result.name));
                //CairngormEventDispatcher.getInstance().dispatchEvent( new GetOrganizationListEvent() );
            }
		}
	}
}