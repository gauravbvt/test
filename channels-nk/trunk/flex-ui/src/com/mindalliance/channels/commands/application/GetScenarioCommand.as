
package com.mindalliance.channels.commands.application
{
	import com.adobe.cairngorm.commands.ICommand;
	import com.adobe.cairngorm.control.CairngormEvent;
	import com.mindalliance.channels.business.application.GetScenarioDelegate;
	import com.mindalliance.channels.events.application.GetScenarioEvent;
	import com.mindalliance.channels.model.ChannelsModelLocator;
	import com.mindalliance.channels.model.application.ProjectScenarioBrowserModel;
	import mx.rpc.events.ResultEvent;
	import mx.rpc.events.FaultEvent;
	import mx.rpc.AsyncToken;
	import mx.rpc.IResponder;
	import com.mindalliance.channels.vo.ScenarioVO;
	
	public class GetScenarioCommand implements ICommand, IResponder
	{
		
		private var model : ProjectScenarioBrowserModel = ChannelsModelLocator.getInstance().projectScenarioBrowserModel;
		public function execute(event:CairngormEvent):void
		{
			var evt:GetScenarioEvent = event as GetScenarioEvent;
			var delegate:GetScenarioDelegate = new GetScenarioDelegate( this );
			
			model.selectedScenarioId = evt.id;
			if (model.selectedScenarioId != null) {
				delegate.getScenario(model.selectedScenarioId);
			} else {
				model.selectedScenario = null;
			}
		}
		
		public function result(data:Object):void
		{
			var result:Object = (data as ResultEvent).result.scenario;
			model.selectedScenario = new ScenarioVO(result.id, result.name, result.projectId, result.description);
		}
		
		public function fault(info:Object):void
		{
			var fault:FaultEvent = info as FaultEvent;
		}
	}
}