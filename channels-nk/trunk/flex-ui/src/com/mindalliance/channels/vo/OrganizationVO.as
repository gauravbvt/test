package com.mindalliance.channels.vo
{
	import com.adobe.cairngorm.vo.IValueObject;

	public class OrganizationVO extends ElementVO implements IValueObject
	{
		public function OrganizationVO( id : String, 
								name : String, 
								description : String,
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
		private var _address : AddressVO = new AddressVO();
		
		public function get abbreviation() : String {
			return _abbreviation;
		}
		
		public function set abbreviation(abbreviation: String) : void {
			this._abbreviation = abbreviation;	
		}
		
		public function get parent() : ElementVO {
			return _parentId;
		}
		
		public function set parent(parentId: ElementVO) : void {
			this._parentId = parentId;	
		}
		
		public function get address() : String {
			return _address;
		}
		
		public function set address(address: String) : void {
			this._address = address;	
		}
		/**
		 * Produces XML of the form:
		 * 
		 * <organization>
		 *   <id>{id}</id>
		 *   <name>{name}</name>
		 *   <description>{description}</description>
		 *   <abbreviation>{abbreviation}</abbreviation>
		 *   <parentId>{parent.id}</parentId>
		 *   <address>
		 *     <street>{street}</street>
		 *     <city>{city}</city>
		 *     <state>{state}</state>
		 *   </address>
		 * </organization>
		 */
		public function toXML() : XML {
			return <organization>
						<id>{id}</id>
						<name>{name}</name>
						<description>{description}</description>
						<abbreviation>{abbreviation}</abbreviation>
						<parentId>{parent.id}</parentId>
						{address.toXML()}
					</organization>;
		}

		/**
		 * Expects XML of the form:
		 * <organization>
		 *   <id>{id}</id>
		 *   <name>{name}</name>
		 *   <description>{description}</description>
		 *   <abbreviation>{abbreviation}</abbreviation>
		 *   <parent>
		 *     <id>{parent.id}</id>
		 *     <name>{parent.name}</name>
		 *   </parent>
		 *   <address>
		 *     <street>{street}</street>
		 *     <city>{city}</city>
		 *     <state>{state}</state>
		 *   </address>
		 * </organization>
		 */
		public static function fromXML( obj : Object ) : ProjectVO {
				return new OrganizationVO(obj.id, 
										obj.name, 
										obj.description, 
										new ElementVO(obj.parent.id, obj.parent.name),
										new AddressVO(obj.address.street, obj.address.city, obj.address.state));
		}
		
		/**
		 * Produces a list from XML of the form:
		 * 
		 * <list>
		 *   <organization>
		 *     <id>{id}</id>
		 *     <name>{name}</id>
		 *   </organization>
		 *   ...
		 * </list>
		 * 
		 */
		public static function fromXMLList( obj : Object ) : ArrayCollection {
			return ElementVO.fromXMLList("organization", obj);
		}
	}
}