
package com.mindalliance.channels.commands
{
	import com.adobe.cairngorm.commands.ICommand;
	import com.adobe.cairngorm.control.CairngormEvent;
	import com.mindalliance.channels.events.DeleteProjectEvent;

	public class DeleteProjectCommand implements ICommand
	{
		public function execute(event:CairngormEvent):void
		{
			var evt:DeleteProjectEvent = event as DeleteProjectEvent;
		}
	}
}