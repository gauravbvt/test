// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.scenario.events
{
	import com.mindalliance.channels.common.events.UpdateElementEvent;
	import com.mindalliance.channels.model.EditorModel;
	import com.mindalliance.channels.vo.common.CategorySetVO;
	import com.mindalliance.channels.vo.common.CauseVO;
	import com.mindalliance.channels.vo.common.DurationVO;
	import com.mindalliance.channels.vo.common.ElementVO;
	import com.mindalliance.channels.vo.common.ISpatial;
	
	import mx.collections.ArrayCollection;

	public class UpdateEventEvent extends UpdateElementEvent
	{
        
		public function UpdateEventEvent(model : EditorModel,
                                name : String, 
                                description : String,
                                categories : CategorySetVO,
                                where : ISpatial,
                                cause : CauseVO,
                                scenario : ElementVO,
                                duration : DurationVO,
                                taskCompletions : ArrayCollection) 
		{
			super( model, {
		        "name" : name,
		        "description" : description,
		        "categories" : categories,
		        "where" : where,
		        "cause" : cause,
		        "scenario" : scenario,
		        "duration" : duration,
		        "taskCompletions" : taskCompletions
		    });
		}
	}
}