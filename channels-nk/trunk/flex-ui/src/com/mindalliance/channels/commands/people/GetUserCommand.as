// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.commands.people
{
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
		
		override public function result(data:Object):void
        {
        	super.result(data);
        	if (data["data"] != null) {
        	   ChannelsModelLocator.getInstance().user = (data["data"] as UserVO);	
        	}
        }
 }
}