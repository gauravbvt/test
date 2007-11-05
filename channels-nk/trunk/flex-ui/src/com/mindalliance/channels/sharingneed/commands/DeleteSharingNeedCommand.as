// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.sharingneed.commands
{
    import com.adobe.cairngorm.control.CairngormEvent;
    import com.mindalliance.channels.common.commands.BaseCommand;
    import com.mindalliance.channels.common.events.DeleteElementEvent;
    import com.mindalliance.channels.model.ElementModel;
    import com.mindalliance.channels.sharingneed.events.*;
    import com.mindalliance.channels.util.CairngormHelper;
    import com.mindalliance.channels.vo.SharingNeedVO;
    
    public class DeleteSharingNeedCommand extends BaseCommand
    {
    
        override public function execute(event:CairngormEvent):void
        {
            var evt:DeleteSharingNeedEvent = event as DeleteSharingNeedEvent;
            var model:ElementModel = channelsModel.getElementModel(evt.id);
            var sn : SharingNeedVO = model.data as SharingNeedVO;
            CairngormHelper.fireEvent(new DeleteElementEvent(sn.knowId));
            CairngormHelper.fireEvent(new DeleteElementEvent(sn.needToKnowId));
            
        }

    }
}