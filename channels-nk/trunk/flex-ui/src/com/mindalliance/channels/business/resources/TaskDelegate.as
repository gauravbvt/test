// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.business.resources
{
	import com.mindalliance.channels.business.BaseDelegate;
	import com.mindalliance.channels.util.XMLHelper;
	import com.mindalliance.channels.vo.TaskVO;
	import com.mindalliance.channels.vo.common.CauseVO;
	import com.mindalliance.channels.vo.common.ElementVO;
	
	import mx.rpc.IResponder;
	
	public class TaskDelegate extends BaseDelegate
	{	
		public function TaskDelegate(responder:IResponder)
		{
			super(responder);
		}
		/**
         * parses /channels/schema/task.rng
         */
		override public function fromXML(xml : XML):ElementVO {
			var cause : CauseVO;
			if (xml.cause != null) {
                cause = XMLHelper.xmlToCause(xml.cause);	
			}
			return new TaskVO(obj.id, 
			                     obj.name, 
			                     obj.description,
			                     XMLHelper.xmlToCategorySet(obj.categories),
			                     XMLHelper.xmlToOccurenceWhere(obj.where),
			                     cause,
			                     new ElementVO(obj.scenarioId, null));
		}
		/**
         * generates /channels/schema/task.rng
         */
		override public function toXML(element:ElementVO) : XML {
			var obj : TaskVO = (element as TaskVO);
			var xml : XML = <task schema="/channels/schema/task.rng">
						<id>{obj.id}</id>
						<name>{obj.name}</name>
						<description>{obj.description}</description>
					</task>;
			
            xml.appendChild(XMLHelper.xmlToCategorySet(obj.categories));
            if (obj.where != null) {
                 xml.appendChild(XMLHelper.occurrenceWhereToXML(obj.where); 
            }
            if (obj.cause != null) {
                xml.appendChild(XMLHelper.causeToXML(obj.cause);    
            }
            if (obj.duration != null) {
            	xml.appendChild(XMLHelper.durationToXML(obj.duration));
            }
            xml.appendChild(<scenarioId>{obj.scenario.id}</scenarioId>);
			return xml;
		}
	}
}