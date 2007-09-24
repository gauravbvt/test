// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.events.scenario
{
	import com.adobe.cairngorm.control.CairngormEvent;
	import com.mindalliance.channels.model.EditorModel;
	import com.mindalliance.channels.vo.common.CategorySetVO;
	import com.mindalliance.channels.vo.common.ElementVO;
	
	import mx.collections.ArrayCollection;

	public class UpdateAcquirementEvent extends CairngormEvent
	{
		public static const UpdateAcquirement_Event:String = "<UpdateAcquirementEvent>";
		
		public var model : EditorModel;
        public var name : String;
        public var description : String;
        public var categories : CategorySetVO;
        public var product : ElementVO ;
        
		public function UpdateAcquirementEvent(model : EditorModel,
                                name : String, 
                                description : String,
                                categories : CategorySetVO,
                                product : ElementVO) 
		{
			super( UpdateAcquirement_Event );
		    this.model = model;
		    this.name = name;
		    this.description = description;
		    this.categories = categories;
		    this.product = product ;
		}
	}
}