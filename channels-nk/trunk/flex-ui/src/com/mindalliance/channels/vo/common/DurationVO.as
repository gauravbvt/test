// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.vo.common
{
	import com.adobe.cairngorm.vo.IValueObject;

    [Bindable]
	public class DurationVO implements IValueObject
	{
		public function DurationVO( value : Number, unit : String ) {
			this.value = value;
			this.unit = unit;
		}

		private var _value : Number;
		private var _unit : String;

		public function get value() : Number {
			return _value;
		};

		public function set value(value : Number) : void {
			_value=value;
		}
		
		public function get unit() : String {
			return _unit;
		};

		public function set unit(unit : String) : void {
			_unit=unit;
		}
		
		public static const UNIT_WEEK : String = "week";
		public static const UNIT_DAY : String = "day";
		public static const UNIT_HOUR : String = "hour";
		public static const UNIT_MINUTE : String = "minute";
		public static const UNIT_SECOND : String = "second";
		
	}
}