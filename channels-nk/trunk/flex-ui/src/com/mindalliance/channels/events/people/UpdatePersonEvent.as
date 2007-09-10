// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.events.people
{
	import com.adobe.cairngorm.control.CairngormEvent;
	import com.mindalliance.channels.model.EditorModel;
	import com.mindalliance.channels.vo.common.AddressVO;
	import com.mindalliance.channels.vo.common.ElementVO;
	
	import mx.collections.ArrayCollection;

	public class UpdatePersonEvent extends CairngormEvent
	{
		public static const UpdatePerson_Event:String = "<UpdatePersonEvent>";
		public var model : EditorModel;
        public var firstName : String;
        public var lastName : String;
        public var photo : String;
        public var email : String;
        public var officePhone : String;
        public var cellPhone : String;
        public var address : AddressVO;
        public var roles : ArrayCollection;
        public var user : ElementVO;
		
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
			super( UpdatePerson_Event );
			this.model = model;
            this.firstName = firstName;
            this.lastName = lastName;
            this.photo = photo;
            this.email = email;
            this.officePhone = officePhone;
            this.cellPhone = cellPhone;
            this.address = address;
            this.roles = roles;
            this.user = user;
		}
	}
}