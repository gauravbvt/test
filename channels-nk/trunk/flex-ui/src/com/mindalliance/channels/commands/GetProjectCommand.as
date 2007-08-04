
package com.mindalliance.channels.commands
{
	import com.adobe.cairngorm.commands.ICommand;
	import com.adobe.cairngorm.control.CairngormEvent;
	import com.mindalliance.channels.business.GetProjectDelegate;
	import com.mindalliance.channels.events.GetProjectEvent;
	import com.mindalliance.channels.model.ChannelsModelLocator;
	import com.mindalliance.channels.vo.ProjectVO;
	
	import mx.rpc.IResponder;
	import mx.rpc.events.FaultEvent;
	import mx.rpc.events.ResultEvent;
	
	public class GetProjectCommand implements ICommand, IResponder
	{
		
		private var model : ChannelsModelLocator = ChannelsModelLocator.getInstance();
	
		public function execute(event:CairngormEvent):void
		{
			var evt:GetProjectEvent = event as GetProjectEvent;
			var delegate:GetProjectDelegate = new GetProjectDelegate( this );
			
			var id : String = evt.id;
			if (id != null) {
				delegate.getProject(id);
			} else {
				model.selectedProject = null;
			}
			
		}
		
		public function result(data:Object):void
		{
			var result:ResultEvent = data as ResultEvent;
			//model.project = new ProjectVO(result.result.id,result.result.name, result.result.description, result.result.manager);
			model.selectedProject = new ProjectVO(result.result.project.id, result.result.project.name, result.result.project.description, result.result.project.manager);
		}
		
		public function fault(info:Object):void
		{
			var fault:FaultEvent = info as FaultEvent;
		}
	}
}