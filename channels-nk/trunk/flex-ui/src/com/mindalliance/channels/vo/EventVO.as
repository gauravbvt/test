// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.vo
{
	import com.adobe.cairngorm.vo.IValueObject;
	import com.mindalliance.channels.vo.common.DurationVO;
	import com.mindalliance.channels.vo.common.OccurrenceVO;
	
	import mx.collections.ArrayCollection;

	public class EventVO extends OccurrenceVO implements IValueObject
	{
		public function EventVO( id : String, 
								name : String, 
								description : String,
								categories : ArrayCollection,
                                scenario : ScenarioVO,
								duration : DurationVO,
								taskCompletions : ArrayCollection) {
            super(id,name,description,categories,where,cause);
            this.duration = duration;
            this.taskCompletions = taskCompletions;
            this.scenario = scenario;
		}
        private var _duration : DurationVO;
        private var _taskCompletions : ArrayCollection;
        private var _scenario : ScenarioVO;
		
		public function get duration() : DurationVO {
			return _duration;
		}

		public function set duration(duration : DurationVO) : void {
			_duration=duration;
		}
		
		public function get taskCompletions() : ArrayCollection {
			return _taskCompletions;
		}

		public function set taskCompletions(taskCompletions : ArrayCollection) : void {
			_taskCompletions=taskCompletions;
		}
		
		public function get scenario() : ElementVO {
			return _scenario;
		}

		public function set scenario(scenario : ElementVO) : void {
			_scenario=scenario;
		}
		
        
	}
}