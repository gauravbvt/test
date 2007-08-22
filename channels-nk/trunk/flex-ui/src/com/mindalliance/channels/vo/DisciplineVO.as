// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.vo
{
	import com.adobe.cairngorm.vo.IValueObject;
	
	import mx.collections.ArrayCollection;

	public class DisciplineVO extends ElementVO implements IValueObject
	{
		public function DisciplineVO( id : String, 
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
		 * <discipline>
		 *   <id>{id}</id>
		 *   <name>{name}</name>
		 *   <description>{description}</description>
		 * </discipline>
		 */
		public function toXML() : XML {
			return <discipline>
						<id>{id}</id>
						<name>{name}</name>
						<description>{description}</description>
					</discipline>;
		}

		/**
		 * Expects XML of the form:
		 * <discipline>
		 *   <id>{id}</id>
		 *   <name>{name}</name>
		 *   <description>{description}</description>
		 * </discipline>
		 */
		public static function fromXML( obj : Object ) : ProjectVO {
				return new DisciplineVO(obj.id, obj.name, obj.description);
		}
		
		/**
		 * Produces a list from XML of the form:
		 * 
		 * <list>
		 *   <discipline>
		 *     <id>{id}</id>
		 *     <name>{name}</id>
		 *   </discipline>
		 *   ...
		 * </list>
		 * 
		 */
		public static function fromXMLList( obj : Object ) : ArrayCollection {
			return ElementVO.fromXMLList("discipline", obj);
		}
	}
}