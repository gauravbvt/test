
package com.mindalliance.channels.commands
{
	import com.adobe.cairngorm.commands.ICommand;
	import com.adobe.cairngorm.control.CairngormEvent;
	import com.mindalliance.channels.events.UpdateScenarioEvent;

	public class UpdateScenarioCommand implements ICommand
	{
		public function execute(event:CairngormEvent):void
		{
			var evt:UpdateScenarioEvent = event as UpdateScenarioEvent;
		}
	}
}