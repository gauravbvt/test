
package com.mindalliance.channels.commands.application
{
	import com.adobe.cairngorm.commands.ICommand;
	import com.adobe.cairngorm.control.CairngormEvent;
	import com.mindalliance.channels.util.ServiceUtil;
	import com.mindalliance.channels.business.application.ProjectDelegate;
	import com.mindalliance.channels.events.application.GetProjectListEvent;
	import com.mindalliance.channels.model.ChannelsModelLocator;
	import com.mindalliance.channels.model.application.ProjectScenarioBrowserModel;
	
	import mx.rpc.IResponder;
	import mx.rpc.events.FaultEvent;
	import mx.rpc.events.ResultEvent;
	
	public class GetProjectListCommand implements ICommand, IResponder
	{
		private var model : ProjectScenarioBrowserModel = ChannelsModelLocator.getInstance().projectScenarioBrowserModel;
		
		public function execute(event:CairngormEvent):void
		{
			var evt:GetProjectListEvent = event as GetProjectListEvent;
			var delegate:ProjectDelegate = new ProjectDelegate( this );
			
			delegate.getProjectList();
		}
		
		public function result(data:Object):void
		{
			var result:Object = (data as ResultEvent).result;
			model.projectList = ServiceUtil.convertServiceResults(result.projects.project);
			model.selectedProject = null;
			model.scenarioList = null;
			model.selectedScenario = null;
		}
		
		public function fault(info:Object):void
		{
			var fault:FaultEvent = info as FaultEvent;
		}
		
	}
}