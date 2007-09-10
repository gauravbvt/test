package com.mindalliance.channels.vo.common
{
	import com.adobe.cairngorm.vo.IValueObject;
    [Bindable]
	public class SpatialCoordinatesVO implements IValueObject
	{
		public function SpatialCoordinatesVO(spatial : ISpatial) {
			_spatial = spatial;
		}
		
		private var _spatial : ISpatial;
		
		
		public function get spatial() : ISpatial {
			return _spatial;
		}

		public function set spatial(spatial : ISpatial) : void {
			_spatial=spatial;
		}
		
		
		
	}
}