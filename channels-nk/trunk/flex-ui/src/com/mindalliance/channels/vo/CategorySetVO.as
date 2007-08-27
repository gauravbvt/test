package com.mindalliance.channels.vo
{
	import com.adobe.cairngorm.vo.IValueObject;
	import mx.collections.ArrayCollection;
	import com.mindalliance.channels.util.XMLHelper;
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
		
		public static function fromXML(obj : Object) : CategorySetVO {
			return new CategorySetVO(obj.@taxonomy, XMLHelper.fromIdList("categoryId", obj.categoryId), obj.@atMostOne);
		}
		
		public static function toXML(obj : CategorySetVO) : XML {
			var xml : XML = <categories atMostOne="{obj.atMostOne}" taxonomy="{obj.taxonomy}"></categories>
			
			for each (var element:ElementVO in obj.categories) {
				xml.appendChild(<categoryId>{element.id}</categoryId>);
			}	
			return xml;		
		}
		
	}
}