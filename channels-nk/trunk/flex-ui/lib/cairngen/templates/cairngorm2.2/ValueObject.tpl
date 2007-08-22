// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package @namespace@.@vo.dir@
{
	import com.adobe.cairngorm.vo.IValueObject;
	
	import mx.collections.ArrayCollection;

	public class @vo@VO extends ElementVO implements IValueObject
	{
		public function @vo@VO( id : String, 
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
		 * <@vo@>
		 *   <id>{id}</id>
		 *   <name>{name}</name>
		 *   <description>{description}</description>
		 * </@vo@>
		 */
		public function toXML() : XML {
			return <@vo@>
						<id>{id}</id>
						<name>{name}</name>
						<description>{description}</description>
					</@vo@>;
		}

		/**
		 * Expects XML of the form:
		 * <@vo@>
		 *   <id>{id}</id>
		 *   <name>{name}</name>
		 *   <description>{description}</description>
		 * </@vo@>
		 */
		public static function fromXML( obj : Object ) : ProjectVO {
				return new @vo@VO(obj.id, obj.name, obj.description);
		}
		
		/**
		 * Produces a list from XML of the form:
		 * 
		 * <list>
		 *   <@vo@>
		 *     <id>{id}</id>
		 *     <name>{name}</id>
		 *   </@vo@>
		 *   ...
		 * </list>
		 * 
		 */
		public static function fromXMLList( obj : Object ) : ArrayCollection {
			return ElementVO.fromXMLList("@vo@", obj);
		}
	}
}