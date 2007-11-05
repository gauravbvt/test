// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.resources.events
{
	import com.mindalliance.channels.common.events.UpdateElementEvent;
	import com.mindalliance.channels.model.EditorModel;
	import com.mindalliance.channels.vo.common.CategorySetVO;
	import com.mindalliance.channels.vo.common.ElementVO;
	
	import mx.collections.ArrayCollection;

	public class UpdateRepositoryEvent extends UpdateElementEvent
	{

        
		public function UpdateRepositoryEvent(model : EditorModel,                      
		                      name : String, 
                                description : String,
                                categories : CategorySetVO,
                                organization : ElementVO,
                                administrators : ArrayCollection,
                                contents : ArrayCollection,
                                access : ArrayCollection) 
		{
			super( model, {
			"name" : name,
			"description" : description,
			"categories" : categories,
			"organization" : organization,
			"administrators" : administrators,
			"contents" : contents,
			"access" : access
			});
		}
	}
}