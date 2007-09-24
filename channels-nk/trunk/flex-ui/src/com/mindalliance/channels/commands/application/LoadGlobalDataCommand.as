// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.commands.application
{
    import com.adobe.cairngorm.control.CairngormEvent;
    import com.mindalliance.channels.commands.BaseCommand;
    import com.mindalliance.channels.events.application.LoadGlobalDataEvent;
    import com.mindalliance.channels.events.people.GetOrganizationListEvent;
    import com.mindalliance.channels.events.people.GetPersonListEvent;
    import com.mindalliance.channels.events.people.GetRoleListEvent;
    import com.mindalliance.channels.events.resources.GetRepositoryListEvent;
    import com.mindalliance.channels.events.scenario.GetAgentListByScenarioEvent;
    import com.mindalliance.channels.util.CairngormHelper;
    
    public class LoadGlobalDataCommand extends BaseCommand
    {
        override public function execute(event:CairngormEvent):void
        {
            var evt:LoadGlobalDataEvent = event as LoadGlobalDataEvent;
            CairngormHelper.fireEvent( new GetRoleListEvent() );
            CairngormHelper.fireEvent( new GetPersonListEvent() );
            CairngormHelper.fireEvent( new GetOrganizationListEvent() );
            CairngormHelper.fireEvent( new GetRepositoryListEvent() );
            CairngormHelper.fireEvent(new GetAgentListByScenarioEvent(null)) ;
        }
    }
}