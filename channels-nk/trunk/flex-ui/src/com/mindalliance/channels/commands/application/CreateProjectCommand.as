
package com.mindalliance.channels.commands.application
{
	import com.adobe.cairngorm.commands.ICommand;
	import com.adobe.cairngorm.control.CairngormEvent;
	import com.adobe.cairngorm.control.CairngormEventDispatcher;
	import com.mindalliance.channels.business.application.ProjectDelegate;
	import com.mindalliance.channels.events.application.CreateProjectEvent;
	import com.mindalliance.channels.events.application.GetProjectListEvent;
	import com.mindalliance.channels.model.ChannelsModelLocator;
	import com.mindalliance.channels.model.application.ProjectScenarioBrowserModel;
	
	import mx.rpc.IResponder;
	import mx.rpc.events.FaultEvent;
	import mx.rpc.events.ResultEvent;	
	import mx.logging.Log;
	import mx.logging.ILogger;
	
	public class CreateProjectCommand implements ICommand, IResponder
	{
		
		private var model : ProjectScenarioBrowserModel = ChannelsModelLocator.getInstance().projectScenarioBrowserModel;
		
		private var log : ILogger = Log.getLogger("com.mindalliance.channels.commands.application.CreateProjectCommand");
		
		public function execute(event:CairngormEvent):void
		{
			var evt:CreateProjectEvent = event as CreateProjectEvent;
			var name : String = evt.name;
			
			log.debug("Creating project " + name);
			
			var delegate:ProjectDelegate = new ProjectDelegate( this );
			delegate.createProject(name);
		}
		
		public function result(data:Object):void
		{
			var result:Object = (data as ResultEvent).result;
			if (result.project != null) {
				log.info("Project created");
				CairngormEventDispatcher.getInstance().dispatchEvent( new GetProjectListEvent() );
			}
		}
		
		public function fault(info:Object):void
		{
			var fault:FaultEvent = info as FaultEvent;
			log.error(fault.toString());
		}
		
	}
}