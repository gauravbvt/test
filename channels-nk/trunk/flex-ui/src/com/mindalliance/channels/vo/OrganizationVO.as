package com.mindalliance.channels.vo
{
	import com.adobe.cairngorm.vo.IValueObject;

	public class OrganizationVO extends ElementVO implements IValueObject
	{
		public function OrganizationVO( id : String, 
								name : String, 
								projectId : String, 
								description : String ) {
			this.id = id;
			this.name = name;
			this.description = description;
		}
		private var _abbreviation : String;
		private var _parentId : String;
		private var _address : AddressVO = new AddressVO();
		
		public function get abbreviation() : String {
			return _abbreviation;
		}
		
		public function set abbreviation(abbreviation: String) : void {
			this._abbreviation = abbreviation;	
		}
		
		public function get parentId() : String {
			return _parentId;
		}
		
		public function set parentId(parentId: String) : void {
			this._parentId = parentId;	
		}
		
		public function get address() : String {
			return _address;
		}
		
		public function set address(address: String) : void {
			this._address = address;	
		}
		
		public function toXML() : XML {
			return <organization>
						<id>{id}</id>
						<name>{name}</name>
						<description>{description}</description>
						<abbreviation>{abbreviation}</abbreviation>
						<parentId>{parentId}</parentId>
						{address.toXML()}
					</organization>;
		}
	}
}