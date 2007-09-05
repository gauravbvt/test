
package com.mindalliance.channels.commands.application
{
	import com.adobe.cairngorm.control.CairngormEvent;
	import com.mindalliance.channels.business.application.ScenarioDelegate;
	import com.mindalliance.channels.commands.BaseDelegateCommand;
	import com.mindalliance.channels.events.application.GetScenarioEvent;
	import com.mindalliance.channels.model.application.ProjectScenarioBrowserModel;
	import com.mindalliance.channels.vo.ScenarioVO;
	
	public class GetScenarioCommand extends BaseDelegateCommand
	{
		
		override public function execute(event:CairngormEvent):void
		{
			var evt:GetScenarioEvent = event as GetScenarioEvent;
			var delegate:ScenarioDelegate = new ScenarioDelegate( this );
			
			var id : String = evt.id;
			if (id!= null) {
				log.debug("Retrieving scenario {0}", [id]);
				delegate.getElement(id);
			} else {
				log.debug("Deselecting scenario");
				channelsModel.projectScenarioBrowserModel.selectedScenario = null;
			}
		}
		
		override public function result(data:Object):void
		{
			var result:ScenarioVO = (data["data"] as ScenarioVO);
			if (result != null) {
				channelsModel.projectScenarioBrowserModel.selectedScenario = result;
				log.debug("Setting selected scenario to {0}", [result.id]);
			} else {
				log.warn("Unable to retrieve scenario");
			}
		}
	}
}