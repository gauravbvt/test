// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.commands.scenario
{
	import com.adobe.cairngorm.control.CairngormEvent;
	import com.adobe.cairngorm.control.CairngormEventDispatcher;
	import com.mindalliance.channels.business.scenario.TaskDelegate;
	import com.mindalliance.channels.events.scenario.*;
	import com.mindalliance.channels.commands.BaseDelegateCommand;
	
	public class DeleteTaskCommand extends BaseDelegateCommand
	{
	
		override public function execute(event:CairngormEvent):void
		{
			var evt:DeleteTaskEvent = event as DeleteTaskEvent;
			var delegate:TaskDelegate = new TaskDelegate( this );
		}
		
		override public function result(data:Object):void
		{
			
		}
	}
}