// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.vo
{
	import com.adobe.cairngorm.vo.IValueObject;
	import com.mindalliance.channels.vo.common.CategorySetVO;
	import com.mindalliance.channels.vo.common.ProductVO;
	
	import mx.collections.ArrayCollection;

	public class AcquirementVO extends ProductVO implements IValueObject
	{
		public function AcquirementVO( id : String, 
                                    name : String, 
                                    description : String,
                                    categories : CategorySetVO,
                                    product : ArrayCollection ) {
            super(id,name,description,categories,product);
		}
	}
}