// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.commands.scenario
{
	import com.adobe.cairngorm.control.CairngormEvent;
	import com.mindalliance.channels.business.scenario.AcquirementDelegate;
	import com.mindalliance.channels.commands.BaseDelegateCommand;
	import com.mindalliance.channels.events.scenario.*;
	import com.mindalliance.channels.util.ElementHelper;
	
	import mx.collections.ArrayCollection;
	
	public class DeleteAcquirementCommand extends BaseDelegateCommand
	{
	
		override public function execute(event:CairngormEvent):void
		{
			var evt:DeleteAcquirementEvent = event as DeleteAcquirementEvent;
			var delegate:AcquirementDelegate = new AcquirementDelegate( this );
			delegate.deleteElement(evt.id);
		}
		
		override public function result(data:Object):void
		{
            var result:Boolean = data["data"] as Boolean;
            if (result == true) {
                var col : ArrayCollection = channelsModel.getElementListModel("acquirements").data;
                var inx : int;
                if (col != null) {
                    inx = ElementHelper.findElementIndexById(data["id"], col);
                    col.removeItemAt(inx);
                }
                col = channelsModel.getElementListModel("acquirements" + data["taskId"]).data
                
                if (col != null) {
                    inx = ElementHelper.findElementIndexById(data["id"], col);
                    if (inx >= 0) col.removeItemAt(inx);
                }
                log.info("Acquirement successfully deleted");
            } else {
                log.warn("Acquirement Deletion failed");   
            }			
		}
	}
}