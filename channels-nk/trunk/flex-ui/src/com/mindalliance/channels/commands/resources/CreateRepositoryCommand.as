// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.commands.resources
{
	import com.adobe.cairngorm.control.CairngormEvent;
	import com.mindalliance.channels.business.resources.RepositoryDelegate;
	import com.mindalliance.channels.commands.BaseDelegateCommand;
	import com.mindalliance.channels.events.resources.*;
	import com.mindalliance.channels.vo.RepositoryVO;
	import com.mindalliance.channels.vo.common.ElementVO;
	
	public class CreateRepositoryCommand extends BaseDelegateCommand
	{
	
		override public function execute(event:CairngormEvent):void
		{
			var evt:CreateRepositoryEvent = event as CreateRepositoryEvent;
			var delegate:RepositoryDelegate = new RepositoryDelegate( this );
			delegate.create(evt.name, evt.organizationId);
		}
		
		
        override public function result(data:Object):void
        {
            var result:RepositoryVO = data["data"] as RepositoryVO;
            if (result!=null) {
                log.info("Repository created");
                channelsModel.getElementListModel('repositories').data.addItem(new ElementVO(result.id, result.name));
                //CairngormEventDispatcher.getInstance().dispatchEvent( new GetOrganizationListEvent() );
            }
        }
	}
}