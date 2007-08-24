
package com.mindalliance.channels.commands.application
{
	import com.adobe.cairngorm.commands.ICommand;
	import com.adobe.cairngorm.control.CairngormEvent;
	import com.mindalliance.channels.business.application.ScenarioDelegate;
	import com.mindalliance.channels.events.application.UpdateScenarioEvent;
	import com.mindalliance.channels.model.ChannelsModelLocator;
	import com.mindalliance.channels.model.application.ProjectScenarioBrowserModel;
	import com.mindalliance.channels.util.ElementHelper;
	
	import mx.logging.ILogger;
	import mx.logging.Log;
	import mx.rpc.IResponder;
	import mx.rpc.events.FaultEvent;
	import mx.rpc.events.ResultEvent;
	
	public class UpdateScenarioCommand implements ICommand, IResponder
	{
		
		private var model : ProjectScenarioBrowserModel = ChannelsModelLocator.getInstance().projectScenarioBrowserModel;
		private var log : ILogger = Log.getLogger("com.mindalliance.channels.commands.application.UpdateScenarioCommand");
		
		public function execute(event:CairngormEvent):void
		{
			if (model.shouldUpdateScenario) {
				log.debug("Updating Scenario");
				var evt:UpdateScenarioEvent = event as UpdateScenarioEvent;
				
				var delegate:ScenarioDelegate = new ScenarioDelegate( this );
				model.selectedScenario = evt.scenario;
				
				delegate.updateElement(model.selectedScenario.id, model.selectedScenario.toXML());
			}
		}
		
		public function result(data:Object):void
		{
			log.debug("Scenario successfully updated");
			var result:Object = (data as ResultEvent).result;
			
			var obj : Object = ElementHelper.findElementById(model.selectedScenario.id, model.scenarioList);
			if (obj.name != model.selectedScenario.name) {
				obj.name = model.selectedScenario.name;	
			}
			model.shouldUpdateScenario = false;
			
		}
		
		

		public function fault(info:Object):void
		{
			var fault:FaultEvent = info as FaultEvent;
			log.error(fault.toString());
		}
		
	}
}