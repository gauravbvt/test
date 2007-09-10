// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.commands.scenario
{
	import com.adobe.cairngorm.control.CairngormEvent;
	import com.mindalliance.channels.business.scenario.EventDelegate;
	import com.mindalliance.channels.commands.common.GetElementCommand;
	import com.mindalliance.channels.events.scenario.*;
	
	public class GetEventCommand extends GetElementCommand
	{
	
		public function GetEventCommand() {
		  super(EventDelegate);	
		}
	}
}