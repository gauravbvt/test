// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.vo
{
	import com.adobe.cairngorm.vo.IValueObject;
	import com.mindalliance.channels.vo.common.CategorizedElementVO;
	import com.mindalliance.channels.vo.common.SpatialCoordinatesVO;
	
	import mx.collections.ArrayCollection;

	public class LocationVO extends CategorizedElementVO implements IValueObject
	{
		public function LocationVO( id : String, 
								name : String, 
								description : String,
								categories : ArrayCollection,
								within : ArrayCollection,
								nextTo : ArrayCollection,
								spatialCoordinates : SpatialCoordinatesVO ) {
			super(id, name, description, categories);
			this.within = within;
			this.nextTo = nextTo;
			this.spatialCoordinates = spatialCoordinates;
			
		}
        private var _within : ArrayCollection;
        private var _nextTo : ArrayCollection;
        private var _spatialCoordinates : SpatialCoordinatesVO;

		
		public function get within() : ArrayCollection {
			return _within;
		}

		public function set within(within : ArrayCollection) : void {
			_within=within;
		}
		
		public function get nextTo() : ArrayCollection {
			return _nextTo;
		}

		public function set nextTo(nextTo : ArrayCollection) : void {
			_nextTo=nextTo;
		}
		
		public function get spatialCoordinates() : SpatialCoordinatesVO {
			return _spatialCoordinates;
		}

		public function set spatialCoordinates(spatialCoordinates : SpatialCoordinatesVO) : void {
			_spatialCoordinates=spatialCoordinates;
		}
		
	}
}