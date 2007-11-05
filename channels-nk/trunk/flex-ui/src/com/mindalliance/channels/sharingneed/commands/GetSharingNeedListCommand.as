// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.sharingneed.commands
{
    import com.mindalliance.channels.common.commands.GetElementListCommand;
    import com.mindalliance.channels.common.events.GetElementEvent;
    import com.mindalliance.channels.sharingneed.events.*;
    import com.mindalliance.channels.util.CairngormHelper;
    import com.mindalliance.channels.vo.SharingNeedVO;
    
    import mx.collections.ArrayCollection;
    
    public class GetSharingNeedListCommand extends GetElementListCommand
    {
        
        override public function result(data:Object):void
        {
        	super.result(data);
        	var result : ArrayCollection = data["data"] as ArrayCollection;
            for each (var el : SharingNeedVO in result) {
                channelsModel.getElementModel(el.id).data  = el;	
            }
            for each (var need : SharingNeedVO in result) {
            	CairngormHelper.fireEvent(new GetElementEvent(need.knowId));
            	CairngormHelper.fireEvent(new GetElementEvent(need.needToKnowId));
            	
            }
            
        }

    }
}