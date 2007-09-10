// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.commands.resources
{
	import com.adobe.cairngorm.control.CairngormEvent;
	import com.adobe.cairngorm.control.CairngormEventDispatcher;
	import com.mindalliance.channels.business.resources.RepositoryDelegate;
	import com.mindalliance.channels.events.resources.*;
	import com.mindalliance.channels.commands.BaseDelegateCommand;
	
	public class GetRepositoryCommand extends BaseDelegateCommand
	{
	
		override public function execute(event:CairngormEvent):void
		{
			var evt:GetRepositoryEvent = event as GetRepositoryEvent;
			var delegate:RepositoryDelegate = new RepositoryDelegate( this );
		}
		
		override public function result(data:Object):void
		{
			
		}
	}
}