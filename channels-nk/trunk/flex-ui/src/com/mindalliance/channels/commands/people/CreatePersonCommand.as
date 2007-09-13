// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.commands.people
{
	import com.adobe.cairngorm.control.CairngormEvent;
	import com.mindalliance.channels.business.people.PersonDelegate;
	import com.mindalliance.channels.commands.BaseDelegateCommand;
	import com.mindalliance.channels.events.people.*;
	import com.mindalliance.channels.vo.PersonVO;
	import com.mindalliance.channels.vo.common.ElementVO;
	
	public class CreatePersonCommand extends BaseDelegateCommand
	{
	
		override public function execute(event:CairngormEvent):void
		{
			var evt:CreatePersonEvent = event as CreatePersonEvent;
			var delegate:PersonDelegate = new PersonDelegate( this );
			delegate.create(evt.firstName, evt.lastName);
		}
		
		override public function result(data:Object):void
		{
			var result:PersonVO = data["data"] as PersonVO;
            if (result!=null) {
                log.info("Person created");
                channelsModel.getElementListModel('people').data.addItem(new ElementVO(result.id, result.name));
                //CairngormEventDispatcher.getInstance().dispatchEvent( new GetOrganizationListEvent() );
            }
		}
	}
}