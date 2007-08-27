// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.commands.people
{
	import com.adobe.cairngorm.control.CairngormEvent;
	import com.mindalliance.channels.business.people.OrganizationDelegate;
	import com.mindalliance.channels.events.people.UpdateOrganizationEvent;
	import com.mindalliance.channels.commands.BaseDelegateCommand;
	
	public class UpdateOrganizationCommand extends BaseDelegateCommand
	{
	
		override public function execute(event:CairngormEvent):void
		{
			if (model.propertyEditorModel.shouldUpdateOrganization) {
				log.debug("Updating Organization");
				var evt:UpdateOrganizationEvent = event as UpdateOrganizationEvent;
				
				var delegate:OrganizationDelegate = new OrganizationDelegate( this );
				
				delegate.updateElement(model.propertyEditorModel.organization);
			}
		}
		
		override public function result(data:Object):void
		{
			log.debug("Organization successfully updated");
			
			model.propertyEditorModel.shouldUpdateOrganization = false;
			
		}
	}
}