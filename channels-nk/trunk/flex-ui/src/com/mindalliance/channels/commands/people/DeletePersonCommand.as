// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.commands.people
{
	import com.adobe.cairngorm.control.CairngormEvent;
	import com.adobe.cairngorm.control.CairngormEventDispatcher;
	import com.mindalliance.channels.business.people.PersonDelegate;
	import com.mindalliance.channels.events.people.*;
	import com.mindalliance.channels.commands.BaseDelegateCommand;
	
	public class DeletePersonCommand extends BaseDelegateCommand
	{
	
		override public function execute(event:CairngormEvent):void
		{
			var evt:DeletePersonEvent = event as DeletePersonEvent;
			var delegate:PersonDelegate = new PersonDelegate( this );
			delegate.deleteElement(evt.id);
		}
		
		override public function result(data:Object):void
		{
			var result:Boolean = data["data"] as Boolean;
            if (result == true) {
                CairngormEventDispatcher.getInstance().dispatchEvent( new GetRoleListEvent() );
                log.info("Role successfully deleted");
            } else {
                log.warn("Role Deletion failed");   
            }
		}
	}
}