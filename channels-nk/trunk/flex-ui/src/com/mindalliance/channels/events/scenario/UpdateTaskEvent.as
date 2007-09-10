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

	public class UpdateTaskEvent extends CairngormEvent
	{
		public static const UpdateTask_Event:String = "<UpdateTaskEvent>";
		public var model : EditorModel;
		public var name : String; 
        public var description : String;
        public var categories : CategorySetVO;
        public var where : ISpatial;
        public var cause : CauseVO;
        public var duration : DurationVO;
        public var scenario : ElementVO;
		public function UpdateTaskEvent(model : EditorModel,
		                        name : String, 
                                description : String,
                                categories : CategorySetVO,
                                where : ISpatial,
                                cause : CauseVO,
                                duration : DurationVO,
                                scenario : ElementVO) 
		{
			super( UpdateTask_Event );
			this.model = model;
			this.name = name;
			this.description = description;
			this.categories = categories;
			this.where = where;
			this.cause = cause;
			this.duration = duration;
			this.scenario = scenario;
		}
	}
}