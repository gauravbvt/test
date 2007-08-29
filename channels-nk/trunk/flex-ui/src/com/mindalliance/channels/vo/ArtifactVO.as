// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.vo
{
	import com.adobe.cairngorm.vo.IValueObject;
	import com.mindalliance.channels.vo.common.ProductVO;
	
	import mx.collections.ArrayCollection;

	public class ArtifactVO extends ProductVO implements IValueObject
	{
		public function ArtifactVO( id : String, 
                                    name : String, 
                                    description : String,
                                    categories : CategorySetVO,
                                    product : ArrayCollection ) {
			this.id = id;
			this.name = name;
			this.description = description;
			this.categories =categories;
			this.product = product;
		}
	}
}