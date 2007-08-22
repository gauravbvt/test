// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.vo
{
	import com.adobe.cairngorm.vo.IValueObject;
	
	import mx.collections.ArrayCollection;

	public class RepositoryVO extends ArtifactVO implements IValueObject
	{
		public function RepositoryVO( id : String, 
								name : String, 
								projectId : String, 
								description : String,
								categories : ArrayCollection,
								information : ArrayCollection,
								owner : ElementVO,
								administrators : ArrayCollection,
								roles : ArrayCollection  ) {
			this.id = id;
			this.name = name;
			this.description = description;
			this.categories = categories;
			this.information = information;
			this.owner = owner;
			this.administrators = administrators;
			this.roles = roles;
		}

		private var _owner : ElementVO;
		private var _administrators : ArrayCollection;
		private var _roles : ArrayCollection;

		
		public function get owner() : ElementVO {
			return _owner;
		};

		public function set owner(owner : ElementVO) : void {
			_owner=owner;
		}
		
		public function get administrators() : ArrayCollection {
			return _administrators;
		};

		public function set administrators(administrators : ArrayCollection) : void {
			_administrators=administrators;
		}
		
		public function get roles() : ArrayCollection {
			return _roles;
		};

		public function set roles(roles : ArrayCollection) : void {
			_roles=roles;
		}
		

		/**
		 * Produces XML of the form:
		 * 
		 * <repository>
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
		 *   <ownerId>{owner.id}</ownerId>
		 *   <administrators>
		 *     <personId>{administrator.id}</personId>
		 *     ...
		 *   </administrators>
		 *   <roles>
		 *     <roleId>{role.id}</roleId>
		 *     ...
		 *   </roles>
		 * </repository>
		 */
		public function toXML() : XML {
			var xml =  <repository>
						<id>{id}</id>
						<name>{name}</name>
						<description>{description}</description>
					</repository>;
			
			xml.appendChild(this.generateElementListXML("categories", "categoryId", categories);
			xml.appendChild(this.generateXMLInformationList(information));
			xml.appendChild(<ownerId>{owner.id}</ownerId>);
			
			xml.appendChild(this.generateElementListXML("administrators", "personId", administrators);
			xml.appendChild(this.generateElementListXML("roles", "roleId", roles);
			return xml;
		}

		/**
		 * Expects XML of the form:
		 * <repository>
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
		 *   <owner>
		 *     <id>{owner.id}</id>
		 *     <name>{owner.name}</name>
		 *   </owner>
		 * 
		 *   <administrators>
		 *     <person>
		 *       <id>{administrator.id}</id>
		 *       <name>{administrator.name}</name>
		 *     </person>
		 *     ...
		 *   </administrators>
		 *   <roles>
		 *     <role>
		 *       <id>{role.id}</id>
		 *       <name>{role.name}</name>
		 *     </role>
		 *     ...
		 *   </roles>
		 * </repository>
		 */
		public static function fromXML( obj : Object ) : ProjectVO {
				return new RepositoryVO(obj.id, 
										obj.name, 
										obj.description,
										new ElementVO(obj.owner.id, obj.owner.name),
										ElementVO.fromXMLList("person", obj.administrators),
										ElementVO.fromXMLList("role", obj.roles));
		}
		
		/**
		 * Produces a list from XML of the form:
		 * 
		 * <list>
		 *   <repository>
		 *     <id>{id}</id>
		 *     <name>{name}</id>
		 *   </repository>
		 *   ...
		 * </list>
		 * 
		 */
		public static function fromXMLList( obj : Object ) : ArrayCollection {
			return ElementVO.fromXMLList("repository", obj);
		}
	}
}