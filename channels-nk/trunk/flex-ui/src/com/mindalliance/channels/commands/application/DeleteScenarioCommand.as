
package com.mindalliance.channels.commands.application
{
	import com.adobe.cairngorm.commands.ICommand;
	import com.adobe.cairngorm.control.CairngormEvent;
	import com.mindalliance.channels.events.DeleteScenarioEvent;

	public class DeleteScenarioCommand implements ICommand
	{
		public function execute(event:CairngormEvent):void
		{
			var evt:DeleteScenarioEvent = event as DeleteScenarioEvent;
		}
	}
}