// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.people.commands
{
	import com.adobe.cairngorm.control.CairngormEvent;
	import com.mindalliance.channels.common.business.GetElementDelegate;
	import com.mindalliance.channels.common.commands.BaseDelegateCommand;
	import com.mindalliance.channels.common.events.GetElementEvent;
	import com.mindalliance.channels.people.events.*;
	import com.mindalliance.channels.util.CairngormHelper;
	
	import mx.collections.ArrayCollection;
	
	public class GetPersonByUserCommand extends BaseDelegateCommand
	{
        override public function execute(event:CairngormEvent):void
        {
            var evt:GetPersonByUserEvent = event as GetPersonByUserEvent;
            var delegate:GetElementDelegate = new GetElementDelegate( this );
            log.debug("Retrieving Organization list");
            delegate.getPersonByUser(evt.userId);
        }
        
        override public function result(data:Object):void
        {
        	
        	var list : ArrayCollection = (data["data"] as ArrayCollection);
        	if (list.length > 0) {
        	   channelsModel.personalProfileEditorModel.personEditorModel.id = list.getItemAt(0).id;
        	   CairngormHelper.fireEvent(new GetElementEvent(list.getItemAt(0).id));	
        	}
            log.debug("Successfully retrieved Organization list");
        }
        
        override public function fault(info:Object):void
        {
            channelsModel.personalProfileEditorModel.personEditorModel.id = null;
            super.fault(info);
        }
	}
}