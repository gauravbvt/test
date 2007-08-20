
package com.mindalliance.channels.vo
{
	import com.adobe.cairngorm.vo.IValueObject;
	
	[Bindable]
	public class ScenarioVO extends ElementVO implements IValueObject
	{
		public function ScenarioVO(id : String, name : String, projectId : String, description : String) {
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
		
		public function toXML() : XML {
			return <scenario>
						<id>{id}</id>
						<name>{name}</name>
						<description>{description}</description>
					</scenario>;
		
		}
		
	}
}