// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.people.events
{
	import com.mindalliance.channels.common.events.UpdateElementEvent;
	import com.mindalliance.channels.model.EditorModel;
	import com.mindalliance.channels.vo.common.AddressVO;
	import com.mindalliance.channels.vo.common.ElementVO;
	
	import mx.collections.ArrayCollection;

	public class UpdatePersonEvent extends UpdateElementEvent
	{

		
		public function UpdatePersonEvent(model : EditorModel,
		                        firstName : String,
                                lastName : String,
                                photo : String,
                                email : String,
                                officePhone : String,
                                cellPhone : String,
                                address : AddressVO,
                                roles : ArrayCollection,
                                user : ElementVO) 
		{
			super( model, {
	            "firstName" : firstName,
	            "lastName" : lastName,
	            "photo" : photo,
	            "email" : email,
	            "officePhone" : officePhone,
	            "cellPhone" : cellPhone,
	            "address" : address,
	            "roles" : roles,
	            "user" : user
            });
		}
	}
}