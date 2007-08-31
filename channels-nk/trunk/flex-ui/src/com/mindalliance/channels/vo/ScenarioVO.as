
package com.mindalliance.channels.vo
{
	import com.adobe.cairngorm.vo.IValueObject;
	import mx.collections.ArrayCollection;
    import com.mindalliance.channels.vo.common.ElementVO;
	
	[Bindable]
	public class ScenarioVO extends ElementVO implements IValueObject
	{
		public function ScenarioVO(id : String, name : String, description : String, project : ElementVO) {

			super(id, name, description);
			this.project = project;
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