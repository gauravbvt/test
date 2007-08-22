
package com.mindalliance.channels.commands.application
{
	import com.adobe.cairngorm.commands.ICommand;
	import com.adobe.cairngorm.control.CairngormEvent;
	import com.adobe.cairngorm.control.CairngormEventDispatcher;
	import com.mindalliance.channels.business.application.ScenarioDelegate;
	import com.mindalliance.channels.events.application.GetScenarioEvent;
	import com.mindalliance.channels.events.application.GetScenarioListEvent;
	import com.mindalliance.channels.model.ChannelsModelLocator;
	import com.mindalliance.channels.model.application.ProjectScenarioBrowserModel;
	import com.mindalliance.channels.util.XMLHelper;
	
	import mx.rpc.IResponder;
	import mx.rpc.events.FaultEvent;
	import mx.rpc.events.ResultEvent;	
	import mx.logging.Log;
	import mx.logging.ILogger;
	
	public class GetScenarioListCommand implements ICommand, IResponder
	{
		private var model : ProjectScenarioBrowserModel = ChannelsModelLocator.getInstance().projectScenarioBrowserModel;
		private var log : ILogger = Log.getLogger("com.mindalliance.channels.commands.application.GetScenarioListCommand");
		
		public function execute(event:CairngormEvent):void
		{
			var evt:GetScenarioListEvent = event as GetScenarioListEvent;
			var delegate:ScenarioDelegate = new ScenarioDelegate( this );
			log.debug("Retrieving scenario list");
			if (evt.projectId != null) {
				delegate.getScenarioList(evt.projectId);
			} else {
				model.scenarioList = null;
			}
			CairngormEventDispatcher.getInstance().dispatchEvent( new GetScenarioEvent(null) );
		}
		
		public function result(data:Object):void
		{
			var result:Object = (data as ResultEvent).result;
			model.scenarioList = XMLHelper.fromXMLList("scenario", result);
			log.debug("Scenario list retrieved successfully");

		}
		
		public function fault(info:Object):void
		{
			var fault:FaultEvent = info as FaultEvent;
			log.error(fault.toString());
		}
	}
}