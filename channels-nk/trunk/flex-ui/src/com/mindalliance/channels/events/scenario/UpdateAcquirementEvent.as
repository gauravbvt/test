// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.events.scenario
{
	import com.adobe.cairngorm.control.CairngormEvent;
	import com.mindalliance.channels.model.EditorModel;
	import com.mindalliance.channels.vo.common.CategorySetVO;
	import com.mindalliance.channels.vo.common.CauseVO;
	import com.mindalliance.channels.vo.common.DurationVO;
	import com.mindalliance.channels.vo.common.ElementVO;
	import com.mindalliance.channels.vo.common.ISpatial;
	
	import mx.collections.ArrayCollection;

	public class UpdateAcquirementEvent extends CairngormEvent
	{
		public static const UpdateAcquirement_Event:String = "<UpdateAcquirementEvent>";
		
		public var model : EditorModel;
        public var name : String;
        public var description : String;
        public var categories : CategorySetVO;
        public var product : ArrayCollection ;
        
		public function UpdateAcquirementEvent(model : EditorModel,
                                name : String, 
                                description : String,
                                categories : CategorySetVO,
                                product : ArrayCollection) 
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