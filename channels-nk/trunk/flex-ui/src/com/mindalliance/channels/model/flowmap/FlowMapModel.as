package com.mindalliance.channels.model.flowmap
{
	import com.mindalliance.channels.view.flowmap.FlowMap;
	
	public class FlowMapModel
	{
		private var tasksHandler:TasksHandler ;
		
		private var eventsHandler:EventsHandler ;
		
		private var orgsHandler:OrganizationsHandler ;
		
		private var reposHandler:RepositoriesHandler ;
		
		private var agentsHandler:AgentsHandler ;
		
		public function FlowMapModel(flowmap:FlowMap) {
			
			tasksHandler = new TasksHandler(flowmap) ;
			
			agentsHandler = new AgentsHandler(flowmap) ;
			
			eventsHandler = new EventsHandler(flowmap) ;
			
			orgsHandler = new OrganizationsHandler(flowmap) ;
			
			reposHandler = new RepositoriesHandler(flowmap) ;
		}
	}
}