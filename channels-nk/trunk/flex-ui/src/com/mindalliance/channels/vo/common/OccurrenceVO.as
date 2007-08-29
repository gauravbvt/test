// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.vo.common
{
	import com.adobe.cairngorm.vo.IValueObject;
	import com.mindalliance.channels.vo.common.*;
	
	import mx.collections.ArrayCollection;

	public class OccurrenceVO extends CategorizedElementVO implements IValueObject
	{
		public function OccurrenceVO( id : String, 
								name : String, 
								description : String,
								categories : CategorySetVO,
								where : ISpatial = null,
								cause : CauseVO = null) {
			super(id,name,description,categories);
			_cause = cause;
            _where = where;
		}

		private var _cause : CauseVO;
		private var _spatial : ISpatial;

		public function get cause() : CauseVO {
			return _cause;
		};

		public function set cause(cause : CauseVO) : void {
			_cause=cause;
		}
        
        public function get where() : ISpatial {
            return _spatial;
        }

        public function set where(spatial : ISpatial) : void {
            _spatial=spatial;
        }
        
	}
}