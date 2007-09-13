// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.commands.people
{
	import com.adobe.cairngorm.control.CairngormEvent;
	import com.mindalliance.channels.business.people.RoleDelegate;
	import com.mindalliance.channels.commands.BaseDelegateCommand;
	import com.mindalliance.channels.events.people.*;
	import com.mindalliance.channels.vo.RoleVO;
	import com.mindalliance.channels.vo.common.ElementVO;
	
	public class CreateRoleCommand extends BaseDelegateCommand
	{
	
		override public function execute(event:CairngormEvent):void
		{
			var evt:CreateRoleEvent = event as CreateRoleEvent;
			var delegate:RoleDelegate = new RoleDelegate( this );
			delegate.create(evt.name, evt.organizationId);
		}
		
		override public function result(data:Object):void
		{
			var result:RoleVO = data["data"] as RoleVO;
            if (result!=null) {
                log.info("Role created");
                channelsModel.getElementListModel('roles').data.addItem(new ElementVO(result.id, result.name));
                //CairngormEventDispatcher.getInstance().dispatchEvent( new GetOrganizationListEvent() );
            }
		}
	}
}