// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.business.scenario
{
	import com.mindalliance.channels.business.BaseDelegate;
	import com.mindalliance.channels.util.XMLHelper;
	import com.mindalliance.channels.vo.ArtifactVO;
	import com.mindalliance.channels.vo.common.ElementVO;
	
	import mx.rpc.IResponder;
	
	public class ArtifactDelegate extends BaseDelegate
	{	
		public function ArtifactDelegate(responder:IResponder)
		{
			super(responder);
		}
		
		override public function fromXML(obj:Object):ElementVO {
			return new ArtifactVO(obj.id, obj.name, obj.description,
			                     XMLHelper.xmlToCategorySet(obj.categories),
			                     XMLHelper.fromXMLElementList("taskId", obj.product));
		}
		
		override public function toXML(element:ElementVO) : XML {
			var obj : ArtifactVO = (element as ArtifactVO);
			var xml : XML =  <artifact schema="/channels/schema/artifact.rng">
						<id>{obj.id}</id>
						<name>{obj.name}</name>
						<description>{obj.description}</description>
					</artifact>;
            
            xml.appendChild(XMLHelper.categorySetToXML(obj.categories));
            xml.appendChild(XMLHelper.toXMLElementList("product", "taskId", obj.product));
            return xml;
		}
	}
}