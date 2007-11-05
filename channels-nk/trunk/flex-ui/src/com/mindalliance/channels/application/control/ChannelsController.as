
package com.mindalliance.channels.application.control
{
	import com.adobe.cairngorm.control.FrontController;
	import com.mindalliance.channels.application.commands.*;
	import com.mindalliance.channels.categories.commands.*;
	import com.mindalliance.channels.common.commands.*;
	import com.mindalliance.channels.people.commands.*;
	import com.mindalliance.channels.commands.resources.*;
    import com.mindalliance.channels.scenario.commands.*;;
    import com.mindalliance.channels.sharingneed.commands.*;
	import com.mindalliance.channels.application.events.*;
	import com.mindalliance.channels.categories.events.*;
	import com.mindalliance.channels.common.events.*;
	import com.mindalliance.channels.people.events.*;
	import com.mindalliance.channels.resources.events.*;
    import com.mindalliance.channels.scenario.events.*;
    import com.mindalliance.channels.sharingneed.events.*;
    
	public class ChannelsController extends FrontController
	{
		public function ChannelsController()
		{
			this.initialize();
		}
		
		private function initialize() : void
		{
			
            this.addCommand(ChooserSelectEvent.ChooserSelect_Event, ChooserSelectCommand);
            this.addCommand(QueueUpdateEvent.QueueUpdate_Event, QueueUpdateCommand);
            this.addCommand(DeleteElementEvent.DeleteElement_Event, DeleteElementCommand);
            this.addCommand(GetElementEvent.GetElement_Event, GetElementCommand);
            this.addCommand(GetElementListEvent.GetElementList_Event, GetElementListCommand);
            this.addCommand(CreateElementEvent.CreateElement_Event, CreateElementCommand);
            this.addCommand(UpdateElementEvent.UpdateElement_Event, UpdateElementCommand);
        
            this.addCommand(LoadGlobalDataEvent.LoadGlobalData_Event, LoadGlobalDataCommand);
            this.addCommand(LoadScenarioEvent.LoadScenario_Event, LoadScenarioCommand);
			
            this.addCommand(AddCategoriesToSetEvent.AddCategoriesToSet_Event, AddCategoriesToSetCommand);
            this.addCommand(RemoveCategoriesFromSetEvent.RemoveCategoriesFromSet_Event, RemoveCategoriesFromSetCommand);
            
            this.addCommand(GetUserEvent.GetUser_Event, GetUserCommand);       
            this.addCommand(GetPersonByUserEvent.GetPersonByUser_Event, GetPersonByUserCommand);

            this.addCommand(GetTaskEvent.GetTask_Event, GetTaskCommand);
            this.addCommand(UpdateArtifactTaskEvent.UpdateArtifactTask_Event, UpdateArtifactTaskCommand);
            this.addCommand(UpdateAcquirementTaskEvent.UpdateAcquirementTask_Event, UpdateAcquirementTaskCommand);

            this.addCommand(CreateSharingNeedEvent.CreateSharingNeed_Event, CreateSharingNeedCommand);
            this.addCommand(GetSharingNeedListEvent.GetSharingNeedList_Event, GetSharingNeedListCommand);
            this.addCommand(DeleteSharingNeedEvent.DeleteSharingNeed_Event, DeleteSharingNeedCommand);
        }
	}
}