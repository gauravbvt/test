// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.vo
{
	import com.adobe.cairngorm.vo.IValueObject;
	
	import mx.collections.ArrayCollection;

	public class CategoryVO extends ElementVO implements IValueObject
	{
		public function CategoryVO( id : String, 
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
		 * <Category>
		 *   <id>{id}</id>
		 *   <name>{name}</name>
		 *   <description>{description}</description>
		 * </Category>
		 */
		public function toXML() : XML {
			return <Category>
						<id>{id}</id>
						<name>{name}</name>
						<description>{description}</description>
					</Category>;
		}

		/**
		 * Expects XML of the form:
		 * <Category>
		 *   <id>{id}</id>
		 *   <name>{name}</name>
		 *   <description>{description}</description>
		 * </Category>
		 */
		public static function fromXML( obj : Object ) : ProjectVO {
				return new CategoryVO(obj.id, obj.name, obj.description);
		}
		
		/**
		 * Produces a list from XML of the form:
		 * 
		 * <list>
		 *   <Category>
		 *     <id>{id}</id>
		 *     <name>{name}</id>
		 *   </Category>
		 *   ...
		 * </list>
		 * 
		 */
		public static function fromXMLList( obj : Object ) : ArrayCollection {
			return ElementVO.fromXMLList("Category", obj);
		}
	}
}