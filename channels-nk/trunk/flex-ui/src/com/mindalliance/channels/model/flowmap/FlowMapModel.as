package com.mindalliance.channels.model.flowmap
{
	import com.mindalliance.channels.view.flowmap.FlowMap;
	import com.mindalliance.channels.view.UtilFuncs;
	
	public class FlowMapModel
	{
		private var tasksHandler:TasksHandler ;
		
		private var eventsHandler:EventsHandler ;
		
		private var orgsHandler:OrganizationsHandler ;
		
		private var reposHandler:RepositoriesHandler ;
		
		public function FlowMapModel(flowmap:FlowMap) {
			
			tasksHandler = new TasksHandler(flowmap) ;
			
			eventsHandler = new EventsHandler(flowmap) ;
			
			orgsHandler = new OrganizationsHandler(flowmap) ;
			
			reposHandler = new RepositoriesHandler(flowmap) ;
		}
	}
}