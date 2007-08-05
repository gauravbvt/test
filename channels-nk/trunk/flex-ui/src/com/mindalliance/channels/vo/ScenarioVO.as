
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
		
		public var projectId : String;
		public var description : String;
	}
}