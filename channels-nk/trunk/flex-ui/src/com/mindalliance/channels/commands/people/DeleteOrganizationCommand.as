// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.commands.people
{
	import com.adobe.cairngorm.control.CairngormEvent;
	import com.mindalliance.channels.business.people.OrganizationDelegate;
	import com.mindalliance.channels.commands.BaseDelegateCommand;
	import com.mindalliance.channels.events.people.*;
	import com.mindalliance.channels.util.ElementHelper;
	
	import mx.collections.ArrayCollection;
	
	public class DeleteOrganizationCommand extends BaseDelegateCommand
	{
	
		override public function execute(event:CairngormEvent):void
		{
			var evt:DeleteOrganizationEvent = event as DeleteOrganizationEvent;
			var id : String = evt.id;
			
			log.debug("Deleting Organization...");
			
			var delegate:OrganizationDelegate = new OrganizationDelegate( this );
			delegate.deleteElement(id);
		}
		
		override public function result(data:Object):void
		{
			var result:Boolean = data["data"] as Boolean;
			if (result == true) {
 	        	var col : ArrayCollection = channelsModel.getElementListModel("organizations").data;
                if (col != null) {
                    var inx: int = ElementHelper.findElementIndexById(data["id"], col);
                    col.removeItemAt(inx);
                }
                log.info("Organization successfully deleted");
			} else {
				log.warn("Organization Deletion failed");	
			}
		}
	}
}