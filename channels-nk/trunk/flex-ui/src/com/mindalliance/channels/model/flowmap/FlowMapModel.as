package com.mindalliance.channels.model.flowmap
{
	public class FlowMapModel
	{
		public function init():void {
			TaskModel.getInstance() ;
			EventModel.getInstance() ;
			AgentModel.getInstance() ;
			RepositoryModel.getInstance() ;
			OrganizationModel.getInstance() ;
			SharingNeedModel.getInstance() ;
		}
	}
}