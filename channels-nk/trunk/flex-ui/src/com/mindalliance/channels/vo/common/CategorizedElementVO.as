// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.vo.common
{
	import com.adobe.cairngorm.vo.IValueObject;
	
	import mx.collections.ArrayCollection;
	import com.mindalliance.channels.vo.common.CategorySetVO;
	[Bindable]
	public class CategorizedElementVO extends ElementVO implements IValueObject
	{
		public function CategorizedElementVO( id : String, 
								name : String, 
								description : String,
								categories : CategorySetVO
								 ) {

			super(id, name, description);
			this.categories = categories;
		}

		private var _categories : CategorySetVO;
		
		public function get categories() : CategorySetVO {
			return _categories;
		}

		public function set categories(categories : CategorySetVO) : void {
			_categories=categories;
		}
	
	}
}