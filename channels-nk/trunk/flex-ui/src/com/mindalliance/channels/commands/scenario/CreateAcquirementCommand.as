// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.commands.scenario
{
	import com.adobe.cairngorm.control.CairngormEvent;
	import com.mindalliance.channels.business.scenario.AcquirementDelegate;
	import com.mindalliance.channels.commands.BaseDelegateCommand;
	import com.mindalliance.channels.events.scenario.*;
	import com.mindalliance.channels.vo.AcquirementVO;
	import com.mindalliance.channels.vo.common.ElementVO;
	
	public class CreateAcquirementCommand extends BaseDelegateCommand
	{
	
		override public function execute(event:CairngormEvent):void
		{
			var evt:CreateAcquirementEvent = event as CreateAcquirementEvent;
			var delegate:AcquirementDelegate = new AcquirementDelegate( this );
			delegate.create(evt.name, evt.taskId);
		}
		
		override public function result(data:Object):void
		{
            var result:AcquirementVO = data["data"] as AcquirementVO;
            if (result!=null) {
                log.info("Acquirement created");
                channelsModel.getElementListModel('acquirements').data.addItem(new ElementVO(result.id, result.name));
                //CairngormEventDispatcher.getInstance().dispatchEvent( new GetOrganizationListEvent() );
            }			
		}
	}
}