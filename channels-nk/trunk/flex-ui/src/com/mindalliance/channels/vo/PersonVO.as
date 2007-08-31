// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.vo
{
	import com.adobe.cairngorm.vo.IValueObject;
	import com.mindalliance.channels.vo.common.AddressVO;
	import com.mindalliance.channels.vo.common.UpdatableElementVO;

	import mx.collections.ArrayCollection;

	public class PersonVO extends ElementVO implements IValueObject
	{
		public function PersonVO( id : String, 	
								firstName : String,
								lastName : String,
								photo : String,
								email : String,
								officePhone : String,
								cellPhone : String,
								address : AddressVO,
								roles : ArrayCollection,
								userId : String) {
			super(id,null,null);
			this.firstname = firstname;
			this.lastName = lastName;
			this.photo = photo;
			this.email = email;
			this.officePhone = officePhone;
			this.cellPhone = cellPhone;
			this.address = address;
			this.roles = roles;
			this.userId = userId;
		}
		
		private var _firstName : String;
		private var _lastName : String;
		private var _photo : String;
		private var _email : String;
		private var _officePhone : String;
		private var _cellPhone : String;
		private var _address : AddressVO;
		private var _roles : ArrayCollection;
		private var _userId : UserVO;
		
		public function get firstName() : String {
			return _firstName;
		}
		public function set firstName(firstName: String) : void {
			this._firstName = firstName;
		}			
		public function set lastName(lastName: String) : void {
			this._lastName = lastName;
		}
		public function get lastName() : String {
			return _lastName;
		}
        public function set email(email: String) : void {
            this._email = email;
        }
        public function get email() : String {
            return _email;
        }
        public function set photo(photo: String) : void {
            this._photo = photo;
        }
        public function get photo() : String {
            return _photo;
        }
		public function set officePhone(officePhone: String) : void {
			this._officePhone = officePhone;
		}
		public function get officePhone() : String {
			return _officePhone;
		}
		public function set cellPhone(cellPhone: String) : void {
			this._cellPhone = cellPhone;
		}
		public function get cellPhone() : String {
			return _cellPhone;
		}
		
		public function get address() : AddressVO {
			return _address;	
		}

		public function set address(address : AddressVO) : void {
			_address=address;
		}
		
		public function set roles(roles: ArrayCollection) : void {
			this._roles = roles;
		}
		public function get roles() : ArrayCollection {
			return _roles;
		}
		
		public function get userId() : String {
			return _userId;
		}

		public function set userId(userId : String) : void {
			_userId=userId;
		}
		
	}
}