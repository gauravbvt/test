// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.vo
{
	import com.adobe.cairngorm.vo.IValueObject;
	import com.mindalliance.channels.vo.common.CategorySetVO;
	import com.mindalliance.channels.vo.common.CauseVO;
	import com.mindalliance.channels.vo.common.DurationVO;
	import com.mindalliance.channels.vo.common.ElementVO;
	import com.mindalliance.channels.vo.common.ISpatial;
	import com.mindalliance.channels.vo.common.OccurrenceVO;
    [Bindable]
	public class TaskVO extends OccurrenceVO implements IValueObject
	{
		public function TaskVO( id : String, 
                                name : String, 
                                description : String,
                                categories : CategorySetVO,
                                where : ISpatial,
                                cause : CauseVO,
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