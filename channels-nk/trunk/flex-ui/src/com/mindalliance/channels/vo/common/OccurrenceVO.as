// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.vo.common
{
	import com.adobe.cairngorm.vo.IValueObject;
    [Bindable]
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
		private var _where : ISpatial;

		public function get cause() : CauseVO {
			return _cause;
		};

		public function set cause(cause : CauseVO) : void {
			_cause=cause;
		}
        
        public function get where() : ISpatial {
            return _where;
        }

        public function set where(spatial : ISpatial) : void {
            _where=spatial;
        }
        
	}
}