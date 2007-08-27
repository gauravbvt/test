// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.commands.people
{
	import com.adobe.cairngorm.control.CairngormEvent;
	import com.adobe.cairngorm.control.CairngormEventDispatcher;
	import com.mindalliance.channels.business.people.OrganizationDelegate;
	import com.mindalliance.channels.events.people.GetOrganizationListEvent;
	import com.mindalliance.channels.commands.BaseDelegateCommand;
	import mx.collections.ArrayCollection;
	
	public class GetOrganizationListCommand extends BaseDelegateCommand
	{
	
		override public function execute(event:CairngormEvent):void
		{
			var evt:GetOrganizationListEvent = event as GetOrganizationListEvent;
			var delegate:OrganizationDelegate = new OrganizationDelegate( this );
			log.debug("Retrieving Organization list");
			delegate.getOrganizationList();
		}
		
		override public function result(data:Object):void
		{
			model.propertyEditorModel.organizationList = (data as ArrayCollection);
			log.debug("Successfully retrieved Organization list");
		}
		
		override public function fault(info:Object):void
		{
			model.propertyEditorModel.organizationList = null;
			super.fault(info);
		}
	}
}