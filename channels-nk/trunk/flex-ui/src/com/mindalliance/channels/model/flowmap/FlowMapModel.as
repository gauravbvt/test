package com.mindalliance.channels.model.flowmap
{
	public class FlowMapModel
	{
		public function init():void {
			TasksHandler.getInstance() ;
			EventsHandler.getInstance() ;
			OrganizationsHandler.getInstance() ;
			RepositoriesHandler.getInstance() ;
		}
	}
}