// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.commands.scenario
{
	import com.mindalliance.channels.business.scenario.TaskDelegate;
	import com.mindalliance.channels.commands.common.GetElementCommand;
	import com.mindalliance.channels.events.scenario.*;
	
	public class GetTaskCommand extends GetElementCommand
	{
	
		public function GetTaskCommand() {
		  super(TaskDelegate);	
		}
	}
}