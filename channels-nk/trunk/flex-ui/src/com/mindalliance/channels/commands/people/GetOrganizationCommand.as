// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.commands.people
{
	import com.adobe.cairngorm.control.CairngormEvent;
	import com.mindalliance.channels.business.people.OrganizationDelegate;
	import com.mindalliance.channels.events.people.GetOrganizationEvent;
	import com.mindalliance.channels.commands.BaseDelegateCommand;
	import com.mindalliance.channels.vo.OrganizationVO;
		
	public class GetOrganizationCommand extends BaseDelegateCommand
	{
	
		override public function execute(event:CairngormEvent):void
		{
			var evt:GetOrganizationEvent = event as GetOrganizationEvent;			
			var id : String = evt.id;
			
			if (id != null) {
				log.debug("Retrieving Organization {0}", [id]);
				var delegate:OrganizationDelegate = new OrganizationDelegate( this );
				delegate.getElement(id);
			} else {
				log.debug("Setting selected Organization to null");
				model.organizationEditorModel.organization = null;
			}
		}
		
		override public function result(data:Object):void
		{
			var result:OrganizationVO = (data as OrganizationVO);
			if (result != null) {
				log.debug("Setting selected Organization to {0}", [result.id]);
				model.organizationEditorModel.organization = result;// new OrganizationVO(result.id, result.name, result.description, result.manager);
			} else {
				log.warn("Unable to retrieve Organization");	
			}
		}
	}
}