// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.events.scenario
{
	import com.adobe.cairngorm.control.CairngormEvent;
	import com.mindalliance.channels.model.EditorModel;
	import com.mindalliance.channels.vo.common.CategorySetVO;
	
	import mx.collections.ArrayCollection;

	public class UpdateArtifactEvent extends CairngormEvent
	{
		public static const UpdateArtifact_Event:String = "<UpdateArtifactEvent>";
		public var model : EditorModel;
		public var name : String; 
        public var description : String;
        public var categories : CategorySetVO;
        public var product : ArrayCollection;
		public function UpdateArtifactEvent(model : EditorModel,
                                    name : String, 
                                    description : String,
                                    categories : CategorySetVO,
                                    product : ArrayCollection) 
		{
			super( UpdateArtifact_Event );
			this.model = model;
			this.name = name;
			this.description = description;
			this.categories = categories;
			this.product = product;
		}
	}
}