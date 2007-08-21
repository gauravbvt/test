// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.vo
{
	import com.adobe.cairngorm.vo.IValueObject;
	
	import mx.collections.ArrayCollection;

	public class RepositoryVO extends ElementVO implements IValueObject
	{
		public function RepositoryVO( id : String, 
								name : String, 
								projectId : String, 
								description : String ) {
			this.id = id;
			this.name = name;
			this.description = description;
		}

		/**
		 * Produces XML of the form:
		 * 
		 * <Repository>
		 *   <id>{id}</id>
		 *   <name>{name}</name>
		 *   <description>{description}</description>
		 * </repository>
		 */
		public function toXML() : XML {
			return <repository>
						<id>{id}</id>
						<name>{name}</name>
						<description>{description}</description>
					</repository>;
		}

		/**
		 * Expects XML of the form:
		 * <repository>
		 *   <id>{id}</id>
		 *   <name>{name}</name>
		 *   <description>{description}</description>
		 * </repository>
		 */
		public static function fromXML( obj : Object ) : ProjectVO {
				return new RepositoryVO(obj.id, obj.name, obj.description);
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