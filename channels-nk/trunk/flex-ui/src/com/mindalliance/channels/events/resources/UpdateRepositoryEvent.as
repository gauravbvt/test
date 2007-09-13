// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.events.resources
{
	import com.adobe.cairngorm.control.CairngormEvent;
	import com.mindalliance.channels.model.EditorModel;
	import com.mindalliance.channels.vo.common.CategorySetVO;
	import com.mindalliance.channels.vo.common.ElementVO;
	
	import mx.collections.ArrayCollection;

	public class UpdateRepositoryEvent extends CairngormEvent
	{
		public static const UpdateRepository_Event:String = "<UpdateRepositoryEvent>";
		
		public var model : EditorModel;
		public var name : String;
        public var description : String;
        public var categories : CategorySetVO;
        public var organization : ElementVO;
        public var administrators : ArrayCollection;
        public var contents : ArrayCollection;
        public var access : ArrayCollection;
        
		public function UpdateRepositoryEvent(model : EditorModel,                      
		                      name : String, 
                                description : String,
                                categories : CategorySetVO,
                                organization : ElementVO,
                                administrators : ArrayCollection,
                                contents : ArrayCollection,
                                access : ArrayCollection) 
		{
			super( UpdateRepository_Event );
			this.model = model;
			this.name = name;
			this.description = description;
			this.categories = categories;
			this.organization = organization;
			this.administrators = administrators;
			this.contents = contents;
			this.access = access;
		}
	}
}