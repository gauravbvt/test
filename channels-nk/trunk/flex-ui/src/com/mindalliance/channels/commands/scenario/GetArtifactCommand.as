// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.commands.scenario
{
	import com.adobe.cairngorm.control.CairngormEvent;
	import com.mindalliance.channels.business.scenario.ArtifactDelegate;
	import com.mindalliance.channels.commands.common.GetElementCommand;
	import com.mindalliance.channels.events.scenario.*;
	
	public class GetArtifactCommand extends GetElementCommand
	{
	   public function GetArtifactCommand() {
		  super(ArtifactDelegate);
	   }
	}
}