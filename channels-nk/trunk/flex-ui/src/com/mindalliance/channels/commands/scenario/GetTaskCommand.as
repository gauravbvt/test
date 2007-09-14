// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.commands.scenario
{
	import com.mindalliance.channels.business.scenario.TaskDelegate;
	import com.mindalliance.channels.commands.common.GetElementCommand;
	import com.mindalliance.channels.events.scenario.*;
	import com.mindalliance.channels.util.CairngormHelper;
	import com.mindalliance.channels.vo.common.ElementVO;
	
	public class GetTaskCommand extends GetElementCommand
	{
	
		public function GetTaskCommand() {
		  super(TaskDelegate);	
		}
		
		override public function result(data : Object) : void
		{
			super.result(data);
			if (data != null) {
				CairngormHelper.fireEvent(new GetAgentListEvent(data["id"]));
			}
		}
	}
}