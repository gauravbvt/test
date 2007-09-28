// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.commands.sharingneed
{
    import com.adobe.cairngorm.control.CairngormEvent;
    import com.mindalliance.channels.business.sharingneed.SharingNeedDelegate;
    import com.mindalliance.channels.commands.BaseDelegateCommand;
    import com.mindalliance.channels.events.sharingneed.*;
    import com.mindalliance.channels.util.CairngormHelper;
    import com.mindalliance.channels.vo.SharingNeedVO;
    
    import mx.collections.ArrayCollection;
    
    public class GetSharingNeedListCommand extends BaseDelegateCommand
    {
        
        override public function execute(event:CairngormEvent):void
        {
            var evt:GetSharingNeedListEvent = event as GetSharingNeedListEvent;
            var delegate:SharingNeedDelegate = new SharingNeedDelegate( this );
            log.debug("Retrieving sharing need list");
            delegate.getSharingNeedList(evt.scenarioId);
        }
        
        override public function result(data:Object):void
        {
        	var result : ArrayCollection = data["data"] as ArrayCollection;
            channelsModel.getElementListModel("sharingneeds").data = result;
            for each (var el : SharingNeedVO in result) {
                channelsModel.getElementModel(el.id).data  = el;	
            }
            log.debug("Successfully retrieved sharing need list");
            for each (var need : SharingNeedVO in result) {
            	CairngormHelper.fireEvent(new GetKnowEvent(need.knowId));
            	CairngormHelper.fireEvent(new GetNeedToKnowEvent(need.needToKnowId));
            	
            }
            
        }
        
        override public function fault(info:Object):void
        {
            channelsModel.getElementListModel("sharingneeds").data  = null;
            super.fault(info);
        }
    }
}