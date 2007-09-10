// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.business.scenario
{
	import com.mindalliance.channels.business.BaseDelegate;
	import com.mindalliance.channels.util.XMLHelper;
	import com.mindalliance.channels.vo.EventVO;
	import com.mindalliance.channels.vo.common.CauseVO;
	import com.mindalliance.channels.vo.common.DurationVO;
	import com.mindalliance.channels.vo.common.ElementVO;
	
	import mx.collections.ArrayCollection;
	import mx.rpc.IResponder;
	
	public class EventDelegate extends BaseDelegate
	{	
		public function EventDelegate(responder:IResponder)
		{
			super(responder);
			typeName="event";
		}
		
		public function getEventList(scenarioId : String) : void {
            var request:Array = new Array();
            request["scenarioId"] = scenarioId;
            performQuery("eventsInScenario", request);
        }
        
		
		/**
         * parses /channels/schema/event.rng
         */
		override public function fromXML(xml:XML):ElementVO {
			var taskCompletions : ArrayCollection = null;
			var duration : DurationVO = null;
			if (xml.terminatesAfter.length() > 0) {
                if (xml.terminatesAfter.taskCompletions.length() > 0) {
                    taskCompletions = XMLHelper.xmlToIdList("taskId",xml.terminatesAfter.taskCompletions); 
                    
                }  else if (xml.terminatesAfter.duration.length() > 0) {
                    duration = XMLHelper.xmlToDuration(xml.terminatesAfter.duration);
                } 
            } 
			
			var cause : CauseVO;
            if (xml.cause.length() > 0) {
                cause = XMLHelper.xmlToCause(xml);    
            }
            return new EventVO(xml.id, 
                                 xml.name, 
                                 xml.description,
                                 XMLHelper.xmlToCategorySet(xml),
                                 XMLHelper.xmlToOccurenceWhere(xml),
                                 cause,
                                 new ElementVO(xml.scenarioId, null),
                                 duration,
                                 taskCompletions);
			
		}
		/**
         * generates /channels/schema/event.rng
         */
		override public function toXML(element:ElementVO) : XML {
			var obj : EventVO = (element as EventVO);
			var xml : XML = <event schema="/channels/schema/event.rng">
						<id>{obj.id}</id>
						<name>{obj.name}</name>
						<description>{obj.description}</description>
					</event>;
			xml.appendChild(XMLHelper.categorySetToXML(obj.categories));
			if (obj.where != null) {
			     xml.appendChild(XMLHelper.occurrenceWhereToXML(obj.where));	
			}
			if (obj.cause != null) {
                xml.appendChild(XMLHelper.causeToXML(obj.cause));	
			}
            var term : XML = <terminatesAfter></terminatesAfter>;
            if (obj.duration != null) {
                term.appendChild(XMLHelper.durationToXML(obj.duration));
                xml.appendChild(term);
            } else if (obj.taskCompletions != null) {
                term.appendChild(XMLHelper.idListToXML("taskCompletions","taskId",obj.taskCompletions));
                xml.appendChild(term);
                	
            }
            xml.appendChild(<scenarioId>{obj.scenario.id}</scenarioId>);
			return xml;
		}
	}
}