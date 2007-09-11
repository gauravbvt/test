// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.commands.resources
{
    import com.adobe.cairngorm.control.CairngormEvent;
    import com.mindalliance.channels.business.resources.RepositoryDelegate;
    import com.mindalliance.channels.commands.BaseDelegateCommand;
    import com.mindalliance.channels.events.resources.GetRepositoryListEvent;
    
    import mx.collections.ArrayCollection;
    
    public class GetRepositoryListCommand extends BaseDelegateCommand
    {
    
        override public function execute(event:CairngormEvent):void
        {
            var evt:GetRepositoryListEvent = event as GetRepositoryListEvent;
            var delegate:RepositoryDelegate = new RepositoryDelegate( this );
            log.debug("Retrieving Repository list");
            delegate.getRepositoryList();
        }
        
        override public function result(data:Object):void
        {
            channelsModel.getElementListModel("repositories").data = (data["data"] as ArrayCollection);
            log.debug("Successfully retrieved Repository list");
        }
        
        override public function fault(info:Object):void
        {
            channelsModel.getElementListModel("repositories").data  = null;
            super.fault(info);
        }
	}
}