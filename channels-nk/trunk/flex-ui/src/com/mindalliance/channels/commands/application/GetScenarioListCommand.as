
package com.mindalliance.channels.commands.application
{
	import com.adobe.cairngorm.control.CairngormEvent;
	import com.adobe.cairngorm.control.CairngormEventDispatcher;
	import com.mindalliance.channels.business.application.ScenarioDelegate;
	import com.mindalliance.channels.commands.BaseDelegateCommand;
	import com.mindalliance.channels.events.application.GetScenarioEvent;
	import com.mindalliance.channels.events.application.GetScenarioListEvent;
	import com.mindalliance.channels.model.ChannelsModelLocator;
	import com.mindalliance.channels.model.application.ProjectScenarioBrowserModel;
	
	import mx.collections.ArrayCollection;
	
	public class GetScenarioListCommand extends BaseDelegateCommand
	{
		override public function execute(event:CairngormEvent):void
		{
			var evt:GetScenarioListEvent = event as GetScenarioListEvent;
			var delegate:ScenarioDelegate = new ScenarioDelegate( this );
			log.debug("Retrieving scenario list");
			if (evt.projectId != null) {
				delegate.getScenarioList(evt.projectId);
			} else {
				model.projectScenarioBrowserModel.scenarioList = null;
			}
			CairngormEventDispatcher.getInstance().dispatchEvent( new GetScenarioEvent(null) );
		}
		
		override public function result(data:Object):void
		{
			model.projectScenarioBrowserModel.scenarioList = (data as ArrayCollection);
			log.debug("Scenario list retrieved successfully");

		}
	}
}