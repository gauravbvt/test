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
								projectId : String, 
								description : String,
								independent : Boolean,
								causeType : String,
								causeId : String,
								duration : Integer,
								durationUnit : String,
								categories : ArrayCollection,
								information : ArrayCollection ) {
			this.id = id;
			this.name = name;
			this.description = description;
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
		 *   <cause>
		 *     <type>{event|task|independent}</type>
		 *     <id>{event or task ID}</id> <!-- optional -->
		 *   </cause>
		 *	 <duration>
		 *     <taskBased>{true or false}
		 * 	   <!-- false -->
		 *     <length>{duration}</length>
		 *     <unit>{unit}</unit>
		 *     <!-- true -->
		 *     <tasks>
		 * 		  <taskId>{taskId}</taskId>
		 *        ...
		 *     </tasks>
		 *   </duration>
		 *   <categories>
		 *     <categoryId>{categoryId}</categoryId>
		 *     ...  
		 *   </categories>
		 *   <information>
		 *     <element>{elementTopic}</element>
		 *   </information>
		 * </event>
		 */
		public function toXML() : XML {
			return <event>
						<id>{id}</id>
						<name>{name}</name>
						<description>{description}</description>
					</event>;
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
				return new EventVO(obj.id, obj.name, obj.description);
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