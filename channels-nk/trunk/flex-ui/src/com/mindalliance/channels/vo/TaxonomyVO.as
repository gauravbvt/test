// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.vo
{
	import com.adobe.cairngorm.vo.IValueObject;
	
	import mx.collections.ArrayCollection;

	public class TaxonomyVO extends ElementVO implements IValueObject
	{
		public function TaxonomyVO( id : String, 
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
		 * <taxonomy>
		 *   <id>{id}</id>
		 *   <name>{name}</name>
		 *   <description>{description}</description>
		 * </taxonomy>
		 */
		public function toXML() : XML {
			return <Taxonomy>
						<id>{id}</id>
						<name>{name}</name>
						<description>{description}</description>
					</Taxonomy>;
		}

		/**
		 * Expects XML of the form:
		 * <taxonomy>
		 *   <id>{id}</id>
		 *   <name>{name}</name>
		 *   <description>{description}</description>
		 * </taxonomy>
		 */
		public static function fromXML( obj : Object ) : ProjectVO {
				return new TaxonomyVO(obj.id, obj.name, obj.description);
		}
		
		/**
		 * Produces a list from XML of the form:
		 * 
		 * <list>
		 *   <taxonomy>
		 *     <id>{id}</id>
		 *     <name>{name}</id>
		 *   </taxonomy>
		 *   ...
		 * </list>
		 * 
		 */
		public static function fromXMLList( obj : Object ) : ArrayCollection {
			return ElementVO.fromXMLList("Taxonomy", obj);
		}
	}
}