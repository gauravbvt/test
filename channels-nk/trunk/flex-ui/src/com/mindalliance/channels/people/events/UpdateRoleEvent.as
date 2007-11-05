// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.people.events
{
	import com.mindalliance.channels.common.events.UpdateElementEvent;
	import com.mindalliance.channels.model.EditorModel;
	import com.mindalliance.channels.vo.common.CategorySetVO;
	import com.mindalliance.channels.vo.common.ElementVO;
	
	import mx.collections.ArrayCollection;

	public class UpdateRoleEvent extends UpdateElementEvent
	{

		public function UpdateRoleEvent(model : EditorModel,
                                        name : String, 
                                        description : String,
                                        categories : CategorySetVO, 
                                        organization : ElementVO,
                                        expertise : ArrayCollection ) 
		{
			super( model,{
			"name" : name,
			"description" : description,
			"categories" : categories,
			"organization" : organization,
			"expertise" : expertise
			});
		}
	}
}