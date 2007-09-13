// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.commands.scenario
{
	import com.adobe.cairngorm.control.CairngormEvent;
	import com.mindalliance.channels.business.scenario.ArtifactDelegate;
	import com.mindalliance.channels.commands.BaseDelegateCommand;
	import com.mindalliance.channels.events.scenario.*;
	import com.mindalliance.channels.vo.ArtifactVO;
	import com.mindalliance.channels.vo.common.ElementVO;
	
	public class CreateArtifactCommand extends BaseDelegateCommand
	{
	
		override public function execute(event:CairngormEvent):void
		{
			var evt:CreateArtifactEvent = event as CreateArtifactEvent;
			var delegate:ArtifactDelegate = new ArtifactDelegate( this );
			delegate.create(evt.name, evt.taskId);
		}
		
		override public function result(data:Object):void
		{
			var result:ArtifactVO = data["data"] as ArtifactVO;
            if (result!=null) {
                log.info("Artifact created");
                channelsModel.getElementListModel('artifacts').data.addItem(new ElementVO(result.id, result.name));
                //CairngormEventDispatcher.getInstance().dispatchEvent( new GetOrganizationListEvent() );
            }
		}
	}
}