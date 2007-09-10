// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.vo.common
{
	import com.adobe.cairngorm.vo.IValueObject;

    [Bindable]
	public class CauseVO implements IValueObject
	{
		private var _type : String;
		private var _id : String;
		private var _duration : DurationVO;
		private var _from : String;

		public function CauseVO( type : String, id : String, duration : DurationVO, from : String ) {
			this.type = type;
			this.id = id; 
            this.duration = duration;
            this.from = from;
		}
		
		public function get type() : String {
			return _type;
		}

		public function set type(type : String) : void {
			_type=type;
		}
		
		public function get id() : String {
			return _id;
		}

		public function set id(id : String) : void {
			_id=id;
		}
		
		public function get duration() : DurationVO {
			return _duration;
		}

		public function set duration(duration : DurationVO) : void {
			_duration=duration;
		}
		public function get from() : String {
            return _from;
        }

        public function set from(from : String) : void {
            _from=from;
        }
        
		public static const CAUSE_EVENT : String = "event";
		public static const CAUSE_TASK : String = "task";
		
		public static const FROM_START : String = "start";
		public static const FROM_END : String = "end";
		
	}
}