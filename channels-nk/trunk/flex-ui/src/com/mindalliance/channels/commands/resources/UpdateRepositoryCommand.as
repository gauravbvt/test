// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.commands.resources
{
	import com.adobe.cairngorm.control.CairngormEvent;
	import com.adobe.cairngorm.control.CairngormEventDispatcher;
	import com.mindalliance.channels.business.resources.RepositoryDelegate;
	import com.mindalliance.channels.events.resources.*;
	import com.mindalliance.channels.commands.BaseDelegateCommand;
	
	public class UpdateRepositoryCommand extends BaseDelegateCommand
	{
	
		override public function execute(event:CairngormEvent):void
		{
			var evt:UpdateRepositoryEvent = event as UpdateRepositoryEvent;
			var delegate:RepositoryDelegate = new RepositoryDelegate( this );
		}
		
		override public function result(data:Object):void
		{
			
		}
	}
}