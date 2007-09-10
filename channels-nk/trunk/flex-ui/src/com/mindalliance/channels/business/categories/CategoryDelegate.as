// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.business.categories
{
	import com.mindalliance.channels.business.BaseDelegate;
	import com.mindalliance.channels.util.XMLHelper;
	import com.mindalliance.channels.vo.CategoryVO;
	import com.mindalliance.channels.vo.common.ElementVO;
	
	import mx.rpc.IResponder;
	
	public class CategoryDelegate extends BaseDelegate
	{	
		public function CategoryDelegate(responder:IResponder)
		{
			super(responder);
		}
		/**
         * parses /channels/schema/category.rng
         */
		override public function fromXML(xml:XML):ElementVO {
			return new CategoryVO(xml.id, 
			                         xml.name, 
			                         xml.description,
			                         XMLHelper.xmlToIdList("categoryId", xml.disciplines),
                                     XMLHelper.xmlToIdList("categoryId", xml.implies),
                                     XMLHelper.xmlToInformation(xml.information));
		}
		/**
         * generates /channels/schema/category.rng
         */
		override public function toXML(element:ElementVO) : XML {
			var obj : CategoryVO = (element as CategoryVO);
			var xml : XML = <category schema="/channels/schema/category.rng">
						<id>{obj.id}</id>
						<name>{obj.name}</name>
						<description>{obj.description}</description>
					</category>;
			xml.appendChild(XMLHelper.idListToXML("disciplines","categoryId",obj.disciplines));
			xml.appendChild(XMLHelper.idListToXML("implies","categoryId",obj.implies));
			xml.appendChild(XMLHelper.informationToXML(obj.information));
			return xml;
		}
	}
}