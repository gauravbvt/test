
package com.mindalliance.channels.control
{
	import com.adobe.cairngorm.control.FrontController;
	import com.mindalliance.channels.commands.application.*;
	import com.mindalliance.channels.commands.categories.*;
	import com.mindalliance.channels.commands.common.*;
	import com.mindalliance.channels.commands.people.*;
	import com.mindalliance.channels.commands.resources.*;
    import com.mindalliance.channels.commands.scenario.*;;
    import com.mindalliance.channels.commands.sharingneed.*;
	import com.mindalliance.channels.events.application.*;
	import com.mindalliance.channels.events.categories.*;
	import com.mindalliance.channels.events.common.*;
	import com.mindalliance.channels.events.people.*;
	import com.mindalliance.channels.events.resources.*;
    import com.mindalliance.channels.events.scenario.*;
    import com.mindalliance.channels.events.sharingneed.*;
    
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
			initializeCategories();	
			initializePeople();
			initializeResources();
			initializeScenario();
			initializeSharingNeed();
		}
		
		private function initializeCommon() : void
		{
			
            this.addCommand(ChooserSelectEvent.ChooserSelect_Event, ChooserSelectCommand);
            this.addCommand(QueueUpdateEvent.QueueUpdate_Event, QueueUpdateCommand);
            this.addCommand(DeleteElementEvent.DeleteElement_Event, DeleteElementCommand);
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
            this.addCommand(LoadGlobalDataEvent.LoadGlobalData_Event, LoadGlobalDataCommand);
            this.addCommand(LoadScenarioEvent.LoadScenario_Event, LoadScenarioCommand);
           this.addCommand(QueueProjectUpdateEvent.QueueProjectUpdate_Event, QueueProjectUpdateCommand);
			this.addCommand(QueueScenarioUpdateEvent.QueueScenarioUpdate_Event, QueueScenarioUpdateCommand);
			
			
		}
		
		private function initializeCategories() : void 
		{
            this.addCommand(GetCategoryListEvent.GetCategoryList_Event, GetCategoryListCommand);
            this.addCommand(GetCategoryListByDisciplineEvent.GetCategoryListByDiscipline_Event, GetCategoryListByDisciplineCommand);
            this.addCommand(GetCategoryEvent.GetCategory_Event, GetCategoryCommand);
            this.addCommand(UpdateCategoryEvent.UpdateCategory_Event, UpdateCategoryCommand);
            this.addCommand(GetDisciplineListEvent.GetDisciplineList_Event, GetDisciplineListCommand);
            this.addCommand(AddCategoriesToSetEvent.AddCategoriesToSet_Event, AddCategoriesToSetCommand);
            this.addCommand(RemoveCategoriesFromSetEvent.RemoveCategoriesFromSet_Event, RemoveCategoriesFromSetCommand);
			
		}
		
		private function initializePeople() : void
		{
			this.addCommand(GetOrganizationListEvent.GetOrganizationList_Event, GetOrganizationListCommand);
			this.addCommand(GetOrganizationEvent.GetOrganization_Event, GetOrganizationCommand);
			this.addCommand(CreateOrganizationEvent.CreateOrganization_Event, CreateOrganizationCommand);			
			this.addCommand(UpdateOrganizationEvent.UpdateOrganization_Event, UpdateOrganizationCommand);
			
			
            this.addCommand(GetRoleListEvent.GetRoleList_Event, GetRoleListCommand);
            this.addCommand(GetRoleEvent.GetRole_Event, GetRoleCommand);
            this.addCommand(CreateRoleEvent.CreateRole_Event, CreateRoleCommand);           
            this.addCommand(UpdateRoleEvent.UpdateRole_Event, UpdateRoleCommand);
            
            this.addCommand(GetUserEvent.GetUser_Event, GetUserCommand);
            this.addCommand(GetPersonEvent.GetPerson_Event, GetPersonCommand);
            this.addCommand(CreatePersonEvent.CreatePerson_Event, CreatePersonCommand);           
            this.addCommand(GetPersonByUserEvent.GetPersonByUser_Event, GetPersonByUserCommand);
            this.addCommand(GetPersonListEvent.GetPersonList_Event, GetPersonListCommand);
            this.addCommand(UpdatePersonEvent.UpdatePerson_Event, UpdatePersonCommand);

		}
		
		private function initializeResources() : void
		{
            this.addCommand(GetRepositoryListEvent.GetRepositoryList_Event, GetRepositoryListCommand);
            this.addCommand(GetRepositoryEvent.GetRepository_Event, GetRepositoryCommand);
            this.addCommand(CreateRepositoryEvent.CreateRepository_Event, CreateRepositoryCommand);
            this.addCommand(UpdateRepositoryEvent.UpdateRepository_Event, UpdateRepositoryCommand);
		}
		
		private function initializeScenario() : void
		{
			
            this.addCommand(GetTaskListEvent.GetTaskList_Event, GetTaskListCommand);
            this.addCommand(GetTaskEvent.GetTask_Event, GetTaskCommand);
            this.addCommand(CreateTaskEvent.CreateTask_Event, CreateTaskCommand);
            this.addCommand(UpdateTaskEvent.UpdateTask_Event, UpdateTaskCommand);
            
            this.addCommand(GetArtifactListEvent.GetArtifactList_Event, GetArtifactListCommand);
            this.addCommand(GetArtifactListByTaskEvent.GetArtifactListByTask_Event, GetArtifactListByTaskCommand);
            this.addCommand(GetArtifactEvent.GetArtifact_Event, GetArtifactCommand);
            this.addCommand(CreateArtifactEvent.CreateArtifact_Event, CreateArtifactCommand);
            this.addCommand(UpdateArtifactEvent.UpdateArtifact_Event, UpdateArtifactCommand);
            this.addCommand(UpdateArtifactTaskEvent.UpdateArtifactTask_Event, UpdateArtifactTaskCommand);
            
            this.addCommand(GetAcquirementListEvent.GetAcquirementList_Event, GetAcquirementListCommand);
            this.addCommand(GetAcquirementListByTaskEvent.GetAcquirementListByTask_Event, GetAcquirementListByTaskCommand);
            this.addCommand(GetAcquirementEvent.GetAcquirement_Event, GetAcquirementCommand);
            this.addCommand(CreateAcquirementEvent.CreateAcquirement_Event, CreateAcquirementCommand);
            this.addCommand(UpdateAcquirementEvent.UpdateAcquirement_Event, UpdateAcquirementCommand);
            this.addCommand(UpdateAcquirementTaskEvent.UpdateAcquirementTask_Event, UpdateAcquirementTaskCommand);
           
            this.addCommand(GetEventListEvent.GetEventList_Event, GetEventListCommand);
            this.addCommand(GetEventEvent.GetEvent_Event, GetEventCommand);
            this.addCommand(CreateEventEvent.CreateEvent_Event, CreateEventCommand);
            this.addCommand(UpdateEventEvent.UpdateEvent_Event, UpdateEventCommand);
                        
            this.addCommand(GetAgentListByScenarioEvent.GetAgentListByScenario_Event, GetAgentListByScenarioCommand);
            this.addCommand(GetAgentListEvent.GetAgentList_Event, GetAgentListCommand);
            this.addCommand(CreateAgentEvent.CreateAgent_Event, CreateAgentCommand);       
		}
		
		private function initializeSharingNeed() : void
		{
            this.addCommand(CreateKnowEvent.CreateKnow_Event, CreateKnowCommand);
            this.addCommand(CreateKnowSequenceEvent.CreateKnowSequence_Event, CreateKnowSequenceCommand);
            this.addCommand(CreateNeedToKnowEvent.CreateNeedToKnow_Event, CreateNeedToKnowCommand);
            this.addCommand(CreateNeedToKnowSequenceEvent.CreateNeedToKnowSequence_Event, CreateNeedToKnowSequenceCommand);
            this.addCommand(CreateSharingNeedEvent.CreateSharingNeed_Event, CreateSharingNeedCommand);
            this.addCommand(CreateSharingNeedSequenceEvent.CreateSharingNeedSequence_Event, CreateSharingNeedSequenceCommand);
            this.addCommand(GetKnowEvent.GetKnow_Event, GetKnowCommand);
            this.addCommand(GetNeedToKnowEvent.GetNeedToKnow_Event, GetNeedToKnowCommand);
            this.addCommand(GetSharingNeedListEvent.GetSharingNeedList_Event, GetSharingNeedListCommand);
            this.addCommand(UpdateKnowEvent.UpdateKnow_Event, UpdateKnowCommand);
            this.addCommand(UpdateNeedToKnowEvent.UpdateNeedToKnow_Event, UpdateNeedToKnowCommand);         
            this.addCommand(DeleteSharingNeedEvent.DeleteSharingNeed_Event, DeleteSharingNeedCommand);
        }
	}
}