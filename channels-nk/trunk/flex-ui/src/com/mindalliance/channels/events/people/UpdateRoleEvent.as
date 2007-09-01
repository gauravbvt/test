// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.events.people
{
	import com.adobe.cairngorm.control.CairngormEvent;
	import com.mindalliance.channels.model.EditorModel;
	import com.mindalliance.channels.vo.common.CategorySetVO;
	import com.mindalliance.channels.vo.common.ElementVO;
	
	import mx.collections.ArrayCollection;

	public class UpdateRoleEvent extends CairngormEvent
	{
		public static const UpdateRole_Event:String = "<UpdateRoleEvent>";
		public var model : EditorModel;
        public var name : String;
        public var description : String;
        public var categories : CategorySetVO 
        public var organization : ElementVO;
        public var expertise : ArrayCollection;
		public function UpdateRoleEvent(model : EditorModel,
                                        name : String, 
                                        description : String,
                                        categories : CategorySetVO, 
                                        organization : ElementVO,
                                        expertise : ArrayCollection ) 
		{
			super( UpdateRole_Event );
			this.model = model;
			this.name = name;
			this.description = description;
			this.categories = categories;
			this.organization = organization;
			this.expertise = expertise;
		}
	}
}