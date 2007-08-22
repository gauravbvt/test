
package com.mindalliance.channels.commands.application
{
	import com.adobe.cairngorm.commands.ICommand;
	import com.adobe.cairngorm.control.CairngormEvent;
	import com.mindalliance.channels.events.application.UpdateScenarioEvent;	
	import mx.logging.Log;
	import mx.logging.ILogger;

	public class UpdateScenarioCommand implements ICommand
	{
		private var log : ILogger = Log.getLogger("com.mindalliance.channels.commands.application.UpdateScenarioCommand");
		public function execute(event:CairngormEvent):void
		{
			var evt:UpdateScenarioEvent = event as UpdateScenarioEvent;
		}
	}
}