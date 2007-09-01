// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.commands.people
{
	import com.adobe.cairngorm.control.CairngormEvent;
	import com.mindalliance.channels.business.people.OrganizationDelegate;
	import com.mindalliance.channels.commands.BaseDelegateCommand;
	import com.mindalliance.channels.events.people.GetOrganizationEvent;
    import com.mindalliance.channels.model.EditorModel;
    import com.mindalliance.channels.model.ElementModel;
	import com.mindalliance.channels.vo.OrganizationVO;
		
	public class GetOrganizationCommand extends BaseDelegateCommand
	{
	
		override public function execute(event:CairngormEvent):void
		{
			var evt:GetOrganizationEvent = event as GetOrganizationEvent;			
			var id : String = evt.id;
			var model : EditorModel = evt.model;
			model.id = id;
			if (id != null) {
				log.debug("Retrieving Organization {0}", [id]);
				var delegate:OrganizationDelegate = new OrganizationDelegate( this );
				delegate.getElement(id);
			}
		}
		
		override public function result(data:Object):void
		{
			var result:OrganizationVO = (data as OrganizationVO);
			if (result != null) {
				log.debug("Setting selected Organization to {0}", [result.id]);
                var model : ElementModel = channelsModel.getElementModel(result.id);
                model.data = result;
			} else {
				log.warn("Unable to retrieve Organization");	
			}
		}
	}
}