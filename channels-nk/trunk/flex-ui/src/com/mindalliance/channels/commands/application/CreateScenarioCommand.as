
package com.mindalliance.channels.commands.application
{
	import com.adobe.cairngorm.control.CairngormEvent;
	import com.adobe.cairngorm.control.CairngormEventDispatcher;
	import com.mindalliance.channels.business.application.ScenarioDelegate;
	import com.mindalliance.channels.commands.BaseDelegateCommand;
	import com.mindalliance.channels.events.application.CreateScenarioEvent;
	import com.mindalliance.channels.events.application.GetScenarioListEvent;
	import com.mindalliance.channels.model.application.ProjectScenarioBrowserModel;
	import com.mindalliance.channels.vo.ScenarioVO;

	public class CreateScenarioCommand extends BaseDelegateCommand
	{
		
		override public function execute(event:CairngormEvent):void
		{
			var evt:CreateScenarioEvent = event as CreateScenarioEvent;
			var name : String = evt.name;
			
			log.debug("Creating scenario");
			
			var delegate:ScenarioDelegate = new ScenarioDelegate( this );
			delegate.createScenario(name, channelsModel.projectScenarioBrowserModel.selectedProject.id);
		}
		
		override public function result(data:Object):void
		{
			var result:ScenarioVO = (data as ScenarioVO);
			if (result != null) {
				CairngormEventDispatcher.getInstance().dispatchEvent( new GetScenarioListEvent(channelsModel.projectScenarioBrowserModel.selectedProject.id) );
				log.info("Scenario successfully created");
			} else {
				log.warn("Scenario creation failed");	
			}
		}

	}
}