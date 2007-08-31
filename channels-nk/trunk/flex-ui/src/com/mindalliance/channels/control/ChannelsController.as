
package com.mindalliance.channels.control
{
	import com.adobe.cairngorm.control.FrontController;
    import com.mindalliance.channels.commands.common.*;
    import com.mindalliance.channels.commands.application.*;
    import com.mindalliance.channels.commands.people.*;
    import com.mindalliance.channels.events.common.*;
    import com.mindalliance.channels.events.application.*;
    import com.mindalliance.channels.events.people.*;
    
	public class ChannelsController extends FrontController
	{
		public function ChannelsController()
		{
			this.initialize();
		}
		
		private function initialize() : void
		{
			initializeApplication();	
			initializePeople();
		}
		
		private function initializeApplication() : void
		{
			this.addCommand(GetProjectListEvent.GetProjectList_Event, GetProjectListCommand);
			this.addCommand(GetProjectEvent.GetProject_Event, GetProjectCommand);
			this.addCommand(GetScenarioListEvent.GetScenarioList_Event, GetScenarioListCommand);
			this.addCommand(GetScenarioEvent.GetScenario_Event, GetScenarioCommand);
			this.addCommand(CreateProjectEvent.CreateProject_Event, CreateProjectCommand);
			this.addCommand(CreateScenarioEvent.CreateScenario_Event, CreateScenarioCommand);			
			this.addCommand(DeleteProjectEvent.DeleteProject_Event, DeleteProjectCommand);
			this.addCommand(DeleteScenarioEvent.DeleteScenario_Event, DeleteScenarioCommand);
			this.addCommand(UpdateProjectEvent.UpdateProject_Event, UpdateProjectCommand);
			this.addCommand(UpdateScenarioEvent.UpdateScenario_Event, UpdateScenarioCommand);
			this.addCommand(LoadScenarioEvent.LoadScenario_Event, LoadScenarioCommand);
			this.addCommand(QueueProjectUpdateEvent.QueueProjectUpdate_Event, QueueProjectUpdateCommand);
			this.addCommand(QueueScenarioUpdateEvent.QueueScenarioUpdate_Event, QueueScenarioUpdateCommand);
			
			
            this.addCommand(ChooserSelectEvent.ChooserSelect_Event, ChooserSelectCommand);
		}
		
		private function initializePeople() : void
		{
			this.addCommand(GetOrganizationListEvent.GetOrganizationList_Event, GetOrganizationListCommand);
			this.addCommand(GetOrganizationEvent.GetOrganization_Event, GetOrganizationCommand);
			this.addCommand(CreateOrganizationEvent.CreateOrganization_Event, CreateOrganizationCommand);			
			this.addCommand(DeleteOrganizationEvent.DeleteOrganization_Event, DeleteOrganizationCommand);
			this.addCommand(UpdateOrganizationEvent.UpdateOrganization_Event, UpdateOrganizationCommand);
			this.addCommand(QueueOrganizationUpdateEvent.QueueOrganizationUpdate_Event, QueueOrganizationUpdateCommand);
			
			
            this.addCommand(GetRoleListEvent.GetRoleList_Event, GetRoleListCommand);
            this.addCommand(GetRoleEvent.GetRole_Event, GetRoleCommand);
		}
	}
}