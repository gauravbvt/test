// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.vo
{
	import com.adobe.cairngorm.vo.IValueObject;
	
	import mx.collections.ArrayCollection;
	import com.yworks.support.Integer;

	public class TaskVO extends ElementVO implements IValueObject
	{
		public function TaskVO( id : String, 
								name : String, 
								description : String,
								agents : ArrayCollection,
								independent : Boolean,
								causeType : String,
								causeId : String,
								duration : Integer,
								durationUnit : String,
								artifacts : ArrayCollection,
								categories : ArrayCollection) {
			this.id = id;
			this.name = name;
			this.description = description;
			_agents : ArrayCollection;
			_independent : Boolean;
			_causeType : String ;
			_causeId : String;
			_duration : Integer;
			_durationUnit : String;
			_artifacts : ArrayCollection;
			_categories : ArrayCollection;
		}

		private var _agents : ArrayCollection;
		private var _independent : Boolean;
		private var _causeType : String ;
		private var _causeId : String;
		private var _duration : Integer;
		private var _durationUnit : String;
		private var _artifacts : ArrayCollection;
		private var _categories : ArrayCollection;
		private var _information : ArrayCollection;
		
		public function get agents() : String {
			return _agents;
		}
		
		public function set agents(agents : String) : void {
			_agents = agents;
		}
		
		
		public function get independent() : Boolean {
			return _independent;
		}

		public function set independent(independent : Boolean) : void {
			_independent=independent;
		}
		
		
		public function get causeType() : String {
			return _causeType;
		}

		public function set causeType(causeType : String) : void {
			_causeType=causeType;
		}
		
		
		public function get causeId() : String {
			return _causeId;
		}

		public function set causeId(causeId : String) : void {
			_causeId=causeId;
		}
		
		public function get duration() : Integer {
			return _duration;
		}

		public function set duration(duration : Integer) : void {
			_duration=duration;
		}
		
		public function get durationUnit() : String{
			return _durationUnit;
		}

		public function set durationUnit(durationUnit : String) : void {
			_durationUnit=durationUnit;
		}
		
		public function get artifacts() : ArrayCollection {
			return _artifacts;
		}

		public function set artifacts(artifacts : ArrayCollection) : void {
			_artifacts=artifacts;
		}
		
		public function get categories() : ArrayCollection {
			return _categories;
		}

		public function set categories(categories : ArrayCollection) : void {
			_categories=categories;
		}
		
		
		public function get information() : ArrayCollection {
			return _information;
		};

		public function set information(information : ArrayCollection) : void {
			_information=information;
		}
		
		/**
		 * Produces XML of the form:
		 * 
		 * <task>
		 *   <id>{id}</id>
		 *   <name>{name}</name>
		 *   <description>{description}</description>
		 *	 <independent>{independent}</independent>
		 *	 <causeType>{causeType}</causeType>
		 *	 <causeId>{causeId}</causeId>
		 *	 <duration>{duration}</duration>
		 * 	 <durationUnit>{durationUnit}</durationUnit>
		 *   <agents>
		 *     <roleId>{roleId}</roleId>
		 *     ...
		 *   </agents>
		 *   <artifacts>
		 *      <artifactId>{artifactId}</artifactId>
		 *      ...
		 *   </artifacts>
		 *   <categories>
		 *     <categoryId>{categoryId}</categoryId>
		 *     ...  
		 *   </categories>
		 *   <information>
		 *     <element>{elementTopic}</element>
		 *   </information>
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
		 *	 <independent>{independent}</independent>
		 *	 <causeType>{causeType}</causeType>
		 *	 <causeId>{causeId}</causeId>
		 *	 <duration>{duration}</duration>
		 * 	 <durationUnit>{durationUnit}</durationUnit>
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
		 * </task>
		 */
		public static function fromXML( obj : Object ) : ProjectVO {
				return new TaskVO(obj.id, 
									obj.name, 
									obj.description,
									ElementVO.fromXMLList("agents", obj.agents),
									obj.independent,
									obj.causeType,
									obj.causeId,
									obj.duration,
									obj.durationUnit,
									ElementVO.fromXMLList("artifacts", obj.artifacts),
									ElementVO.fromXMLList("categories", obj.categories),
									ElementVO.fromXMLList("information", obj.information));
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