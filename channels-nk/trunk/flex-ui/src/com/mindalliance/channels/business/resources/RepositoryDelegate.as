// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.business.resources
{
	import com.mindalliance.channels.business.BaseDelegate;
	import com.mindalliance.channels.util.XMLHelper;
	import com.mindalliance.channels.vo.RepositoryVO;
	import com.mindalliance.channels.vo.common.ElementVO;
	
	import mx.collections.ArrayCollection;
	import mx.rpc.IResponder;
	
	public class RepositoryDelegate extends BaseDelegate
	{	
		public function RepositoryDelegate(responder:IResponder)
		{
			super(responder);
		}
		/**
         * parses /channels/schema/repository.rng
         */
		override public function fromXML(obj:XML):ElementVO {
			var contents : ArrayCollection = new ArrayCollection();
			for each (el in obj.contents) {
			     contents.addItem(XMLHelper.xmlToInformation(el);	
			}
			return new RepositoryVO(obj.id, 
			                         obj.name, 
			                         obj.description,
			                         XMLHelper.xmlToCategorySet(obj.categories),
			                         new ElementVO(obj.organizationId, null),
			                         XMLHelper.fromXMLElementList("roleId", obj.administrators),
			                         contents,
			                         XMLHelper.fromXMLElementList("roleId", obj.access));
		}
		/**
         * generates /channels/schema/repository.rng
         */
		override public function toXML(element:ElementVO) : XML {
			var obj : RepositoryVO = (element as RepositoryVO);
			var xml : XML = <Repository schema="/channels/schema/repository.rng">
						<id>{obj.id}</id>
						<name>{obj.name}</name>
						<description>{obj.description}</description>
					</repository>;
			xml.appendChild(XMLHelper.categorySetToXML(obj.categories);
			xml.appendChild(<organizationId>{obj.organization.id}</organizationId>);
			XMLHelper.toXMLElementList("administrators","roleId",obj.administrators);
			var contents : XML = <contents></contents>;
			for each (var info : InformationVO in obj.contents) {
                contents.appendChild(XMLHelper.informationToXML(info));	
			}
			xml.appendChild(contents);
			XMLHelper.toXMLElementList("access", "roleId", obj.access);
			return xml;
		}
	}
}