// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.commands.people
{
	import com.mindalliance.channels.business.people.RoleDelegate;
	import com.mindalliance.channels.commands.common.GetElementCommand;
	import com.mindalliance.channels.events.people.*;
	
	public class GetRoleCommand extends GetElementCommand
	{
	
		public function GetRoleCommand() {
			super(RoleDelegate);
			
		}
	}
}