// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.commands.people
{
	import com.adobe.cairngorm.control.CairngormEvent;
	import com.mindalliance.channels.business.people.RoleDelegate;
	import com.mindalliance.channels.commands.BaseDelegateCommand;
	import com.mindalliance.channels.events.people.*;
	
	import mx.collections.ArrayCollection;
	
	public class GetRoleListCommand extends BaseDelegateCommand
	{
	
		override public function execute(event:CairngormEvent):void
		{
			var evt:GetRoleListEvent = event as GetRoleListEvent;
			var delegate:RoleDelegate = new RoleDelegate( this );
			log.debug("Retrieving role list");
			delegate.getRoleList();
		}
		
        override public function result(data:Object):void
        {
            channelsModel.getElementListModel("roles").data = (data["data"] as ArrayCollection);
            log.debug("Successfully retrieved role list");
        }
        
        override public function fault(info:Object):void
        {
            channelsModel.getElementListModel("roles").data  = null;
            super.fault(info);
        }
	}
}