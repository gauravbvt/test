// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.business.scenario
{
	import com.mindalliance.channels.business.BaseDelegate;
	import com.mindalliance.channels.util.XMLHelper;
	import com.mindalliance.channels.vo.AcquirementVO;
	import com.mindalliance.channels.vo.common.ElementVO;
	
	import mx.rpc.IResponder;
	
	public class AcquirementDelegate extends BaseDelegate
	{	
		public function AcquirementDelegate(responder:IResponder)
		{
			super(responder);
			typeName="acquirement";
		}
		public function getAcquirementList(scenarioId : String) : void {
            var request:Array = new Array();
            request["scenarioId"] = scenarioId;
            performQuery("acquirementsInScenario", request);
        }
        
		override public function fromXML(obj:XML):ElementVO {

			return new AcquirementVO(obj.id, obj.name, obj.description,
			                     XMLHelper.xmlToCategorySet(obj),
			                     XMLHelper.xmlToIdList("taskId", obj.product),
                                     XMLHelper.xmlToInformation(obj.information[0]));
		}
		
		override public function toXML(element:ElementVO) : XML {
			var obj : AcquirementVO = (element as AcquirementVO);
			var xml : XML =  <acquirement schema="/channels/schema/acquirement.rng">
						<id>{obj.id}</id>
						<name>{obj.name}</name>
						<description>{obj.description}</description>
					</acquirement>;
            
            xml.appendChild(XMLHelper.categorySetToXML(obj.categories));
            xml.appendChild(XMLHelper.idListToXML("product", "taskId", obj.product));
            xml.appendChild(XMLHelper.informationToXML(obj.information));
            return xml;
		}
		
		public function create(name : String, taskId : String) : void {
          var param : Array=new Array();
          param["name"] = name;
          param["taskId"] = taskId;
           createElement( <acquirement schema="/channels/schema/acquirement.rng">
             <name>{name}</name>
             <categories atMostOne="false" taxonomy="acquirement"/>
             <product>
                <taskId>{taskId}</taskId>
             </product>
             <information/>
           </acquirement>, param);
        }
	}
}