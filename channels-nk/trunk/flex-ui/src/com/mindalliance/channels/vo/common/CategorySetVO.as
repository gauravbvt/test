package com.mindalliance.channels.vo.common
{
	import com.adobe.cairngorm.vo.IValueObject;
	import mx.collections.ArrayCollection;
	import com.mindalliance.channels.util.XMLHelper;
	import com.mindalliance.channels.vo.common.ElementVO;
	[Bindable]
	public class CategorySetVO implements IValueObject
	{
		private var _atMostOne : Boolean;
		private var _taxonomy : String;
		private var _categories : ArrayCollection;
		
		public function CategorySetVO(taxonomy : String, categories : ArrayCollection, atMostOne : Boolean = false) {
			this._atMostOne = atMostOne;
			this._taxonomy = taxonomy;
			this._categories = categories;	
		}
		
		
		public function get atMostOne() : Boolean {
			return _atMostOne;
		}

		public function set atMostOne(atMostOne : Boolean) : void {
			_atMostOne=atMostOne;
		}
		
		public function get taxonomy() : String {
			return _taxonomy;
		}

		public function set taxonomy(taxonomy : String) : void {
			_taxonomy=taxonomy;
		}
		
		public function get categories() : ArrayCollection {
			return _categories;
		}

		public function set categories(categories : ArrayCollection) : void {
			_categories=categories;
		}
		
	}
}