// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.common.commands
{
    import com.adobe.cairngorm.control.CairngormEvent;
    import com.mindalliance.channels.common.business.DeleteElementDelegate;
    import com.mindalliance.channels.common.events.*;
    import com.mindalliance.channels.util.CairngormHelper;
    
    import mx.collections.ArrayCollection;
    
    public class DeleteElementCommand extends BaseDelegateCommand
    {
    
        override public function execute(event:CairngormEvent):void
        {
            var evt:DeleteElementEvent = event as DeleteElementEvent;
            var delegate:DeleteElementDelegate = new DeleteElementDelegate( this );
            delegate.deleteElement(evt.id);
        }
        
        override public function result(data:Object):void
        {
            var result:Boolean = data["data"] as Boolean;
            if (result == true) {
                var deleted : ArrayCollection = data["deleted"];
                channelsModel.removeFromCache(deleted);
                var updated : ArrayCollection = data["updated"];
                for each (var id : String in updated) {
                    if (channelsModel.isCached(id)) {
                    	CairngormHelper.fireEvent(new GetElementEvent(id));	
                    }
                }
            } else {
                log.warn("Element Deletion failed");   
            } 
        }
    }
}