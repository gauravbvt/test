
package com.mindalliance.channels.control
{
	import com.adobe.cairngorm.control.FrontController;
    import com.mindalliance.channels.commands.application.*;
    import com.mindalliance.channels.events.application.*;
    
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
		}
		
		private function initializePeople() : void
		{
				
		}
	}
}