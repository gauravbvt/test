
package com.mindalliance.channels.vo
{
	import com.adobe.cairngorm.vo.IValueObject;

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
	}
}