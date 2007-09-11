// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.commands.people
{
	import com.adobe.cairngorm.control.CairngormEvent;
	import com.mindalliance.channels.business.people.OrganizationDelegate;
	import com.mindalliance.channels.commands.BaseDelegateCommand;
	import com.mindalliance.channels.events.people.UpdateOrganizationEvent;
	import com.mindalliance.channels.model.*;
	import com.mindalliance.channels.util.ElementHelper;
	import com.mindalliance.channels.vo.OrganizationVO;
	import com.mindalliance.channels.vo.common.ElementVO;
	
	public class UpdateOrganizationCommand extends BaseDelegateCommand
	{
	
		override public function execute(event:CairngormEvent):void
		{ 
            var evt:UpdateOrganizationEvent = event as UpdateOrganizationEvent;
            var model : EditorModel = evt.model;
            var element :ElementModel = model.getElementModel(model.id);
            var data : OrganizationVO = (element.data as OrganizationVO);	
			if (model.isChanged) {
				log.debug("Updating Organization model");

				data.name=evt.name;
				data.description = evt.description;
                data.abbreviation = evt.abbreviation;
                data.address = evt.address;
                data.parent = evt.parent;
                
				model.isChanged = false;
				element.dirty=true;
			}
			if (element.dirty == true) {
				log.debug("Updating Organization");
                var delegate:OrganizationDelegate = new OrganizationDelegate( this );
                delegate.updateElement(data);
            }        
			
		}
		
		override public function result(data:Object):void
		{
			if (data["data"]==true) {
                log.debug("Organization successfully updated");
                var id : String = (data["id"] as String);
			    channelsModel.getElementModel((data["id"] as String)).dirty = false;
			
			    var el : ElementVO = ElementHelper.findElementById(id, channelsModel.getElementListModel('organizations').data);
                el.name = channelsModel.getElementModel(id).data.name;
			} else {
                log.error("Update of organization " + data["id"] + " failed");
			}
			
		}
	}
}