package com.mindalliance.channels.vo
{
	import com.adobe.cairngorm.vo.IValueObject;
	import com.mindalliance.channels.vo.common.AddressVO;
	import com.mindalliance.channels.vo.common.CategorySetVO;
	import com.mindalliance.channels.vo.common.Taxonomy;
	import mx.collections.ArrayCollection;
	
	[Bindable]
	public class OrganizationVO extends CategorizedElementVO implements IValueObject
	{
		public function OrganizationVO( id : String, 
								name : String, 
								description : String,
								categories : ArrayCollection,
								abbreviation : String,
								parent : ElementVO,
								address : AddressVO,
								logo : String ) {
			super(id, name, description, new CategorySetVO(Taxonomy.ORGANIZATION, categories));
			this.abbreviation = abbreviation;
			this.parent = parent;
			this.address = address;
			this.logo = logo;
		}
		private var _abbreviation : String;
		private var _parent : ElementVO;
		private var _address : AddressVO;
		private var _logo : String;
		
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
		
		public function get logo() : String {
			return _logo;
		}
		
		public function set logo(logo: String) : void {
			this._logo = logo;	
		}

	}
}