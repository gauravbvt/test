
package com.mindalliance.channels.vo
{
	import com.adobe.cairngorm.vo.IValueObject;
	import mx.collections.ArrayCollection;
	
	[Bindable]
	public class ScenarioVO extends ElementVO implements IValueObject
	{
		public function ScenarioVO(id : String, name : String, description : String, projectId : ElementVO) {
			this.name = name;
			this.id = id;
			this.project = project;
			this.description = description;
		}
		
		private var _project : ElementVO;
		
		public function get project() : ElementVO {
			return _project;
		}
		
		public function set project(project : ElementVO) : void {
			_project = project;
		}
		
	}
}