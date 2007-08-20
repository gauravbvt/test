// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.vo
{
	import com.adobe.cairngorm.vo.IValueObject;

	public class RoleVO extends ElementVO implements IValueObject
	{
		public function RoleVO( id : String, 
								name : String, 
								projectId : String, 
								description : String 
								organizationId : String,
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
		
		public function toXML() : XML {
			return <role>
						<id>{id}</id>
						<name>{name}</name>
						<description>{description}</description>
						<organizationId>{organizationId}</organizationId>
						<expertise>{expertise}</expertise>
					</role>;
		}
	}
}