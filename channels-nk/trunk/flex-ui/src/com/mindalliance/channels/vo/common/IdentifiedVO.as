package com.mindalliance.channels.vo.common
{
	import com.adobe.cairngorm.vo.IValueObject;

	[Bindable]
	public class IdentifiedVO implements IValueObject
	{
		
		public function IdentifiedVO(name : String, description : String) {
			this.name = name;
			this.description = description;
		}
		
		private var _name:String;
		private var _description:String;
		
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
		
	}
}