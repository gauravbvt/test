// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.commands.people
{
	import com.adobe.cairngorm.control.CairngormEvent;
	import com.mindalliance.channels.business.people.PersonDelegate;
	import com.mindalliance.channels.commands.BaseDelegateCommand;
	import com.mindalliance.channels.events.people.*;
	import com.mindalliance.channels.model.ChannelsModelLocator;
	import com.mindalliance.channels.util.CairngormHelper;
	
	import mx.collections.ArrayCollection;
	
	public class GetPersonByUserCommand extends BaseDelegateCommand
	{
        override public function execute(event:CairngormEvent):void
        {
            var evt:GetPersonByUserEvent = event as GetPersonByUserEvent;
            var delegate:PersonDelegate = new PersonDelegate( this );
            log.debug("Retrieving Organization list");
            delegate.getPersonByUser(evt.userId);
        }
        
        override public function result(data:Object):void
        {
        	
        	var list : ArrayCollection = (data["data"] as ArrayCollection);
        	if (list.length > 0) {
        	   channelsModel.personId = list.getItemAt(0).id;
        	   CairngormHelper.fireEvent(new GetPersonEvent(list.getItemAt(0).id));	
        	}
            log.debug("Successfully retrieved Organization list");
        }
        
        override public function fault(info:Object):void
        {
            channelsModel.personId = null;
            super.fault(info);
        }
	}
}