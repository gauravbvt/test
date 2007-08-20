
package com.mindalliance.channels.commands.application
{
	import com.adobe.cairngorm.commands.ICommand;
	import com.adobe.cairngorm.control.CairngormEvent;
	import com.adobe.cairngorm.control.CairngormEventDispatcher;
	import com.mindalliance.channels.business.application.ScenarioDelegate;
	import com.mindalliance.channels.events.application.CreateScenarioEvent;
	import com.mindalliance.channels.events.application.GetScenarioListEvent;
	import com.mindalliance.channels.model.ChannelsModelLocator;
	import com.mindalliance.channels.model.application.ProjectScenarioBrowserModel;
	
	import mx.rpc.IResponder;
	import mx.rpc.events.FaultEvent;
	import mx.rpc.events.ResultEvent;

	public class CreateScenarioCommand implements ICommand, IResponder
	{
		private var model : ProjectScenarioBrowserModel = ChannelsModelLocator.getInstance().projectScenarioBrowserModel;
		
		public function execute(event:CairngormEvent):void
		{
			var evt:CreateScenarioEvent = event as CreateScenarioEvent;
			var name : String = evt.name;
			
			var delegate:ScenarioDelegate = new ScenarioDelegate( this );
			delegate.createScenario(name, model.selectedProject.id);
		}
		
		public function result(data:Object):void
		{
			var result:Object = (data as ResultEvent).result;
			if (result.scenario != null) {
				CairngormEventDispatcher.getInstance().dispatchEvent( new GetScenarioListEvent(model.selectedProject.id) );
			}
		}
		
		public function fault(info:Object):void
		{
			var fault:FaultEvent = info as FaultEvent;
		}
	}
}