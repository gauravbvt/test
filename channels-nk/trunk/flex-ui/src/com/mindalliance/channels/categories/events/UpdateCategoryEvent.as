// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.categories.events
{
	import com.mindalliance.channels.common.events.UpdateElementEvent;
	import com.mindalliance.channels.model.EditorModel;
	import com.mindalliance.channels.vo.common.InformationVO;
	
	import mx.collections.ArrayCollection;

	public class UpdateCategoryEvent extends UpdateElementEvent
	{
		public function UpdateCategoryEvent(model : EditorModel,
		                                  name : String, 
                                description : String,
                                taxonomy : String,
                                disciplines : ArrayCollection,
                                implies : ArrayCollection,
                                information : InformationVO) 
		{
			super(model, {
			"name" : name,
			"description" : description,
			"taxonomy" : taxonomy,
			"disciplines" : disciplines,
			"implies" : implies,
			"information" : information
			});
		}
	}
}