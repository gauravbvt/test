// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.commands.people
{
	import com.adobe.cairngorm.control.CairngormEvent;
	import com.mindalliance.channels.business.people.RoleDelegate;
	import com.mindalliance.channels.commands.BaseDelegateCommand;
	import com.mindalliance.channels.events.people.*;
	import com.mindalliance.channels.model.*;
	import com.mindalliance.channels.util.ElementHelper;
	import com.mindalliance.channels.vo.RoleVO;
	import com.mindalliance.channels.vo.common.ElementVO;
	
	public class UpdateRoleCommand extends BaseDelegateCommand
	{
	
		override public function execute(event:CairngormEvent):void
		{
			var evt:UpdateRoleEvent = event as UpdateRoleEvent;
			
			var model : EditorModel = evt.model;
            var element :ElementModel = model.getElementModel(model.id);
            var data : RoleVO = (element.data as RoleVO);   
            if (model.isChanged) {
                log.debug("Updating Role model");

                data.name=evt.name;
                data.description = evt.description;
                data.categories = evt.categories; 
                data.organization = evt.organization;
                data.expertise = evt.expertise;
                
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
			if (data["data"] == true) {
                log.debug("Role successfully updated");
                var id : String = (data["id"] as String);
                channelsModel.getElementModel(id).dirty = false;
                                // Update the element name in the role list
                var el : ElementVO = ElementHelper.findElementById(id, channelsModel.getElementListModel('roles').data);
                el.name = channelsModel.getElementModel(id).data.name;
            } else {
                log.error("Update of Role " + result + " failed");
            }
		}
	}
}