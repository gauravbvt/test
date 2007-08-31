
package com.mindalliance.channels.commands.application
{
	import com.adobe.cairngorm.commands.ICommand;
	import com.adobe.cairngorm.control.CairngormEvent;
	import com.mindalliance.channels.events.application.LoadScenarioEvent;
	import com.mindalliance.channels.model.ChannelsModelLocator;	
	import mx.logging.Log;
	import mx.logging.ILogger;
	import com.mindalliance.channels.commands.BaseCommand;
	
	public class LoadScenarioCommand extends BaseCommand
	{
		override public function execute(event:CairngormEvent):void
		{
			var evt:LoadScenarioEvent = event as LoadScenarioEvent;
			
			channelsModel.currentProject = channelsModel.projectScenarioBrowserModel.selectedProject;
			channelsModel.currentScenario = channelsModel.projectScenarioBrowserModel.selectedScenario;	
			log.debug("Loaded scenario {0}", [channelsModel.currentScenario.id]);
		}
	}
}