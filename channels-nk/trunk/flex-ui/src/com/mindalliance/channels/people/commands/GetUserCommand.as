// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.people.commands
{
	import com.adobe.cairngorm.control.CairngormEvent;
	import com.mindalliance.channels.common.commands.GetElementCommand;
	import com.mindalliance.channels.people.events.*;
	import com.mindalliance.channels.model.ChannelsModelLocator;
	import com.mindalliance.channels.vo.UserVO;
	
	public class GetUserCommand extends GetElementCommand
	{
	
	
	    public function GetUserCommand() {
			super();
			
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