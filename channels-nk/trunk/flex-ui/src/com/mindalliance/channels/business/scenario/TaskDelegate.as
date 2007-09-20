// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.business.scenario
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
			typeName="task";
		}
		
		public function getTaskList(scenarioId : String) : void {
			var request:Array = new Array();
            request["scenarioId"] = scenarioId;
			performQuery("tasksInScenario", request);
		}
		
		/**
         * parses /channels/schema/task.rng
         */
		override public function fromXML(xml : XML):ElementVO {
			var cause : CauseVO;
			if (xml.cause.length > 0) {
                cause = XMLHelper.xmlToCause(xml);	
			}
			return new TaskVO(xml.id, 
			                     xml.name, 
			                     xml.description,
			                     XMLHelper.xmlToCategorySet(xml),
			                     XMLHelper.xmlToOccurenceWhere(xml),
			                     cause,
			                     XMLHelper.xmlToDuration(xml.duration),
			                     new ElementVO(xml.scenarioId, null));
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
			
            xml.appendChild(XMLHelper.categorySetToXML(obj.categories));
            if (obj.where != null) {
                 xml.appendChild(XMLHelper.occurrenceWhereToXML(obj.where)); 
            }
            xml.appendChild(<scenarioId>{obj.scenario.id}</scenarioId>);
            if (obj.cause != null) {
                xml.appendChild(XMLHelper.causeToXML(obj.cause));    
            }
            if (obj.duration != null) {
            	xml.appendChild(XMLHelper.durationToXML(obj.duration));
            }
			return xml;
		}
		public function create(name : String, scenarioId : String) : void {
	       var param : Array=new Array();
           param["name"] = name;
           param["scenarioId"] = scenarioId;
	       createElement( <task schema="/channels/schema/task.rng">
	         <name>{name}</name>
	         <categories atMostOne="false" taxonomy="task"/>
	         <scenarioId>{scenarioId}</scenarioId>
	         <duration>
	           <value>0</value>
	           <unit>second</unit>
	         </duration>
	       </task>, param);
	    }
	}
	

}