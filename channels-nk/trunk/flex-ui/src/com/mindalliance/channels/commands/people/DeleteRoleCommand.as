// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.commands.people
{
	import com.adobe.cairngorm.control.CairngormEvent;
	import com.mindalliance.channels.business.people.RoleDelegate;
	import com.mindalliance.channels.commands.BaseDelegateCommand;
	import com.mindalliance.channels.events.people.*;
	import com.mindalliance.channels.util.ElementHelper;
	
	import mx.collections.ArrayCollection;
	
	public class DeleteRoleCommand extends BaseDelegateCommand
	{
	
		override public function execute(event:CairngormEvent):void
		{
			var evt:DeleteRoleEvent = event as DeleteRoleEvent;
			var delegate:RoleDelegate = new RoleDelegate( this );
			delegate.deleteElement(evt.id);
		}
		
		override public function result(data:Object):void
		{
            var result:Boolean = data["data"] as Boolean;
            if (result == true) {
                var col : ArrayCollection = channelsModel.getElementListModel("roles").data;
                if (col != null) {
                    var inx: int = ElementHelper.findElementIndexById(data["id"], col);
                    col.removeItemAt(inx);
                }
                log.info("Role successfully deleted");
            } else {
                log.warn("Role Deletion failed");   
            }			
		}
	}
}