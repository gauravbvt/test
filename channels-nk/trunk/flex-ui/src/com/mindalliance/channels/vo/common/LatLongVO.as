package com.mindalliance.channels.vo.common
{
	import com.adobe.cairngorm.vo.IValueObject;

	public class LatLongVO implements IValueObject, ISpatial
	{
		public function LatLongVO(lat : Number, long : Number) {
			this._latitude = lat;
			this._longitude = long;
		}
		private var _latitude : Number;
		private var _longitude : Number;
		
		
		public function get latitude() : Number {
			return _latitude;
		}

		public function set latitude(latitude : Number) : void {
			_latitude=latitude;
		}
		
		public function get longitude() : Number {
			return _longitude;
		}

		public function set longitude(longitude : Number) : void {
			_longitude=longitude;
		}
		
	}
}