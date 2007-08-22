
package com.mindalliance.channels.commands.application
{
	import com.adobe.cairngorm.commands.ICommand;
	import com.adobe.cairngorm.control.CairngormEvent;
	import com.adobe.cairngorm.control.CairngormEventDispatcher;
	import com.mindalliance.channels.business.application.ProjectDelegate;
	import com.mindalliance.channels.events.application.GetProjectEvent;
	import com.mindalliance.channels.events.application.GetProjectListEvent;
	import com.mindalliance.channels.model.ChannelsModelLocator;
	import com.mindalliance.channels.model.application.ProjectScenarioBrowserModel;
	import com.mindalliance.channels.util.XMLHelper;
	
	import mx.rpc.IResponder;
	import mx.rpc.events.FaultEvent;
	import mx.rpc.events.ResultEvent;	
	import mx.logging.Log;
	import mx.logging.ILogger;
	
	public class GetProjectListCommand implements ICommand, IResponder
	{
		private var model : ProjectScenarioBrowserModel = ChannelsModelLocator.getInstance().projectScenarioBrowserModel;
		
		private var log : ILogger = Log.getLogger("com.mindalliance.channels.commands.application.GetProjectListCommand");
		public function execute(event:CairngormEvent):void
		{
			var evt:GetProjectListEvent = event as GetProjectListEvent;
			var delegate:ProjectDelegate = new ProjectDelegate( this );
			log.debug("Retrieving project list");
			delegate.getProjectList();
		}
		
		public function result(data:Object):void
		{
			var result:Object = (data as ResultEvent).result;
			model.projectList = XMLHelper.fromXMLList("project", result);
			log.debug("Successfully retrieved project list");
        	CairngormEventDispatcher.getInstance().dispatchEvent( new GetProjectEvent(null) );
		}
		
		public function fault(info:Object):void
		{
			var fault:FaultEvent = info as FaultEvent;
			log.error(fault.toString());
		}
		
	}
}