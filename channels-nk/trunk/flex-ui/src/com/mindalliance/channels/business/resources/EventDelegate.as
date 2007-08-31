// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.business.resources
{
	import com.mindalliance.channels.business.BaseDelegate;
	import com.mindalliance.channels.util.XMLHelper;
	import com.mindalliance.channels.vo.EventVO;
	import com.mindalliance.channels.vo.common.DurationVO;
	
	import mx.collections.ArrayCollection;
	import mx.rpc.IResponder;
	
	public class EventDelegate extends BaseDelegate
	{	
		public function EventDelegate(responder:IResponder)
		{
			super(responder);
		}
		/**
         * parses /channels/schema/event.rng
         */
		override public function fromXML(obj:Object):ElementVO {
			var taskCompletions : ArrayCollection = null;
			var duration : DurationVO = null;
			if (xml.terminatesAfter != null) {
                if (xml.terminatesAfter.duration != null) {
                    taskCompletions = xml.appendChild(XMLHelper.fromXMLList("taskId",xml.terminatesAfter.taskCompletions); 
                    
                }  else if (xml.terminatesAfter.duration!=null) {
                    duration = XMLHelper.xmlToDuration(xml.terminatesAfter.duration);
                } 
            } 
			
			var cause : CauseVO;
            if (xml.cause != null) {
                cause = XMLHelper.xmlToCause(xml.cause);    
            }
            return new EventVO(obj.id, 
                                 obj.name, 
                                 obj.description,
                                 XMLHelper.xmlToCategorySet(obj.categories),
                                 XMLHelper.xmlToOccurenceWhere(obj.where),
                                 cause,
                                 new ElementVO(obj.scenarioId, null),
                                 duration,
                                 taskCompletions);
			
			return new EventVO(obj.id, obj.name, obj.description);
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
			xml.appendChild(XMLHelper.xmlToCategorySet(obj.categories));
			if (obj.where != null) {
			     xml.appendChild(XMLHelper.occurrenceWhereToXML(obj.where);	
			}
			if (obj.cause != null) {
                xml.appendChild(XMLHelper.causeToXML(obj.cause);	
			}
            if (obj.duration != null) {
                var term : XML = <terminatesAfter></terminatesAfter>;
                term.appendChild(XMLHelper.durationToXML(obj.duration));
                xml.appendChild(term);
            } else if (obj.taskCompletions != null) {
                var term : XML = <terminatesAfter></terminatesAfter>;
                term.appendChild(XMLHelper.toXMLElementList("taskCompletions","taskId",obj.taskCompletions);
                xml.appendChild(term);
                	
            }
            
			return xml;
		}
	}
}