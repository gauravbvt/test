package com.mindalliance.channels.vo
{
	import com.adobe.cairngorm.vo.IValueObject;

	import mx.collections.ArrayCollection;
	
	[Bindable]
	public class OrganizationVO extends ElementVO implements IValueObject
	{
		public function OrganizationVO( id : String, 
								name : String, 
								description : String,
								abbreviation : String,
								parent : ElementVO,
								address : AddressVO ) {
			this.id = id;
			this.name = name;
			this.description = description;
			this.parent = parent;
			this.address = address;
		}
		private var _abbreviation : String;
		private var _parent : ElementVO;
		private var _address : AddressVO;
		
		public function get abbreviation() : String {
			return _abbreviation;
		}
		
		public function set abbreviation(abbreviation: String) : void {
			this._abbreviation = abbreviation;	
		}
		
		public function get parent() : ElementVO {
			return _parent;
		}
		
		public function set parent(parentId: ElementVO) : void {
			this._parent = parent;	
		}
		
		public function get address() : AddressVO {
			return _address;
		}
		
		public function set address(address: AddressVO) : void {
			this._address = address;	
		}

	}
}