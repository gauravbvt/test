// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.commands.people
{
	import com.adobe.cairngorm.control.CairngormEvent;
	import com.mindalliance.channels.business.people.UserDelegate;
	import com.mindalliance.channels.commands.common.GetElementCommand;
	import com.mindalliance.channels.events.people.*;
	import com.mindalliance.channels.model.ChannelsModelLocator;
	import com.mindalliance.channels.vo.UserVO;
	
	public class GetUserCommand extends GetElementCommand
	{
	
	
	    public function GetUserCommand() {
			super(UserDelegate);
			
		}
		
		override public function execute(event:CairngormEvent):void
		{
		    var evt:GetUserEvent = event as GetUserEvent;  
            channelsModel.personalProfileEditorModel.userEditorModel.id=evt.id;
        
			super.execute(event);
            
		}
		
		override public function result(data:Object):void
        {
        	super.result(data);
        	channelsModel.user = (data["data"] as UserVO);	
        }
 }
}