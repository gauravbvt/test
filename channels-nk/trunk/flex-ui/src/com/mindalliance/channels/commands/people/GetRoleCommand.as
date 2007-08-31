// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.commands.people
{
	import com.adobe.cairngorm.control.CairngormEvent;
	import com.mindalliance.channels.business.people.RoleDelegate;
	import com.mindalliance.channels.commands.BaseDelegateCommand;
	import com.mindalliance.channels.events.people.*;
	import com.mindalliance.channels.model.EditorModel;
	import com.mindalliance.channels.vo.RoleVO;
	
	public class GetRoleCommand extends BaseDelegateCommand
	{
	
		override public function execute(event:CairngormEvent):void
		{
            var evt:GetRoleEvent = event as GetRoleEvent;           
            var id : String = evt.id;
            var model : EditorModel = evt.model;
            if (id != null) {
                log.debug("Retrieving Role {0}", [id]);
                var delegate:RoleDelegate = new RoleDelegate( this );
                delegate.getElement(id);
            } else {
                log.debug("Setting selected Role to null");
                model.id = null;
            }
		}
		
		override public function result(data:Object):void
		{
			var result:RoleVO = (data as RoleVO);
            if (result != null) {
                log.debug("Setting selected Role to {0}", [result.id]);
                channelsModel.getElementModel(result.id).data = result;// new RoleVO(result.id, result.name, result.description, result.manager);
            } else {
                log.warn("Unable to retrieve Role");    
            }
		}
	}
}