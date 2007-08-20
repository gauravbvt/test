// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.vo
{
	import com.adobe.cairngorm.vo.IValueObject;
	import mx.collections.ArrayCollection;
	public class RoleVO extends ElementVO implements IValueObject
	{
		public function RoleVO( id : String, 
								name : String, 
								projectId : String, 
								description : String 
								organization : ElementVO,
								expertise : String) {
			this.id = id;
			this.name = name;
			this.description = description;
		}
		
		private var _organizationId : String;
		private var _expertise : String;
		
		public function get organizationId() : String {
			return _organizationId;
		}
		
		public function set organizationId(organizationId: String) : void {
			this._organizationId = organizationId;	
		}
		
		public function get expertise() : String {
			return _expertise;
		}
		
		public function set expertise(expertise: String) : void {
			this._expertise = expertise;	
		}
		/**
		 * Produces XML of the form:
		 * 
		 * <role>
		 *   <id>{id}</id>
		 *   <name>{name}</name>
		 *   <description>{description}</description>
		 *   <organizationId>{organizationId}</organizationId>
		 *   <expertise>{expertise}</expertise>
		 * </role>
		 */
		public function toXML() : XML {
			return <role>
						<id>{id}</id>
						<name>{name}</name>
						<description>{description}</description>
						<organizationId>{organizationId}</organizationId>
						<expertise>{expertise}</expertise>
					</role>;
		}
		
		/**
		 * Expects XML of the form:
		 * <role>
		 *   <id>{id}</id>
		 *   <name>{name}</name>
		 *   <description>{description}</description>
		 *   <organization>
		 *     <id>{organizationId}</id>
		 *     <name>{organizationName}</name>
		 *   </organization>
		 *   <expertise>{expertise}</expertise>
		 * </role>
		 */
		public static function fromXML( obj : Object ) : ScenarioVO {
				return new RoleVO(obj.id, obj.name, obj.projectId, obj.description, new ElementVO(obj.organization.id, obj.organization.name));
		}
		
		/**
		 * Produces a list from XML of the form:
		 * 
		 * <list>
		 *   <role>
		 *     <id>{id}</id>
		 *     <name>{name}</id>
		 *   </role>
		 *   ...
		 * </list>
		 * 
		 */
		public static function fromXMLList( obj : Object ) : ArrayCollection {
			return ElementVO.fromXMLList("role", obj);
		}
	}
}