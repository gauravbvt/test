// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.commands.scenario
{
	import com.adobe.cairngorm.control.CairngormEvent;
	import com.mindalliance.channels.business.scenario.AcquirementDelegate;
	import com.mindalliance.channels.commands.common.GetElementCommand;
	import com.mindalliance.channels.events.scenario.*;
	
	public class GetAcquirementCommand extends GetElementCommand
	{
	   public function GetAcquirementCommand() {
		  super(AcquirementDelegate);
	   }
	}
}