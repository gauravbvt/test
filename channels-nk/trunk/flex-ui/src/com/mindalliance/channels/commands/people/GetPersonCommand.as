// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.commands.people
{
	import com.adobe.cairngorm.control.CairngormEvent;
	import com.mindalliance.channels.business.people.PersonDelegate;
	import com.mindalliance.channels.commands.common.GetElementCommand;
	import com.mindalliance.channels.events.people.*;
	
	public class GetPersonCommand extends GetElementCommand
	{
	
		public function GetPersonCommand() {
		  super(PersonDelegate);	
		}
	}
}