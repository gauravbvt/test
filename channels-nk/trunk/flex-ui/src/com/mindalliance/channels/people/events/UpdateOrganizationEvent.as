// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.people.events
{
	import com.mindalliance.channels.common.events.UpdateElementEvent;
	import com.mindalliance.channels.model.EditorModel;
	import com.mindalliance.channels.vo.common.AddressVO;
	import com.mindalliance.channels.vo.common.ElementVO;

	public class UpdateOrganizationEvent extends UpdateElementEvent
	{
        
		public function UpdateOrganizationEvent(model : EditorModel,
		                                        name : String,
                                                description : String,
                                                abbreviation : String,
                                                address: AddressVO,
                                                parent : ElementVO) 
		{
			super(model,{
				"name" : name,
				"description" : description,
				"abbreviation" : abbreviation,
				"address" : address,
				"parent" : parent
			});
		}
	}
}