
package com.mindalliance.channels.vo
{
	import com.adobe.cairngorm.vo.IValueObject;

	[Bindable]
	public class ProjectVO extends ElementVO implements IValueObject
	{
		public function ProjectVO( id:String,
		                           name:String,
		                           description:String,
		                           manager:String ) {
			this.id = id;
			this.name = name;
			this.description = description;
			this.manager = manager;
		}
		
	
		private var _manager:String;
		
		public function get manager() : String {
			return _manager;
		}
		
		public function set manager(manager: String) : void {
			this._manager = manager;	
		}
		
		public function toXML() : XML {
			return <project>
						<id>{id}</id>
						<name>{name}</name>
						<description>{description}</description>
						//<manager>{manager}</manager>	
					</project>;
		
		}
	}
}