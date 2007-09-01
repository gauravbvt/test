
package com.mindalliance.channels.commands.application
{
	import com.adobe.cairngorm.control.CairngormEvent;
	import com.mindalliance.channels.business.application.ScenarioDelegate;
	import com.mindalliance.channels.commands.BaseDelegateCommand;
	import com.mindalliance.channels.events.application.UpdateScenarioEvent;
	import com.mindalliance.channels.util.ElementHelper;
	import com.mindalliance.channels.vo.common.ElementVO;
	
	public class UpdateScenarioCommand extends BaseDelegateCommand
	{
		
		override public function execute(event:CairngormEvent):void
		{
			if (channelsModel.projectScenarioBrowserModel.shouldUpdateScenario) {
				log.debug("Updating Scenario");
				var evt:UpdateScenarioEvent = event as UpdateScenarioEvent;
				
				var delegate:ScenarioDelegate = new ScenarioDelegate( this );
				channelsModel.projectScenarioBrowserModel.selectedScenario = evt.scenario;
				
				delegate.updateElement(channelsModel.projectScenarioBrowserModel.selectedScenario);
			}
		}
		
		override public function result(data:Object):void
		{
			log.debug("Scenario successfully updated");
			
			var obj : ElementVO = ElementHelper.findElementById(
												channelsModel.projectScenarioBrowserModel.selectedScenario.id, 
												channelsModel.projectScenarioBrowserModel.scenarioList
											);
												
			if (obj.name != channelsModel.projectScenarioBrowserModel.selectedScenario.name) {
				obj.name = channelsModel.projectScenarioBrowserModel.selectedScenario.name;	
			}
			channelsModel.projectScenarioBrowserModel.shouldUpdateScenario = false;
			
		}

	}
}