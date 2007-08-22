// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.vo
{
	import com.adobe.cairngorm.vo.IValueObject;
	
	import mx.collections.ArrayCollection;

	public class CategorizedElementVO extends ElementVO implements IValueObject
	{
		public function CategorizedElementVO( id : String, 
								name : String, 
								projectId : String, 
								description : String,
								categories : ArrayCollection,
								information : ArrayCollection  ) {
			this.id = id;
			this.name = name;
			this.description = description;
			this.categories = categories;
			this.information = information
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
		 * <categorizedElement>
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
		 * </categorizedElement>
		 */
		public function toXML() : XML {
			var xml =  <categorizedElement>
						<id>{id}</id>
						<name>{name}</name>
						<description>{description}</description>
					</categorizedElement>;
			
			xml.appendChild(this.generateElementListXML("categories", "categoryId", categories);
			xml.appendChild(this.generateXMLInformationList(information));
			return xml;
			
		}

		/**
		 * Expects XML of the form:
		 * <categorizedElement>
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
		 * </categorizedElement>
		 */
		public static function fromXML( obj : Object ) : ProjectVO {
				return new CategorizedElementVO(  obj.id, 
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
			return ElementVO.fromXMLList("categorizedElement", obj);
		}
		
		protected function generateXMLInformationList(list : ArrayCollection) : XML {
			var xml : XML = <information></information>;
			for each (var element in list) {
				informationXML.appendChild(<element><topic>{element.topic}</topic></element>);
			}		
			return xml;
		}
	
	}
}