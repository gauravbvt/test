
package com.mindalliance.channels.commands.application
{
	import com.adobe.cairngorm.control.CairngormEvent;
	import com.mindalliance.channels.business.application.ScenarioDelegate;
	import com.mindalliance.channels.commands.BaseDelegateCommand;
	import com.mindalliance.channels.events.application.UpdateScenarioEvent;
	import com.mindalliance.channels.model.application.ProjectScenarioBrowserModel;
	import com.mindalliance.channels.util.ElementHelper;
	
	public class UpdateScenarioCommand extends BaseDelegateCommand
	{
		
		override public function execute(event:CairngormEvent):void
		{
			if (model.projectScenarioBrowserModel.shouldUpdateScenario) {
				log.debug("Updating Scenario");
				var evt:UpdateScenarioEvent = event as UpdateScenarioEvent;
				
				var delegate:ScenarioDelegate = new ScenarioDelegate( this );
				model.projectScenarioBrowserModel.selectedScenario = evt.scenario;
				
				delegate.updateElement(model.projectScenarioBrowserModel.selectedScenario);
			}
		}
		
		override public function result(data:Object):void
		{
			log.debug("Scenario successfully updated");
			
			var obj : Object = ElementHelper.findElementById(
												model.projectScenarioBrowserModel.selectedScenario.id, 
												model.projectScenarioBrowserModel.scenarioList
											);
												
			if (obj.name != model.projectScenarioBrowserModel.selectedScenario.name) {
				obj.name = model.projectScenarioBrowserModel.selectedScenario.name;	
			}
			model.projectScenarioBrowserModel.shouldUpdateScenario = false;
			
		}

	}
}