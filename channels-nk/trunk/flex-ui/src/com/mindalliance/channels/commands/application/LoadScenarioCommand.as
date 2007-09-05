
package com.mindalliance.channels.commands.application
{
	import com.adobe.cairngorm.control.CairngormEvent;
	import com.mindalliance.channels.commands.BaseCommand;
	import com.mindalliance.channels.events.application.LoadScenarioEvent;
	import com.mindalliance.channels.events.scenario.GetTaskListEvent;
	import com.mindalliance.channels.util.CairngormHelper;
	
	public class LoadScenarioCommand extends BaseCommand
	{
		override public function execute(event:CairngormEvent):void
		{
			var evt:LoadScenarioEvent = event as LoadScenarioEvent;
			
			channelsModel.currentProject = channelsModel.projectScenarioBrowserModel.selectedProject;
			channelsModel.currentScenario = channelsModel.projectScenarioBrowserModel.selectedScenario;	
			
			CairngormHelper.fireEvent(new GetTaskListEvent(evt.id));
			log.debug("Loaded scenario {0}", [evt.id]);
		}
	}
}