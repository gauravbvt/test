
package com.mindalliance.channels.commands.application
{
	import com.adobe.cairngorm.commands.ICommand;
	import com.adobe.cairngorm.control.CairngormEvent;
	import com.mindalliance.channels.business.application.GetProjectDelegate;
	import com.mindalliance.channels.events.application.GetProjectEvent;
	import com.mindalliance.channels.model.ChannelsModelLocator;
	import com.mindalliance.channels.model.application.ProjectScenarioBrowserModel;
	import com.mindalliance.channels.vo.ProjectVO;
	
	import mx.rpc.IResponder;
	import mx.rpc.events.FaultEvent;
	import mx.rpc.events.ResultEvent;
	
	public class GetProjectCommand implements ICommand, IResponder
	{
		
		private var model : ProjectScenarioBrowserModel = ChannelsModelLocator.getInstance().projectScenarioBrowserModel;
	
		public function execute(event:CairngormEvent):void
		{
			var evt:GetProjectEvent = event as GetProjectEvent;			
			var id : String = evt.id;
			model.selectedProjectId = evt.id;
			
			if (id != null) {
				var delegate:GetProjectDelegate = new GetProjectDelegate( this );
				delegate.getProject(id);
			} else {
				model.selectedProject = null;
			}
			
		}
		
		public function result(data:Object):void
		{
			var result:Object = (data as ResultEvent).result.project;
			//model.project = new ProjectVO(result.result.id,result.result.name, result.result.description, result.result.manager);
			model.selectedProject = new ProjectVO(result.id, result.name, result.description, result.manager);
		}
		
		public function fault(info:Object):void
		{
			var fault:FaultEvent = info as FaultEvent;
		}
	}
}