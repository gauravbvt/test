// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.vo
{
	import com.adobe.cairngorm.vo.IValueObject;
	
	import mx.collections.ArrayCollection;

	public class PersonVO extends ElementVO implements IValueObject
	{
		public function PersonVO( id : String, 
								name : String, 
								description : String, 		
								firstName : String = "",
								lastName : String = "",
								email : String = "",
								officePhone : String = "",
								cellPhone : String = "",
								roles : ArrayCollection) {
			this.id = id;
			this.name = name;
			this.description = description;
			this.firstname = firstname;
			this.lastName = lastName;
			this.email = email;
			this.officePhone = officePhone;
			this.cellPhone = cellPhone;
			this.roles = roles;
		}
		
		private var _firstName : String;
		private var _lastName : String;
		private var _email : String;
		private var _officePhone : String;
		private var _cellPhone : String;
		private var _roles : ArrayCollection;
		
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
		public function set roles(roles: ArrayCollection) : void {
			this._roles = roles;
		}
		public function get roles() : ArrayCollection {
			return _roles;
		}
		/**
		 * Produces XML of the form:
		 * 
		 * <person>
		 *   <id>{id}</id>
		 *   <name>{name}</name>
		 *   <description>{description}</description>  
		 *   <firstName>{firstName}</firstName>
		 *   <lastName>{lastName}</lastName>
		 *   <email>{email}</email>
		 *   <officePhone>{officePhone}</officePhone>
		 *   <cellPhone>{cellPhone}</cellPhone>
		 *   <roles>
		 *     <roleId>{roleId}</roleId>
		 *     ...
		 *   </roles>
		 * </person>
		 */
		public function toXML() : XML {
			
			var personXML : XML = <person>
						<id>{id}</id>
						<name>{name}</name>
						<description>{description}</description>
						<firstName>{firstName}</firstName>
						<lastName>{lastName}</lastName>
						<email>{email}</email>
						<officePhone>{officePhone}</officePhone>
						<cellPhone>{cellPhone}</cellPhone>
					</person>;
			var rolesXML : XML = <roles></roles>;
			for each (var role in roles) {
				rolesXML.appendChild(<role><id>{role.id}</id><name>{role.name}</name></role>);	
			}
			personXML.appendChild(rolesXML);
			return personXML;
		}

		/**
		 * Expects XML of the form:
		 * <person>
		 *   <id>{id}</id>
		 *   <name>{name}</name>
		 *   <description>{description}</description>  
		 *   <firstName>{firstName}</firstName>
		 *   <lastName>{lastName}</lastName>
		 *   <email>{email}</email>
		 *   <officePhone>{officePhone}</officePhone>
		 *   <cellPhone>{cellPhone}</cellPhone>
		 *   <roles>
		 *     <role>
		 *       <id>{roleId}</id>
		 *       <name>{roleName}</name>
		 *     </role>
		 *     ...
		 *   </roles>
		 * </person>
		 */
		public static function fromXML( obj : Object ) : ProjectVO {
				return new PersonVO(obj.id, 
									obj.name, 
									obj.description,
									obj.firstName,
									obj.lastName,
									obj.email,
									obj.officePhone,
									obj.cellPhone,
									ElementVO.fromXMLList("role", obj.roles)
									);
		}
		
		/**
		 * Produces a list from XML of the form:
		 * 
		 * <list>
		 *   <person>
		 *     <id>{id}</id>
		 *     <name>{name}</id>
		 *   </person>
		 *   ...
		 * </list>
		 * 
		 */
		public static function fromXMLList( obj : Object ) : ArrayCollection {
			return ElementVO.fromXMLList("person", obj);
		}
	}
}