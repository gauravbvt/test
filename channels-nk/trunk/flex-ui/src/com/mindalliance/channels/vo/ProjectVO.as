
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
		
		
		public var description:String;
		public var manager:String;
		
	}
}