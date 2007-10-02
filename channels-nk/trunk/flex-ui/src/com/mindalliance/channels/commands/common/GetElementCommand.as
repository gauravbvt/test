// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.commands.common
{
	import com.adobe.cairngorm.control.CairngormEvent;
	import com.mindalliance.channels.business.common.BaseDelegate;
	import com.mindalliance.channels.commands.BaseDelegateCommand;
	import com.mindalliance.channels.events.common.*;
	import com.mindalliance.channels.events.people.*;
	import com.mindalliance.channels.model.EditorModel;
	import com.mindalliance.channels.model.ElementModel;
	import com.mindalliance.channels.vo.common.ElementVO;
	
	public class GetElementCommand extends BaseDelegateCommand
	{
        public function GetElementCommand(delegateType : Class) {
        	this.delegateType = delegateType;
        }
	
	   private var delegateType : Class;
	
        override public function execute(event:CairngormEvent):void
        {
            var evt:GetElementEvent = event as GetElementEvent;           
            var id : String = evt.id;
            var model : EditorModel = evt.model;
            if (model != null)
                model.id = id;
            if (id != null) {
                log.debug("Retrieving element {0}", [id]);
                var delegate:BaseDelegate = new delegateType( this );
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