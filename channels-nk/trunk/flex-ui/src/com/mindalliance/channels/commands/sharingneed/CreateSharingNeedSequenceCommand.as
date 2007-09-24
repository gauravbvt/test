// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.commands.sharingneed
{
    import com.adobe.cairngorm.control.CairngormEvent;
    import com.mindalliance.channels.business.sharingneed.SharingNeedDelegate;
    import com.mindalliance.channels.commands.BaseDelegateCommand;
    import com.mindalliance.channels.events.sharingneed.*;
    import com.mindalliance.channels.vo.SharingNeedVO;
    
    public class CreateSharingNeedSequenceCommand extends BaseDelegateCommand
    {
    
        override public function execute(event:CairngormEvent):void
        {
            var evt:CreateSharingNeedSequenceEvent = event as CreateSharingNeedSequenceEvent;
            var delegate:SharingNeedDelegate = new SharingNeedDelegate( this );
            delegate.create(evt.knowId,evt.needToKnowId);
        }
        
        override public function result(data:Object):void
        {
            var result : SharingNeedVO = data["data"] as SharingNeedVO;
            if (result != null) {
                 channelsModel.getElementListModel('sharingneeds').data.addItem(result);
                 	
            }
        }
    }
}