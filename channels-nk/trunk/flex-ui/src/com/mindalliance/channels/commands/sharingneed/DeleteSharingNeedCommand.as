// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.commands.sharingneed
{
    import com.adobe.cairngorm.control.CairngormEvent;
    import com.mindalliance.channels.business.common.BaseDelegate;
    import com.mindalliance.channels.commands.BaseCommand;
    import com.mindalliance.channels.events.common.DeleteElementEvent;
    import com.mindalliance.channels.events.sharingneed.*;
    import com.mindalliance.channels.model.ElementModel;
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