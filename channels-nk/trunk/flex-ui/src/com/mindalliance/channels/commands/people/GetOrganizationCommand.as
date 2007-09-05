// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.commands.people
{
	import com.mindalliance.channels.business.people.OrganizationDelegate;
	import com.mindalliance.channels.commands.common.GetElementCommand;
		
	public class GetOrganizationCommand extends GetElementCommand
	{
	
		public function GetOrganizationCommand() {
		  super (OrganizationDelegate);	
		}
	}
}