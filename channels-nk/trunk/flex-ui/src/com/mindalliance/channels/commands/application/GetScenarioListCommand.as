
package com.mindalliance.channels.commands.application
{
	import com.adobe.cairngorm.commands.ICommand;
	import com.adobe.cairngorm.control.CairngormEvent;
	import com.mindalliance.channels.util.ServiceUtil;
	import com.mindalliance.channels.business.application.ScenarioDelegate;
	import com.mindalliance.channels.events.application.GetScenarioListEvent;
	import com.mindalliance.channels.model.ChannelsModelLocator;
	import com.mindalliance.channels.model.application.ProjectScenarioBrowserModel;
	
	import mx.rpc.IResponder;
	import mx.rpc.events.FaultEvent;
	import mx.rpc.events.ResultEvent;
	
	public class GetScenarioListCommand implements ICommand, IResponder
	{
		private var model : ProjectScenarioBrowserModel = ChannelsModelLocator.getInstance().projectScenarioBrowserModel;
		
		public function execute(event:CairngormEvent):void
		{
			var evt:GetScenarioListEvent = event as GetScenarioListEvent;
			var delegate:ScenarioDelegate = new ScenarioDelegate( this );
			if (evt.projectId != null) {
				delegate.getScenarioList(evt.projectId);
			} else {
				model.scenarioList = null;
			}
			model.selectedScenario = null;
		}
		
		public function result(data:Object):void
		{
			var result:Object = (data as ResultEvent).result;
			model.scenarioList = ServiceUtil.convertServiceResults(result.scenarios.scenario);
		}
		
		public function fault(info:Object):void
		{
			var fault:FaultEvent = info as FaultEvent;
		}
	}
}