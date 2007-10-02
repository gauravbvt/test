// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.commands.common
{
    import com.adobe.cairngorm.control.CairngormEvent;
    import com.mindalliance.channels.business.common.BaseDelegate;
    import com.mindalliance.channels.commands.BaseDelegateCommand;
    import com.mindalliance.channels.events.common.*;
    
    import mx.collections.ArrayCollection;
    
    public class DeleteElementCommand extends BaseDelegateCommand
    {
    
        override public function execute(event:CairngormEvent):void
        {
            var evt:DeleteElementEvent = event as DeleteElementEvent;
            var delegate:BaseDelegate = new BaseDelegate( this );
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
                        // TODO update when generic GetElementEvent is completed	
                    }
                }
            } else {
                log.warn("Element Deletion failed");   
            } 
        }
    }
}