
package com.mindalliance.channels.commands.application
{
	import com.adobe.cairngorm.commands.ICommand;
	import com.adobe.cairngorm.control.CairngormEvent;
	import com.mindalliance.channels.events.application.CreateScenarioEvent;

	public class CreateScenarioCommand implements ICommand
	{
		public function execute(event:CairngormEvent):void
		{
			var evt:CreateScenarioEvent = event as CreateScenarioEvent;
		}
	}
}