
package com.mindalliance.channels.commands.application
{
	import com.adobe.cairngorm.commands.ICommand;
	import com.adobe.cairngorm.control.CairngormEvent;
	import com.mindalliance.channels.events.application.UpdateProjectEvent;

	public class UpdateProjectCommand implements ICommand
	{
		public function execute(event:CairngormEvent):void
		{
			var evt:UpdateProjectEvent = event as UpdateProjectEvent;
		}
	}
}