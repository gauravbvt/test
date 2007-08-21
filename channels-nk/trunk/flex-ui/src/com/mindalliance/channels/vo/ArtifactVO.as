// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.vo
{
	import com.adobe.cairngorm.vo.IValueObject;
	
	import mx.collections.ArrayCollection;

	public class ArtifactVO extends ElementVO implements IValueObject
	{
		public function ArtifactVO( id : String, 
								name : String, 
								description : String,								
								categories : ArrayCollection,
								information : ArrayCollection ) {
			this.id = id;
			this.name = name;
			this.description = description;
			_categories =categories;
			_information = information;
		}

		private var _categories : ArrayCollection;
		private var _information : ArrayCollection;

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
		 * <artifact>
		 *   <id>{id}</id>
		 *   <name>{name}</name>
		 *   <description>{description}</description>
		 * 	 <categories>
		 *     <categoryId>{categoryId}</categoryId>
		 *     ...  
		 *   </categories>
		 *   <information>
		 *     <element>{elementTopic}</element>
		 *   </information>
		 * </artifact>
		 */
		public function toXML() : XML {
			var categoriesXML : XML = <categories></categories>;
			for each (var category in categories) {
				categoriesXML.appendChild(<category><id>{category.id}</id></category>);
				
			}
			var informationXML : XML = <information></information>;
			for each (var element in information) {
				informationXML.appendChild(<element><topic>{element.topic}</topic></element>);
			}			
			
			
			var artifactXML =  <artifact>
						<id>{id}</id>
						<name>{name}</name>
						<description>{description}</description>
					</artifact>;
			
			artifactXML.appendChild(categoriesXML);
			artifactXML.appendChild(informationXML);
			return artifactXML;
			
		}

		/**
		 * Expects XML of the form:
		 * <artifact>
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
		 * </artifact>
		 */
		public static function fromXML( obj : Object ) : ProjectVO {
				return new ArtifactVO(  obj.id, 
										obj.name, 
										obj.description,
										ElementVO.fromXMLList("category", obj.categories),
										ElementVO.fromXMLList("element", obj.information));
		}
		
		/**
		 * Produces a list from XML of the form:
		 * 
		 * <list>
		 *   <artifact>
		 *     <id>{id}</id>
		 *     <name>{name}</id>
		 *   </artifact>
		 *   ...
		 * </list>
		 * 
		 */
		public static function fromXMLList( obj : Object ) : ArrayCollection {
			return ElementVO.fromXMLList("artifact", obj);
		}
	}
}