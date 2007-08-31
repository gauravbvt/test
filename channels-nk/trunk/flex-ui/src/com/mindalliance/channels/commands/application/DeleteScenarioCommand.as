
package com.mindalliance.channels.commands.application
{
	import com.adobe.cairngorm.control.CairngormEvent;
	import com.adobe.cairngorm.control.CairngormEventDispatcher;
	import com.mindalliance.channels.business.application.ScenarioDelegate;
	import com.mindalliance.channels.commands.BaseDelegateCommand;
	import com.mindalliance.channels.events.application.DeleteScenarioEvent;
	import com.mindalliance.channels.events.application.GetScenarioListEvent;
	import com.mindalliance.channels.model.application.ProjectScenarioBrowserModel;
	
	public class DeleteScenarioCommand extends BaseDelegateCommand
	{
		
		override public function execute(event:CairngormEvent):void
		{
			var evt:DeleteScenarioEvent = event as DeleteScenarioEvent;
			var id : String = evt.id;
			
			log.debug("Deleting scenario...");
			
			var delegate:ScenarioDelegate = new ScenarioDelegate( this );
			delegate.deleteElement(id);
		}
		
		override public function result(data:Object):void
		{
			var result:Boolean = (data as Boolean);
			if (result == true) {
 	        	CairngormEventDispatcher.getInstance().dispatchEvent( new GetScenarioListEvent(channelsModel.projectScenarioBrowserModel.selectedProject.id) );
 	        	log.info("Scenario successfully deleted");
			} else {
				log.warn("Scenario deletion failed");	
			}
		}
	}
}