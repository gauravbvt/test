
package com.mindalliance.channels.commands.application
{
	import com.adobe.cairngorm.commands.ICommand;
	import com.adobe.cairngorm.control.CairngormEvent;
	import com.adobe.cairngorm.control.CairngormEventDispatcher;
	import com.mindalliance.channels.events.application.DeleteProjectEvent;
	import com.mindalliance.channels.business.application.ProjectDelegate;
	import com.mindalliance.channels.model.ChannelsModelLocator;
	import com.mindalliance.channels.events.application.GetProjectListEvent;
	import com.mindalliance.channels.model.application.ProjectScenarioBrowserModel;
	
	import mx.rpc.IResponder;
	import mx.rpc.events.FaultEvent;
	import mx.rpc.events.ResultEvent;
	public class DeleteProjectCommand implements ICommand,IResponder
	{
		private var model : ProjectScenarioBrowserModel = ChannelsModelLocator.getInstance().projectScenarioBrowserModel;
		
		public function execute(event:CairngormEvent):void
		{
			var evt:DeleteProjectEvent = event as DeleteProjectEvent;
			var id : String = evt.id;
			
			var delegate:ProjectDelegate = new ProjectDelegate( this );
			delegate.deleteElement(id);
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