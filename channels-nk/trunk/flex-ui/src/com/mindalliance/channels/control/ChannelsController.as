
package com.mindalliance.channels.control
{
	import com.adobe.cairngorm.control.FrontController;
    import com.mindalliance.channels.commands.common.*;
    import com.mindalliance.channels.commands.application.*;
    import com.mindalliance.channels.commands.people.*;
    import com.mindalliance.channels.commands.resources.*;
    import com.mindalliance.channels.commands.scenario.*;
    import com.mindalliance.channels.events.common.*;
    import com.mindalliance.channels.events.application.*;
    import com.mindalliance.channels.events.people.*;
    import com.mindalliance.channels.events.resources.*;
    import com.mindalliance.channels.events.scenario.*;
    
	public class ChannelsController extends FrontController
	{
		public function ChannelsController()
		{
			this.initialize();
		}
		
		private function initialize() : void
		{
			initializeCommon();
			initializeApplication();	
			initializePeople();
			initializeResources();
			initializeScenario();
		}
		
		private function initializeCommon() : void
		{
			
            this.addCommand(ChooserSelectEvent.ChooserSelect_Event, ChooserSelectCommand);
            this.addCommand(QueueUpdateEvent.QueueUpdate_Event, QueueUpdateCommand);
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
			
			
		}
		
		private function initializePeople() : void
		{
			this.addCommand(GetOrganizationListEvent.GetOrganizationList_Event, GetOrganizationListCommand);
			this.addCommand(GetOrganizationEvent.GetOrganization_Event, GetOrganizationCommand);
			this.addCommand(CreateOrganizationEvent.CreateOrganization_Event, CreateOrganizationCommand);			
			this.addCommand(DeleteOrganizationEvent.DeleteOrganization_Event, DeleteOrganizationCommand);
			this.addCommand(UpdateOrganizationEvent.UpdateOrganization_Event, UpdateOrganizationCommand);
			
			
            this.addCommand(GetRoleListEvent.GetRoleList_Event, GetRoleListCommand);
            this.addCommand(GetRoleEvent.GetRole_Event, GetRoleCommand);
            this.addCommand(UpdateRoleEvent.UpdateRole_Event, UpdateRoleCommand);
            
            this.addCommand(GetUserEvent.GetUser_Event, GetUserCommand);
            this.addCommand(GetPersonEvent.GetPerson_Event, GetPersonCommand);
            this.addCommand(GetPersonListEvent.GetPersonList_Event, GetPersonListCommand);
            this.addCommand(UpdatePersonEvent.UpdatePerson_Event, UpdatePersonCommand);
            
		}
		
		private function initializeResources() : void
		{
			this.addCommand(GetRepositoryListEvent.GetRepositoryList_Event, GetRepositoryListCommand);
		}
		
		private function initializeScenario() : void
		{
			
            this.addCommand(GetTaskListEvent.GetTaskList_Event, GetTaskListCommand);
			
		}
	}
}