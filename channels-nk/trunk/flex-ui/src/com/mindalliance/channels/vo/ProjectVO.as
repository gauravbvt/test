
package com.mindalliance.channels.vo
{
	import com.adobe.cairngorm.vo.IValueObject;
    import com.mindalliance.channels.vo.common.ElementVO;

	import mx.collections.ArrayCollection;
	[Bindable]
	public class ProjectVO extends ElementVO implements IValueObject
	{
		public function ProjectVO( id:String,
		                           name:String,
		                           description:String,
		                           manager:ElementVO = null) {
			super(id, name, description);
			this.manager = manager;
		}
		
	
		private var _manager:ElementVO;
		
		public function get manager() : ElementVO {
			return _manager;
		}
		
		public function set manager(manager: ElementVO) : void {
			this._manager = manager;	
		}

	}
}