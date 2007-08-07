
package com.mindalliance.channels.commands
{
	import com.adobe.cairngorm.commands.ICommand;
	import com.adobe.cairngorm.control.CairngormEvent;
	import com.mindalliance.channels.events.CreateProjectEvent;

	public class CreateProjectCommand implements ICommand
	{
		public function execute(event:CairngormEvent):void
		{
			var evt:CreateProjectEvent = event as CreateProjectEvent;
		}
	}
}