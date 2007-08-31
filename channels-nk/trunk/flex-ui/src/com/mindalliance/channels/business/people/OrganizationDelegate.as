// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.business.people
{
	import com.mindalliance.channels.business.BaseDelegate;
	
	import mx.rpc.IResponder;
	import com.mindalliance.channels.vo.OrganizationVO;
	import com.mindalliance.channels.vo.common.ElementVO;
	import com.mindalliance.channels.vo.common.AddressVO;
	import com.mindalliance.channels.util.XMLHelper;
	import com.mindalliance.channels.vo.common.CategorySetVO;
	
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
		 * generates /channels/schema/organization.rng
		 */
		override public function toXML(element : ElementVO) : XML {
			var obj : OrganizationVO = (element as OrganizationVO);
			var xml : XML =  <organization  schema="/channels/schema/organization.rng">
						<id>{obj.id}</id>
						<name>{obj.name}</name>
						<description>{obj.description}</description>
						

					</organization>;
			
			xml.appendChild(XMLHelper.categorySetToXML(obj.categories));
			if (obj.abbreviation != null) {
				xml.appendChild(<abbreviation>{obj.abbreviation}</abbreviation>);
			}
			if (obj.parent != null) {
				xml.appendChild(<parentOrganizationId>{obj.parent.id}</parentOrganizationId>);	
			}
			if (obj.address != null) {
				xml.appendChild(XMLHelper.addressToXML(obj.address));
			}
			if (obj.logo != null) {
				xml.appendChild(<logo>{obj.logo}</logo>);
			}
			return xml;
		}

		/**
		 * parses /channels/schema/organization.rng
		 */
		override public function fromXML( obj : XML ) : ElementVO {
				return new OrganizationVO(obj.id, 
										obj.name, 
										obj.description, 
										XMLHelper.xmlToCategorySet(obj.categories),
										obj.abbreviation,
										new ElementVO(obj.parentOrganizationId, null),
										XMLHelper.xmlToAddress(obj.address),
										obj.logo);
		}
	}
}