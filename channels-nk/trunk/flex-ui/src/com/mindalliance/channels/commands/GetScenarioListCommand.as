
package com.mindalliance.channels.commands
{
	import com.adobe.cairngorm.commands.ICommand;
	import com.adobe.cairngorm.control.CairngormEvent;
	import com.mindalliance.UtilFuncs;
	import com.mindalliance.channels.business.GetScenarioListDelegate;
	import com.mindalliance.channels.events.GetScenarioListEvent;
	import com.mindalliance.channels.model.ChannelsModelLocator;
	import com.mindalliance.channels.model.ProjectScenarioBrowserModel;
	
	import mx.rpc.IResponder;
	import mx.rpc.events.FaultEvent;
	import mx.rpc.events.ResultEvent;
	
	public class GetScenarioListCommand implements ICommand, IResponder
	{
		private var model : ProjectScenarioBrowserModel = ChannelsModelLocator.getInstance().projectScenarioBrowserModel;
		
		public function execute(event:CairngormEvent):void
		{
			var evt:GetScenarioListEvent = event as GetScenarioListEvent;
			var delegate:GetScenarioListDelegate = new GetScenarioListDelegate( this );
			if (evt.projectId != null) {
				delegate.getScenarioList(evt.projectId);
			} else {
				model.scenarioList = null;
			}
			model.selectedScenario = null;
		}
		
		public function result(data:Object):void
		{
			var result:ResultEvent = data as ResultEvent;
			model.scenarioList = UtilFuncs.convertServiceResults(result.result.scenarios.scenario);
		}
		
		public function fault(info:Object):void
		{
			var fault:FaultEvent = info as FaultEvent;
		}
	}
}