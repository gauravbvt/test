// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.vo
{
	import com.adobe.cairngorm.vo.IValueObject;
	
	import mx.collections.ArrayCollection;
	import com.yworks.support.Integer;

	public class TaskVO extends OccurrenceVO implements IValueObject
	{
		public function TaskVO( id : String, 
								name : String, 
								description : String,
								independent : Boolean,
								causeType : String,
								causeId : String,
								duration : Integer,
								durationUnit : String,
								categories : ArrayCollection,
								information : ArrayCollection,
								agents : ArrayCollection,
								artifacts : ArrayCollection) {
			this.id = id;
			this.name = name;
			this.description = description;
			this.independent = independent;
			this.causeType = causeType;
			this.causeId = causeId;
			this.duration =duration;
			this.durationUnit =durationUnit;
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
		 *   <cause>
		 *     <type>{event|task|independent}</type>
		 *     <id>{event or task ID}</id> <!-- optional -->
		 *   </cause>
		 *	 <duration>
		 *     <length>{duration}</length>
		 *     <unit>{unit}</unit>
		 *   </duration>
		 *   <categories>
		 *     <categoryId>{categoryId}</categoryId>
		 *     ...  
		 *   </categories>
		 *   <information>
		 *     <element>{elementTopic}</element>
		 *   </information>
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
			var agentsXML : XML = <agents></agents>;
			for each (var role in agents) {
				agentsXML.appendChild(<roleId>{role.id}</roleId>);	
			}
			
			var artifactXML : XML = <artifacts></artifacts>;
			for each (var artifact in artifacts) {
				artifactXML.appendChild(<artifactId>{artifact.id}</artifactId>);
			}
			var categoriesXML : XML = <categories></categories>;
			for each (var category in categories) {
				categoriesXML.appendChild(<category><id>{category.id}</id></category>);
				
			}
			var informationXML : XML = <information></information>;
			for each (var element in information) {
				informationXML.appendChild(<element><topic>{element.topic}</topic></element>);
			}			
			
			var taskXML : XML = <task>
						<id>{id}</id>
						<name>{name}</name>
						<description>{description}</description>
						<independent>{independent}</independent>
						<causeType>{causeType}</causeType>
						<causeId>{causeId}</causeId>
						<duration>{duration}</duration>
						<durationUnit>{durationUnit}</durationUnit>
						
					</task>;
			taskXML.appendChild(agentsXML);
			taskXML.appendChild(artifactXML);
			taskXML.appendChild(categoriesXML);
			
		}

		/**
		 * Expects XML of the form:
		 * <task>
		 *   <id>{id}</id>
		 *   <name>{name}</name>
		 *   <description>{description}</description>
		 *   <cause>
		 *     <type>{event|task|independent}</type>
		 *     <id>{event or task ID}</id> <!-- optional -->
		 *     <name>{event or task name}</name> <!-- optional -->
		 *   </cause>
		 *	 <duration>
		 *     <length>{duration}</length>
		 *     <unit>{unit}</unit>
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
									obj.independent,
									obj.causeType,
									obj.causeId,
									obj.duration,
									obj.durationUnit,
									ElementVO.fromXMLList("categories", obj.categories),
									ElementVO.fromXMLList("information", obj.information),
									ElementVO.fromXMLList("agents", obj.agents),
									ElementVO.fromXMLList("artifacts", obj.artifacts));
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