// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.scenario.commands
{
	import com.mindalliance.channels.common.commands.GetElementCommand;
	import com.mindalliance.channels.scenario.events.*;
	import com.mindalliance.channels.util.CairngormHelper;
	import com.mindalliance.channels.vo.common.ElementVO;
	
	public class GetTaskCommand extends GetElementCommand
	{
	
		public function GetTaskCommand() {
		  super();	
		}
		
		override public function result(data : Object) : void
		{
			super.result(data);
			if (data != null) {
				
                var result:ElementVO = (data["data"] as ElementVO);
                CairngormHelper.fireEvent(new GetAgentListEvent(result.id));
                CairngormHelper.fireEvent(new GetArtifactListByTaskEvent(result.id));
                CairngormHelper.fireEvent(new GetAcquirementListByTaskEvent(result.id));
			}
		}
	}
}