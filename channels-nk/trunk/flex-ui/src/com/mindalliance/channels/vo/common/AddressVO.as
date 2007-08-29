package com.mindalliance.channels.vo.common
{
	import com.adobe.cairngorm.vo.IValueObject;

	[Bindable]
	public class AddressVO implements IValueObject, ISpatial
	{
		
		public function AddressVO(street:String,
									city : String,
									state : String) 
		{
			this.street = street;
			this.city = city;
			this.state = state;					
		}
		
		private var _street : String;
		private var _city : String;
		private var _state : String;
		
		public function get street() : String {
			return _street;
		}
		
		public function set street(street: String) : void {
			this._street = street;	
		}
		
		public function get city() : String {
			return _city;
		}
		
		public function set city(city: String) : void {
			this._city = city;
		}
		
		public function get state() : String {
			return _state;
		}
		
		public function set state(state: String) : void {
			this._state = state;
		}

	}
}