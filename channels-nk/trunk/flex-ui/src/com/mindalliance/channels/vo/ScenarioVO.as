
package com.mindalliance.channels.vo
{
	import com.adobe.cairngorm.vo.IValueObject;
	import mx.collections.ArrayCollection;
	
	[Bindable]
	public class ScenarioVO extends ElementVO implements IValueObject
	{
		public function ScenarioVO(id : String, name : String, description : String, projectId : String) {
			this.name = name;
			this.id = id;
			this.projectId = projectId;
			this.description = description;
		}
		
		private var _projectId : String;
		
		public function get projectId() : String {
			return _projectId;
		}
		
		public function set projectId(projectId : String) : void {
			_projectId = projectId;
		}
		
		
		/**
		 * Produces XML of the form:
		 * 
		 * <scenario>
		 *   <id>{id}</id>
		 *   <name>{name}</name>
		 *   <description>{description}</description>
		 * </scenario>
		 */
		public function toXML() : XML {
			return <scenario schema="/channels/schema/project.rng">
						<id>{id}</id>
						<name>{name}</name>
						<description>{description}</description>
						<projectId>{projectId}</projectId>
					</scenario>;
		
		}
		/**
		 * Expects XML of the form:
		 * <scenario>
		 *   <id>{id}</id>
		 *   <name>{name}</id>
		 *   <description>{description}</description>
		 *   <projectId>{projectId}</projectId>
		 * </scenario>
		 */
		public static function fromXML( obj : Object ) : ScenarioVO {
				return new ScenarioVO(obj.id, obj.name, obj.description, obj.projectId);
		}
		
		/**
		 * Produces a list from XML of the form:
		 * 
		 * <list>
		 *   <scenario>
		 *     <id>{id}</id>
		 *     <name>{name}</id>
		 *   </scenario>
		 *   ...
		 * </list>
		 * 
		 */
		public static function fromXMLList( obj : Object ) : ArrayCollection {
			return ElementVO.fromXMLList("scenario", obj);
		}
		
	}
}