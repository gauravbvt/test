
package com.mindalliance.channels.commands.application
{
	import com.adobe.cairngorm.commands.ICommand;
	import com.adobe.cairngorm.control.CairngormEvent;
	import com.mindalliance.channels.business.application.ProjectDelegate;
	import com.mindalliance.channels.events.application.GetProjectEvent;
	import com.mindalliance.channels.model.ChannelsModelLocator;
	import com.mindalliance.channels.model.application.ProjectScenarioBrowserModel;
	import com.mindalliance.channels.vo.ProjectVO;
	
	import mx.rpc.IResponder;
	import mx.rpc.events.FaultEvent;
	import mx.rpc.events.ResultEvent;	
	import mx.logging.Log;
	import mx.logging.ILogger;
	
	public class GetProjectCommand implements ICommand, IResponder
	{
		
		private var model : ProjectScenarioBrowserModel = ChannelsModelLocator.getInstance().projectScenarioBrowserModel;
	
		private var log : ILogger = Log.getLogger("com.mindalliance.channels.commands.application.GetProjectCommand");
		public function execute(event:CairngormEvent):void
		{
			var evt:GetProjectEvent = event as GetProjectEvent;			
			var id : String = evt.id;
			
			if (id != null) {
				log.debug("Retrieving project {0}", [id]);
				var delegate:ProjectDelegate = new ProjectDelegate( this );
				delegate.getElement(id);
			} else {
				log.debug("Setting selected Project to null");
				model.selectedProject = null;
			}
			
		}
		
		public function result(data:Object):void
		{
			var result:Object = (data as ResultEvent).result.project;
			if (result != null) {
				log.debug("Setting selected project to {0}", [result.id]);
				model.selectedProject = new ProjectVO(result.id, result.name, result.description, result.manager);
			} else {
				log.warn("Unable to retrieve project");	
			}
		}
		
		public function fault(info:Object):void
		{
			var fault:FaultEvent = info as FaultEvent;
			log.error(fault.toString());
		}
	}
}