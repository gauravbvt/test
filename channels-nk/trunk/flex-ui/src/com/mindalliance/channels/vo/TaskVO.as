// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.vo
{
	import com.adobe.cairngorm.vo.IValueObject;
	
	import mx.collections.ArrayCollection;

	public class TaskVO extends OccurrenceVO implements IValueObject
	{
		public function TaskVO( id : String, 
								name : String, 
								description : String,
								categories : ArrayCollection,
								information : ArrayCollection,
								cause : CauseVO,
								duration : DurationVO,
								agents : ArrayCollection,
								artifacts : ArrayCollection) {
			this.id = id;
			this.name = name;
			this.description = description;
			this.cause = cause;
			this.duration =duration;
			this.categories =categories;
			_agents = agents;
			_artifacts = artifacts;
		}

		private var _agents : ArrayCollection;
		private var _artifacts : ArrayCollection;
		
		public function get agents() : String {
			return _agents;
		}
		
		public function set agents(agents : String) : void {
			_agents = agents;
		}
		
		public function get artifacts() : ArrayCollection {
			return _artifacts;
		}

		public function set artifacts(artifacts : ArrayCollection) : void {
			_artifacts=artifacts;
		}
		
		/**
		 * Produces XML of the form:
		 * 
		 * <task>
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
		 *     <length>{duration}</length>
		 *     <unit>{unit}</unit>
		 *   </duration>
		 * 	 <agents>
		 *     <roleId>{roleId}</roleId>
		 *     ...
		 *   </agents>
		 *   <artifacts>
		 *      <artifactId>{artifactId}</artifactId>
		 *      ...
		 *   </artifacts>
		 * </task>
		 */
		public function toXML() : XML {
			var taskXML : XML = <task>
						<id>{id}</id>
						<name>{name}</name>
						<description>{description}</description>						
					</task>;
			xml.appendChild(this.generateElementListXML("categories", "categoryId", categories);
			xml.appendChild(this.generateXMLInformationList(information));
			taskXML.appendChild(cause.toXML());
			taskXML.appendChild(duration.toXML());
			taskXML.appendChild(generateElementListXML("agents", "roleId", agents);
			taskXML.appendChild(generateElementListXML("artifacts", "artifactId", artifacts);
			
		}

		/**
		 * Expects XML of the form:
		 * <task>
		 *   <id>{id}</id>
		 *   <name>{name}</name>
		 *   <description>{description}</description>
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
		 *   <cause>
		 *     <type>{event|task|independent}</type>
		 *     <id>{event or task ID}</id> <!-- optional -->
		 *     <name>{event or task name}</name> <!-- optional -->
		 *   </cause>
		 *	 <duration>
		 *     <length>{duration}</length>
		 *     <unit>{unit}</unit>
		 *   </duration>
         *   <agents>
		 *     <role>
		 *       <id>{roleId}</id>
		 *       <name>{roleName}</name>
		 *     </role>
		 *     ...
		 *   </agents>
		 *   <artifacts>
		 *      <artifact>
		 *        <id>{artifactId}</id>
		 *        <name>{artifactName}</name>
		 *      </artifact>
		 *      ...
		 *   </artifacts>
		 * </task>
		 */
		public static function fromXML( obj : Object ) : ProjectVO {
				return new TaskVO(obj.id, 
									obj.name, 
									obj.description,
									ElementVO.fromXMLList("category", obj.categories),
									ElementVO.fromXMLList("element", obj.information),
									CauseVO.fromXML(obj.cause);,
									DurationVO.fromXML(obj.duration);
									ElementVO.fromXMLList("role", obj.agents),
									ElementVO.fromXMLList("artifact", obj.artifacts));
		}
		
		/**
		 * Produces a list from XML of the form:
		 * 
		 * <list>
		 *   <task>
		 *     <id>{id}</id>
		 *     <name>{name}</id>
		 *   </task>
		 *   ...
		 * </list>
		 * 
		 */
		public static function fromXMLList( obj : Object ) : ArrayCollection {
			return ElementVO.fromXMLList("task", obj);
		}
	}
}