// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.commands.people
{
    import com.adobe.cairngorm.control.CairngormEvent;
    import com.mindalliance.channels.business.people.PersonDelegate;
    import com.mindalliance.channels.commands.BaseDelegateCommand;
    import com.mindalliance.channels.events.people.UpdatePersonEvent;
    import com.mindalliance.channels.model.*;
    import com.mindalliance.channels.vo.PersonVO;
	
	public class UpdatePersonCommand extends BaseDelegateCommand
	{
	
		override public function execute(event:CairngormEvent):void
		{
			var evt:UpdatePersonEvent = event as UpdatePersonEvent;
			var model : EditorModel = evt.model;
            var element :ElementModel = model.getElementModel(model.id);
            var data : PersonVO = (element.data as PersonVO);   
            if (model.isChanged) {
                log.debug("Updating Person model");
                data.firstName = evt.firstName;
                data.lastName = evt.lastName;
                data.photo = evt.photo;
                data.email = evt.email;
                data.officePhone = evt.officePhone;
                data.cellPhone = evt.cellPhone;
                data.address = evt.address;
                data.roles = evt.roles;
                data.user = evt.user;
                
                model.isChanged = false;
                element.dirty=true;
            }
            if (element.dirty == true) {
                log.debug("Updating Person");
                var delegate:PersonDelegate = new PersonDelegate( this );
                delegate.updateElement(data);
            }   
		}
		
		override public function result(data:Object):void
		{
			if (data["data"]==true) {
                log.debug("Person successfully updated");
                channelsModel.getElementModel((data["id"] as String)).dirty = false;
            } else {
                log.error("Update of person " + result + " failed");
            }
		}
	}
}