
package com.mindalliance.channels.commands.application
{
	import com.adobe.cairngorm.commands.ICommand;
	import com.adobe.cairngorm.control.CairngormEvent;
	import com.mindalliance.channels.business.application.ScenarioDelegate;
	import com.mindalliance.channels.events.application.GetScenarioEvent;
	import com.mindalliance.channels.model.ChannelsModelLocator;
	import com.mindalliance.channels.model.application.ProjectScenarioBrowserModel;
	import mx.rpc.events.ResultEvent;
	import mx.rpc.events.FaultEvent;
	import mx.rpc.AsyncToken;
	import mx.rpc.IResponder;
	import com.mindalliance.channels.vo.ScenarioVO;	
	import mx.logging.Log;
	import mx.logging.ILogger;
	
	public class GetScenarioCommand implements ICommand, IResponder
	{
		
		private var model : ProjectScenarioBrowserModel = ChannelsModelLocator.getInstance().projectScenarioBrowserModel;
		private var log : ILogger = Log.getLogger("com.mindalliance.channels.commands.application.GetScenarioCommand");
		public function execute(event:CairngormEvent):void
		{
			var evt:GetScenarioEvent = event as GetScenarioEvent;
			var delegate:ScenarioDelegate = new ScenarioDelegate( this );
			
			var id : String = evt.id;
			if (id!= null) {
				log.debug("Retrieving scenario {0}", [id]);
				delegate.getElement(id);
			} else {
				log.debug("Deselecting scenario");
				model.selectedScenario = null;
			}
		}
		
		public function result(data:Object):void
		{
			var result:Object = (data as ResultEvent).result.scenario;
			if (result != null) {
				model.selectedScenario = ScenarioVO.fromXML(result);
				log.debug("Setting selected scenario to {0}", [result.id]);
			} else {
				log.warn("Unable to retrieve scenario");
			}
		}
		
		public function fault(info:Object):void
		{
			var fault:FaultEvent = info as FaultEvent;
			log.error(fault.toString());
		}
	}
}