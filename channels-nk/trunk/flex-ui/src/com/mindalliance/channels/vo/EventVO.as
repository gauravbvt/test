// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.vo
{
	import com.adobe.cairngorm.vo.IValueObject;
	
	import mx.collections.ArrayCollection;

	public class EventVO extends OccurrenceVO implements IValueObject
	{
		public function EventVO( id : String, 
								name : String, 
								description : String,
								categories : ArrayCollection,
								information : ArrayCollection,
								cause : CauseVO,
								duration : DurationVO) {
			this.id = id;
			this.name = name;
			this.description = description;
			this.categories = categories;
			this.information = information;
			this.cause = cause;
			this.duration = duration;
		}

		/**
		 * Produces XML of the form:
		 * 
		/**
		 * Produces XML of the form:
		 * 
		 * <event>
		 *   <id>{id}</id>
		 *   <name>{name}</name>
		 *   <description>{description}</description>
		 *   <categories>
		 *     <categoryId>{categoryId}</categoryId>
		 *     ...  
		 *   </categories>
		 *   <information>
		 *     <element>{elementTopic}</element>
		 *   </information>
		 *   <cause>
		 *     <type>{event|task|independent}</type>
		 *     <id>{event or task ID}</id> <!-- optional -->
		 *   </cause>
		 *	 <duration>
		 *     <taskBased>{true or false}</taskBased>
		 * 	   <!-- false -->
		 *     <length>{duration}</length>
		 *     <unit>{unit}</unit>
		 *     <!-- true -->
		 *     <tasks>
		 * 		  <taskId>{taskId}</taskId>
		 *        ...
		 *     </tasks>
		 *   </duration>

		 * </event>
		 */
		public function toXML() : XML {
			var xml : XML =  <event>
						<id>{id}</id>
						<name>{name}</name>
						<description>{description}</description>
					</event>;	
			
			xml.appendChild(this.generateElementListXML("categories", "categoryId", categories);
			xml.appendChild(this.generateXMLInformationList(information));
			xml.appendChild(cause.toXML());
			xml.appendChild(duration.toXML());
			
			return xml;
		}

		/**
		 * Expects XML of the form:
		 * <event>
		 *   <id>{id}</id>
		 *   <name>{name}</name>
		 *   <description>{description}</description>
		 *   <cause>
		 *     <type>{event|task|independent}</type>
		 *     <id>{event or task ID}</id> <!-- optional -->
		 *     <name>{event or task name}</name> <!-- optional -->
		 *   </cause>
		 *	 <duration>
		 *     <taskBased>{true or false}
		 * 	   <!-- false -->
		 *     <length>{duration}</length>
		 *     <unit>{unit}</unit>
		 *     <!-- true -->
		 *     <tasks>
		 * 		  <task>
		 *          <id>{taskId}</id>
		 *          <name>{name}</name>
		 *        </task>
		 *        ...
		 *     </tasks>
		 *   </duration>
		 *   <categories>
		 *     <category>
		 *       <id>{categoryId}</id>
		 *       <name>{categoryName}</name>
		 *       <information>
		 *         <element>
		 *           <topic>{topicName}</topic>
		 *           <label>{labelName}</label>
		 *         </element>
		 *         ...
		 *       </information>
		 *     </category>
		 *     ...  
		 *   </categories>
		 *   <information>
		 *     <element>{elementTopic}</element>
		 *     ...
		 *   </information>		 
		 * </event>
		 */
		public static function fromXML( obj : Object ) : ProjectVO {
				return new EventVO(obj.id, 
				obj.name, 
				obj.description,
				ElementVO.fromXMLList("category", obj.categories),
				ElementVO.fromXMLList("element", obj.information),
				CauseVO.fromXML(obj.cause),
				DurationVO.fromXML(obj.duration));
		}
		
		/**
		 * Produces a list from XML of the form:
		 * 
		 * <list>
		 *   <event>
		 *     <id>{id}</id>
		 *     <name>{name}</id>
		 *   </event>
		 *   ...
		 * </list>
		 * 
		 */
		public static function fromXMLList( obj : Object ) : ArrayCollection {
			return ElementVO.fromXMLList("event", obj);
		}
	}
}