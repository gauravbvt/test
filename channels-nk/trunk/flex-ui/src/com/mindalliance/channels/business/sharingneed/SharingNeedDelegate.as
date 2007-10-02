// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.business.sharingneed
{
	import com.mindalliance.channels.business.common.BaseDelegate;
	import com.mindalliance.channels.vo.SharingNeedVO;
	import com.mindalliance.channels.vo.common.ElementVO;
	
	import mx.collections.ArrayCollection;
	import mx.rpc.IResponder;
	
	public class SharingNeedDelegate extends BaseDelegate
	{	
		public function SharingNeedDelegate(responder:IResponder)
		{
			super(responder);
			typeName="sharingNeed";
		}
		
		public function getSharingNeedList(scenarioId : String) : void {
            var request:Array = new Array();
            request["scenarioId"] = scenarioId;
            performQuery("sharingNeedsInScenario", request);
        }
		
		/**
         * parses /channels/schema/SharingNeed.rng
         */
		override public function fromXML(xml:XML):ElementVO {
			return new SharingNeedVO(xml.id,xml.knowId, xml.needToKnowId);
		}
		/**
         * generates /channels/schema/SharingNeed.rng
         */
		override public function toXML(element:ElementVO) : XML {
			var obj : SharingNeedVO = (element as SharingNeedVO);
			var xml : XML = <sharingNeed schema="/channels/schema/sharingNeed.rng">
						<id>{obj.id}</id>
						<knowId>{obj.knowId}</knowId>
						<needToKnowId>{obj.needToKnowId}</needToKnowId>
					</sharingNeed>;
			
			return xml;
		}
		
		override public function fromXMLElementList(list : XML) : ArrayCollection {
            var results : ArrayCollection = new ArrayCollection();
            for each (var el : XML in list.elements(typeName)) {
                results.addItem(fromXML(el)); 
            }
            return results; 
        }
        
        public function create(knowId : String, needToKnowId : String):void {
            var param : Array = new Array();
            param["knowId"] = knowId;
            param["needToKnowId"] = needToKnowId;
            var xml : XML = 	<sharingNeed schema="/channels/schema/sharingNeed.rng">
                        <knowId>{knowId}</knowId>
                        <needToKnowId>{needToKnowId}</needToKnowId>
                    </sharingNeed>;
            createElement(xml, param);
        }
	}
}