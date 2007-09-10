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

	public class UpdateEventEvent extends CairngormEvent
	{
		public static const UpdateEvent_Event:String = "<UpdateEventEvent>";
		
		public var model : EditorModel;
        public var name : String;
        public var description : String;
        public var categories : CategorySetVO;
        public var where : ISpatial;
        public var cause : CauseVO;
        public var scenario : ElementVO;
        public var duration : DurationVO;
        public var taskCompletions : ArrayCollection;
        
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
			super( UpdateEvent_Event );
		    this.model = model;
		    this.name = name;
		    this.description = description;
		    this.categories = categories;
		    this.where = where;
		    this.cause = cause;
		    this.scenario = scenario;
		    this.duration = duration;
		    this.taskCompletions = taskCompletions;
		}
	}
}