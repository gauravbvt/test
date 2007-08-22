
package com.mindalliance.channels.vo
{
	import com.adobe.cairngorm.vo.IValueObject;
	import mx.collections.ArrayCollection;
	
	[Bindable]
	public class ElementVO implements IValueObject
	{
		public function ElementVO( id : String = "0",
		                           name : String = "" ) {
		    this._id = id;
			this._name = name;
		}
		private var _id:String;
		private var _name:String;
		private var _description:String;
		
		public function get id () : String {
			return _id;	
		}
		
		public function set id (id:String) : void {
			this._id = id;
		}
		
		public function get name() : String {
			return _name;	
		}
		
		public function set name(name : String) : void {
			this._name = name;	
		}
		
		public function get description() : String {
			return _description;	
		}
		
		public function set description(description : String) : void {
			this._description = description;	
		}
		
		protected static function fromXMLList(key : String, results : Object) : ArrayCollection {
			if (results.list == null || results.list[key] == null) {
				return new ArrayCollection();
			} else if (results.list[key] is ObjectProxy) {
				return new ArrayCollection([results.list[key]]);
			} else { 
				return results.list[key] as ArrayCollection;
			}		
		}
		
		protected function generateElementListXML(listTag : String, elementTag : String, list : ArrayCollection) :XML {
			var xml : XML = <{listTag}></{listTag}>;
			for each (var obj : Object in list) {
				xml.appendChild(<{elementTag}><id>{obj.id}</id></{elementTag}>);	
			}
			return xml;
		}
	}
}