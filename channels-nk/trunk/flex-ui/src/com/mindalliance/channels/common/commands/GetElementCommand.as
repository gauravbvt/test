// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.common.commands
{
	import com.adobe.cairngorm.control.CairngormEvent;
	import com.mindalliance.channels.common.business.GetElementDelegate;
	import com.mindalliance.channels.common.events.*;
	import com.mindalliance.channels.people.events.*;
	import com.mindalliance.channels.model.EditorModel;
	import com.mindalliance.channels.model.ElementModel;
	import com.mindalliance.channels.vo.common.ElementVO;
	
	public class GetElementCommand extends BaseDelegateCommand
	{
        override public function execute(event:CairngormEvent):void
        {
            var evt:GetElementEvent = event as GetElementEvent;           
            var id : String = evt.id;
            var model : EditorModel = evt.model;
            if (model != null)
                model.id = id;
            if (id != null) {
                log.debug("Retrieving element {0}", [id]);
                var delegate:GetElementDelegate = new GetElementDelegate( this );
                delegate.getElement(id);
            }
        }
        
        override public function result(data:Object):void
        {
            var result:ElementVO = (data["data"] as ElementVO);
            if (result != null) {
                log.debug("Setting selected element to {0}", [result.id]);
                var model : ElementModel = channelsModel.getElementModel(result.id);
                model.data = result;
            } else {
                log.warn("Unable to retrieve element");    
            }
        }
	}
}