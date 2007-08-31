// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.events.people
{
	import com.adobe.cairngorm.control.CairngormEvent;
	import com.mindalliance.channels.model.EditorModel;
    import com.mindalliance.channels.vo.common.ElementVO;
    import com.mindalliance.channels.vo.common.AddressVO;

	public class UpdateOrganizationEvent extends CairngormEvent
	{
		public static const UpdateOrganization_Event:String = "<UpdateOrganizationEvent>";
		public var model : EditorModel;
        public var name : String;
        public var description : String;
        public var abbreviation : String;
        public var address: AddressVO;
        public var parent : ElementVO;
        
		public function UpdateOrganizationEvent(model : EditorModel,
		                                        name : String,
                                                description : String,
                                                abbreviation : String,
                                                address: AddressVO,
                                                parent : ElementVO) 
		{
			super( UpdateOrganization_Event );
			this.model = model;
			this.name = name;
			this.description = description;
			this.abbreviation = abbreviation;
			this.address = address;
			this.parent = parent;
		}
	}
}