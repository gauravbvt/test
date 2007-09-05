// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.commands.people
{
	import com.adobe.cairngorm.control.CairngormEvent;
	import com.mindalliance.channels.business.people.PersonDelegate;
	import com.mindalliance.channels.commands.BaseDelegateCommand;
	import com.mindalliance.channels.events.people.*;
	
	import mx.collections.ArrayCollection;
	
	public class GetPersonListCommand extends BaseDelegateCommand
	{
        override public function execute(event:CairngormEvent):void
        {
            var evt:GetPersonListEvent = event as GetPersonListEvent;
            var delegate:PersonDelegate = new PersonDelegate( this );
            log.debug("Retrieving Person list");
            delegate.getPersonList();
        }
        
        override public function result(data:Object):void
        {
            channelsModel.getElementListModel("people").data = (data["data"] as ArrayCollection);
            log.debug("Successfully retrieved Person list");
        }
        
        override public function fault(info:Object):void
        {
            channelsModel.getElementListModel("people").data  = null;
            super.fault(info);
        }
	}
}