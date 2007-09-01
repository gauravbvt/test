// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.commands.people
{
	import com.adobe.cairngorm.control.CairngormEvent;
	import com.mindalliance.channels.business.people.RoleDelegate;
	import com.mindalliance.channels.commands.BaseDelegateCommand;
	import com.mindalliance.channels.events.people.*;
	import com.mindalliance.channels.model.EditorModel;
	import com.mindalliance.channels.model.ElementModel;
	import com.mindalliance.channels.util.ElementHelper;
	import com.mindalliance.channels.vo.RoleVO;
	import com.mindalliance.channels.vo.common.ElementVO;
	
	public class GetRoleCommand extends BaseDelegateCommand
	{
	
		override public function execute(event:CairngormEvent):void
		{
            var evt:GetRoleEvent = event as GetRoleEvent;           
            var id : String = evt.id;
            var model : EditorModel = evt.model;
            model.id = id;
            if (id != null) {
                log.debug("Retrieving Role {0}", [id]);
                var delegate:RoleDelegate = new RoleDelegate( this );
                delegate.getElement(id);
            }
		}
		
		override public function result(data:Object):void
		{
			var result:RoleVO = (data as RoleVO);
            if (result != null) {
                log.debug("Setting selected Role to {0}", [result.id]);
                var model : ElementModel = channelsModel.getElementModel(result.id);
                model.data = result;
                

            } else {
                log.warn("Unable to retrieve Role");    
            }
		}
	}
}