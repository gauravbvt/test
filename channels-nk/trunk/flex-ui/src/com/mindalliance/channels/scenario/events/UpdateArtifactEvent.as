// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.scenario.events
{
	import com.mindalliance.channels.common.events.UpdateElementEvent;
	import com.mindalliance.channels.model.EditorModel;
	import com.mindalliance.channels.vo.common.CategorySetVO;
	import com.mindalliance.channels.vo.common.ElementVO;

	public class UpdateArtifactEvent extends UpdateElementEvent
	{

		public function UpdateArtifactEvent(model : EditorModel,
                                    name : String, 
                                    description : String,
                                    categories : CategorySetVO,
                                    product : ElementVO) 
		{
			super( model, {
			    "name" : name,
			    "description" : description,
			    "categories" : categories,
			    "product" : product
			});
		}
	}
}