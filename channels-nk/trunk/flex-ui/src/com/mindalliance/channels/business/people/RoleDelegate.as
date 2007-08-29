// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.business.people
{
	import com.mindalliance.channels.business.BaseDelegate;
	
	import mx.rpc.IResponder;
	import com.mindalliance.channels.vo.ElementVO;
	import com.mindalliance.channels.vo.RoleVO;
	import com.mindalliance.channels.vo.common.InformationVO;
	import com.mindalliance.channels.util.XMLHelper;
	import mx.collections.ArrayCollection;
	
	public class RoleDelegate extends BaseDelegate
	{	
		public function RoleDelegate(responder:IResponder)
		{
			super(responder);
		}
		
		override public function fromXML(xml:XML):ElementVO {
			var expertise : ArrayCollection = new ArrayCollection();
			for each (info : XML in xml.expertise) {
				expertise.addItem(XMLHelper.xmlToInformation(info);
			}
			return new RoleVO(xml.id, 
								xml.name, 
								xml.description,
								XMLHelper.categorySetToXML(xml.categories).categories,
								new ElementVO(xml.organizationId, null),
								expertise);
		}
		
		override public function toXML(obj:ElementVO) : XML {
			var role : RoleVO = (obj as RoleVO);
			var xml : XML = <role schema="/channels/schema/role.rng">
						<id>{role.id}</id>
						<name>{role.name}</name>
						<description>{role.description}</description>
					</role>;
			
			xml.appendChild(XMLHelper.categorySetToXML(obj.categories));
			xml.appendChild(<organizationId>{role.organization.id}</organizationId>);
			var expertise : XML = <expertise></expertise>;
			for each (var information : InformationVO in role.expertise) {
				expertise.appendChild(XMLHelper.informationToXML(information));	
			}
			xml.appendChild(expertise);
			return xml;
		}
	}
}