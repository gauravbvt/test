// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.people.commands
{
	import com.adobe.cairngorm.control.CairngormEvent;
	import com.mindalliance.channels.common.business.UpdateElementDelegate;
	import com.mindalliance.channels.people.events.*;
	import com.mindalliance.channels.model.EditorModel;
	import com.mindalliance.channels.model.ElementModel;
	import com.mindalliance.channels.vo.UserVO;
	
	public class ChangePasswordCommand extends BaseDelegateCommand
	{
	
		override public function execute(event:CairngormEvent):void
		{
			var evt:ChangePasswordEvent = event as ChangePasswordEvent;
			var model : EditorModel = evt.model;
			var element : ElementModel = model.getElementModel(model.id);
		    var data : UserVO  = (element.data as UserVO);
            data.password = evt.newPassword;	
            element.dirty == true;
		    if (element.dirty == true) {
		    	log.debug("changing password for user {0}", data.name);
		    	var delegate:UpdateElementDelegate = new UpdateElementDelegate( this,data );
            	delegate.update();	
		    }
		}
		
		override public function result(data:Object):void
		{
			
		}
	}
}