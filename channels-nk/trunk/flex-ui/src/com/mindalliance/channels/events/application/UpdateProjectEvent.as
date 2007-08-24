
package com.mindalliance.channels.events.application
{
	import com.adobe.cairngorm.control.CairngormEvent;
	import com.mindalliance.channels.vo.ProjectVO;
	
	public class UpdateProjectEvent extends CairngormEvent
	{
		public static const UpdateProject_Event:String = "<UpdateProjectEvent>";
		
		public var id : String;
		public var name : String;
		public var description : String;
		
		public function UpdateProjectEvent(id : String,
										   name : String,
										   description : String) 
		{
			super( UpdateProject_Event );
			this.id = id;
			this.name = name;
			this.description = description;
		}
	}
}