// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.commands.people
{
	import com.adobe.cairngorm.control.CairngormEvent;
	import com.mindalliance.channels.business.people.RoleDelegate;
	import com.mindalliance.channels.commands.BaseDelegateCommand;
	import com.mindalliance.channels.events.people.*;
	import com.mindalliance.channels.vo.RoleVO;
	
	public class UpdateRoleCommand extends BaseDelegateCommand
	{
	
		override public function execute(event:CairngormEvent):void
		{
			var evt:UpdateRoleEvent = event as UpdateRoleEvent;
			var delegate:RoleDelegate = new RoleDelegate( this );
			
			var model : EditorModel = evt.model;
            var element :ElementModel = model.getElementModel(model.id);
            var data : RoleVO = (element.data as RoleVO);   
            if (model.isChanged) {
                log.debug("Updating Role model");

                data.name=evt.name;
                data.description = evt.description;
                data.categories = categories; 
                data.organization = organization;
                data.expertise = expertise;
                
                model.isChanged = false;
                element.dirty=true;
            }
            if (element.dirty == true) {
                log.debug("Updating Role");
                var delegate:RoleDelegate = new RoleDelegate( this );
                delegate.updateElement(data);
            }      
            
		}
		
		override public function result(data:Object):void
		{
			if (data != null) {
                log.debug("Role successfully updated");
                channelsModel.getElementModel((data as String)).dirty = false;
            } else {
                log.error("Update of Role " + result + " failed");
            }
		}
	}
}