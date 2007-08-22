// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.vo
{
	import com.adobe.cairngorm.vo.IValueObject;
	
	import mx.collections.ArrayCollection;

	public class ArtifactVO extends CategorizedElementVO implements IValueObject
	{
		public function ArtifactVO( id : String, 
								name : String, 
								description : String,								
								categories : ArrayCollection,
								information : ArrayCollection ) {
			this.id = id;
			this.name = name;
			this.description = description;
			this.categories =categories;
			this.information = information;
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
			var xml =  <artifact>
						<id>{id}</id>
						<name>{name}</name>
						<description>{description}</description>
					</artifact>;
			
			xml.appendChild(this.generateElementListXML("categories", "categoryId", categories);
			xml.appendChild(this.generateXMLInformationList(information));
			return xml;
			
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