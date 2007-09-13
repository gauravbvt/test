// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.commands.people
{
	import com.adobe.cairngorm.control.CairngormEvent;
	import com.adobe.cairngorm.control.CairngormEventDispatcher;
	import com.mindalliance.channels.business.people.OrganizationDelegate;
	import com.mindalliance.channels.commands.BaseDelegateCommand;
	import com.mindalliance.channels.events.people.*;
	import com.mindalliance.channels.vo.OrganizationVO;
	import com.mindalliance.channels.vo.common.ElementVO;
	
	public class CreateOrganizationCommand extends BaseDelegateCommand
	{
	
		override public function execute(event:CairngormEvent):void
		{
			var evt:CreateOrganizationEvent = event as CreateOrganizationEvent;
			var name : String = evt.name;
			
			log.debug("Creating Organization " + name);
			
			var delegate:OrganizationDelegate = new OrganizationDelegate( this );
			delegate.create(name);
		}
		
		override public function result(data:Object):void
		{
			var result:OrganizationVO = data["data"] as OrganizationVO;
			if (result!=null) {
				log.info("Organization created");
				channelsModel.getElementListModel('organizations').data.addItem(new ElementVO(result.id, result.name));
				//CairngormEventDispatcher.getInstance().dispatchEvent( new GetOrganizationListEvent() );
			}
		}
	}
}