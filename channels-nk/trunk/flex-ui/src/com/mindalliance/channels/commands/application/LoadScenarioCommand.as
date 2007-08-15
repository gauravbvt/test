
package com.mindalliance.channels.commands.application
{
	import com.adobe.cairngorm.commands.ICommand;
	import com.adobe.cairngorm.control.CairngormEvent;
	import com.mindalliance.channels.events.application.LoadScenarioEvent;
	import com.mindalliance.channels.model.ChannelsModelLocator;
	public class LoadScenarioCommand implements ICommand
	{
		private var model : ChannelsModelLocator = ChannelsModelLocator.getInstance();
		
		public function execute(event:CairngormEvent):void
		{
			var evt:LoadScenarioEvent = event as LoadScenarioEvent;
			
			model.currentProject = model.projectScenarioBrowserModel.selectedProject;
			model.currentScenario = model.projectScenarioBrowserModel.selectedScenario;	
			
		}
	}
}