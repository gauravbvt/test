
package com.mindalliance.channels.commands.application
{
	import com.adobe.cairngorm.commands.ICommand;
	import com.adobe.cairngorm.control.CairngormEvent;
	import com.adobe.cairngorm.control.CairngormEventDispatcher;
	import com.mindalliance.channels.business.application.ProjectDelegate;
	import com.mindalliance.channels.events.application.UpdateProjectEvent;
	import com.mindalliance.channels.model.ChannelsModelLocator;
	import com.mindalliance.channels.model.application.ProjectScenarioBrowserModel;
	import com.mindalliance.channels.vo.ProjectVO;
	import com.mindalliance.channels.util.ElementHelper;
	import mx.rpc.IResponder;
	import mx.rpc.events.FaultEvent;
	import mx.rpc.events.ResultEvent;	
	import mx.logging.Log;
	import mx.logging.ILogger;
	import mx.collections.ArrayCollection;
	
	public class UpdateProjectCommand implements ICommand, IResponder
	{
		
		private var model : ProjectScenarioBrowserModel = ChannelsModelLocator.getInstance().projectScenarioBrowserModel;
		private var log : ILogger = Log.getLogger("com.mindalliance.channels.commands.application.UpdateProjectCommand");
		
		public function execute(event:CairngormEvent):void
		{
			log.debug("Updating project");
			var evt:UpdateProjectEvent = event as UpdateProjectEvent;
			
			var delegate:ProjectDelegate = new ProjectDelegate( this );
			model.selectedProject.name = evt.name;
			model.selectedProject.description = evt.description;
			
			delegate.updateElement(model.selectedProject.id, model.selectedProject.toXML());
		}
		
		public function result(data:Object):void
		{
			log.debug("Project successfully updated");
			var result:Object = (data as ResultEvent).result;
			
			var obj : Object = ElementHelper.findElementById(model.selectedProject.id, model.projectList);
			if (obj.name != model.selectedProject.name) {
				obj.name = model.selectedProject.name;	
			}
			
		}
		
		

		public function fault(info:Object):void
		{
			var fault:FaultEvent = info as FaultEvent;
			log.error(fault.toString());
		}
		
	}
}