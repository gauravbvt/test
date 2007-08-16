
package com.mindalliance.channels.commands.application
{
	import com.adobe.cairngorm.commands.ICommand;
	import com.adobe.cairngorm.control.CairngormEvent;
	import com.mindalliance.channels.events.DeleteProjectEvent;
	import com.mindalliance.channels.business.application.ProjectDelegate;
	import com.mindalliance.channels.model.ChannelsModelLocator;
	import com.mindalliance.channels.model.application.ProjectScenarioBrowserModel;
	
	public class DeleteProjectCommand implements ICommand
	{
		private var model : ProjectScenarioBrowserModel = ChannelsModelLocator.getInstance().projectScenarioBrowserModel;
		
		public function execute(event:CairngormEvent):void
		{
			var evt:DeleteProjectEvent = event as DeleteProjectEvent;
			var id : String = evt.id;
			
			var delegate:ProjectDelegate = new ProjectDelegate( this );
			delegate.deleteProject(id);
		}
		
		public function result(data:Object):void
		{
			var result:Object = (data as ResultEvent).result;
			if (result.project != null) {
 	        	CairngormEventDispatcher.getInstance().dispatchEvent( new GetProjectListEvent() );
			}
		}
		
		public function fault(info:Object):void
		{
			var fault:FaultEvent = info as FaultEvent;
		}
	}
}