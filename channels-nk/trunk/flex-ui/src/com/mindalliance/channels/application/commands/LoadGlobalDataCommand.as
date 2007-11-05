// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.application.commands
{
    import com.adobe.cairngorm.control.CairngormEvent;
    import com.mindalliance.channels.common.commands.BaseCommand;
    import com.mindalliance.channels.application.events.GetProjectListEvent;
    import com.mindalliance.channels.application.events.LoadGlobalDataEvent;
    import com.mindalliance.channels.people.events.GetOrganizationListEvent;
    import com.mindalliance.channels.people.events.GetPersonListEvent;
    import com.mindalliance.channels.people.events.GetRoleListEvent;
    import com.mindalliance.channels.resources.events.GetRepositoryListEvent;
    import com.mindalliance.channels.scenario.events.GetAgentListByScenarioEvent;
    import com.mindalliance.channels.util.CairngormHelper;
    
    public class LoadGlobalDataCommand extends BaseCommand
    {
        override public function execute(event:CairngormEvent):void
        {
            var evt:LoadGlobalDataEvent = event as LoadGlobalDataEvent;
            //CairngormHelper.fireEvent( new GetProjectListEvent() );
            CairngormHelper.fireEvent( new GetRoleListEvent() );
            CairngormHelper.fireEvent( new GetPersonListEvent() );
            CairngormHelper.fireEvent( new GetOrganizationListEvent() );
            CairngormHelper.fireEvent( new GetRepositoryListEvent() );
            CairngormHelper.fireEvent( new GetAgentListByScenarioEvent(null) ) ;
        }
    }
}