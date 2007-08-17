
package com.mindalliance.channels.commands.application
{
	import com.adobe.cairngorm.commands.ICommand;
	import com.adobe.cairngorm.control.CairngormEvent;
	import com.mindalliance.channels.events.DeleteScenarioEvent;

	public class DeleteScenarioCommand implements ICommand
	{

		private var model : ProjectScenarioBrowserModel = ChannelsModelLocator.getInstance().projectScenarioBrowserModel;
		
		public function execute(event:CairngormEvent):void
		{
			var evt:DeleteScenarioEvent = event as DeleteScenarioEvent;
			var id : String = evt.id;
			
			var delegate:ScenarioDelegate = new ScenarioDelegate( this );
			delegate.deleteElement(id);
		}
		
		public function result(data:Object):void
		{
			var result:Object = (data as ResultEvent).result;
			if (result.project != null) {
 	        	CairngormEventDispatcher.getInstance().dispatchEvent( new GetScenarioListEvent() );
			}
		}
		
		public function fault(info:Object):void
		{
			var fault:FaultEvent = info as FaultEvent;
		}
	}
}