// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.business.people
{
	import com.mindalliance.channels.business.BaseDelegate;
	
	import mx.rpc.IResponder;
	import com.mindalliance.channels.vo.OrganizationVO;
	import com.mindalliance.channels.vo.ElementVO;
	import com.mindalliance.channels.vo.AddressVO;
	
	public class OrganizationDelegate extends BaseDelegate
	{	
		public function OrganizationDelegate(responder:IResponder)
		{
			super(responder);
			typeName="organization";
		}
		
		public function getOrganizationList() : void {
			performQuery("allOrganizations", null);
		}
		
		public function createOrganization(name:String) : void {
			var Organization : XML = <organization></organization>;
			Organization.appendChild(<name>{name}</name>);
			
			createElement(Organization);	
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
		override public function toXML(element : ElementVO) : XML {
			var obj : OrganizationVO = (element as OrganizationVO);
			var xml : XML =  <organization  schema="/channels/schema/organization.rng">
						<id>{obj.id}</id>
						<name>{obj.name}</name>
						<description>{obj.description}</description>
						<abbreviation>{obj.abbreviation}</abbreviation>
					</organization>;
			if (obj.parent != null) {
				xml.appendChild(<parentOrganizationId>{obj.parent.id}</parentOrganizationId>);	
			}
			if (obj.address != null) {
				xml.appendChild(obj.address.toXML());
			}
			return xml;
		}

		/**
		 * Expects XML of the form:
		 * <organization>
		 *   <id>{id}</id>
		 *   <name>{name}</name>
		 *   <description>{description}</description>
		 *   <abbreviation>{abbreviation}</abbreviation>
		 *   <parentId name="{parent.name}">{parent.id}</parentId>
		 *   <address>
		 *     <street>{street}</street>
		 *     <city>{city}</city>
		 *     <state>{state}</state>
		 *   </address>
		 * </organization>
		 */
		override public function fromXML( obj : Object ) : ElementVO {
				return new OrganizationVO(obj.id, 
										obj.name, 
										obj.description, 
										obj.abbreviation,
										new ElementVO(obj.parentOrganizationId, null),
										new AddressVO(obj.address.street, obj.address.city, obj.address.state));
		}
	}
}