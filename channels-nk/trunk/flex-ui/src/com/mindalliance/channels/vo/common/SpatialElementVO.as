package com.mindalliance.channels.vo.common
{
	public class SpatialElementVO extends ElementVO implements ISpatial
	{
		public function SpatialElementVO( id : String,
                                   name : String,
                                   description : String = null) {
            super(id, name, description);
        }
	}
}