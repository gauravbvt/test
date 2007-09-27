// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.commands.resources
{
	import com.adobe.cairngorm.control.CairngormEvent;
	import com.mindalliance.channels.business.resources.RepositoryDelegate;
	import com.mindalliance.channels.commands.BaseDelegateCommand;
	import com.mindalliance.channels.events.resources.*;
	import com.mindalliance.channels.util.ElementHelper;
	
	import mx.collections.ArrayCollection;
	
	public class DeleteRepositoryCommand extends BaseDelegateCommand
	{
	
		override public function execute(event:CairngormEvent):void
		{
			var evt:DeleteRepositoryEvent = event as DeleteRepositoryEvent;
			var delegate:RepositoryDelegate = new RepositoryDelegate( this );
			delegate.deleteElement(evt.id);
		}
		
		override public function result(data:Object):void
		{
            var result:Boolean = data["data"] as Boolean;
            if (result == true) {
                var col : ArrayCollection = channelsModel.getElementListModel("repositories").data;
                channelsModel.deleteElementModel(data["id"]);
                if (col != null) {
                    var inx: int = ElementHelper.findElementIndexById(data["id"], col);
                    if (inx >= 0) col.removeItemAt(inx);
                }
                log.info("Repository successfully deleted");
            } else {
                log.warn("Repository Deletion failed");   
            }			
		}
	}
}