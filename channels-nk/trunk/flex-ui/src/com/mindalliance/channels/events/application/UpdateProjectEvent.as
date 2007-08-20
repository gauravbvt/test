
package com.mindalliance.channels.events.application
{
	import com.adobe.cairngorm.control.CairngormEvent;

	public class UpdateProjectEvent extends CairngormEvent
	{
		public static const UpdateProject_Event:String = "<UpdateProjectEvent>";
		
		public var id : String;
		
		public function UpdateProjectEvent(id : String) 
		{
			super( UpdateProject_Event );
			this.id = id;
		}
	}
}