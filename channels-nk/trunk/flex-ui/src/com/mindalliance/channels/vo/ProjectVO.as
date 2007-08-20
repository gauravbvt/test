
package com.mindalliance.channels.vo
{
	import com.adobe.cairngorm.vo.IValueObject;

	import mx.collections.ArrayCollection;
	[Bindable]
	public class ProjectVO extends ElementVO implements IValueObject
	{
		public function ProjectVO( id:String,
		                           name:String,
		                           description:String,
		                           manager:ElementVO ) {
			this.id = id;
			this.name = name;
			this.description = description;
			this.manager = manager;
		}
		
	
		private var _manager:ElementVO;
		
		public function get manager() : ElementVO {
			return _manager;
		}
		
		public function set manager(manager: ElementVO) : void {
			this._manager = manager;	
		}
		/**
		 * Produces XML of the form:
		 * 
		 * <project>
		 *   <id>{id}</id>
		 *   <name>{name}</name>
		 *   <description>{description}</description>
		 *   <managerId>{managerId}</managerId>
		 * </project>
		 */
		public function toXML() : XML {
			return <project>
						<id>{id}</id>
						<name>{name}</name>
						<description>{description}</description>
						<managerId>{manager}</managerId>	
					</project>;
		
		}

		/**
		 * Expects XML of the form:
		 * <project>
		 *   <id>{id}</id>
		 *   <name>{name}</name>
		 *   <description>{description}</description>
		 *   <manager>
		 *     <id>{managerId}</id>
		 *     <name>{managerName}</name>
		 *   </manager>
		 * </project>
		 */
		public static function fromXML( obj : Object ) : ProjectVO {
				return new ProjectVO(obj.id, obj.name, obj.description, new ElementVO(obj.manager.id, obj.manager.name));
		}
		
		/**
		 * Produces a list from XML of the form:
		 * 
		 * <list>
		 *   <project>
		 *     <id>{id}</id>
		 *     <name>{name}</id>
		 *   </project>
		 *   ...
		 * </list>
		 * 
		 */
		public static function fromXMLList( obj : Object ) : ArrayCollection {
			return ElementVO.fromXMLList("project", obj);
		}
	}
}