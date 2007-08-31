// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.vo
{
	import com.adobe.cairngorm.vo.IValueObject;
	import com.mindalliance.channels.vo.common.DurationVO;
	
	import mx.collections.ArrayCollection;

	public class TaskVO extends OccurrenceVO implements IValueObject
	{
		public function TaskVO( id : String, 
                                name : String, 
                                description : String,
                                categories : CategorySetVO,
                                where : ISpatial = null,
                                cause : CauseVO = null,
                                duration : DurationVO,
                                scenario : ElementVO) {
                                
			super(id,name,description,categories,where,cause);
			this.duration = duration;
			this.scenario = scenario;
		}

		private var _duration : DurationVO;
        private var _scenario : ElementVO;
        
		public function get duration() : DurationVO {
			return _duration;
		}

		public function set duration(duration : DurationVO) : void {
			_duration=duration;
		}
		
		public function get scenario() : ElementVO {
			return _scenario;
		}

		public function set scenario(scenario : ElementVO) : void {
			_scenario=scenario;
		}
		
	}
}