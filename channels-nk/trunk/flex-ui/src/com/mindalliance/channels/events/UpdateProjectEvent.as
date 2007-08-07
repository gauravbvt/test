
package com.mindalliance.channels.events
{
	import com.adobe.cairngorm.control.CairngormEvent;

	public class UpdateProjectEvent extends CairngormEvent
	{
		public static const UpdateProject_Event:String = "<UpdateProjectEvent>";
		
		public function UpdateProjectEvent() 
		{
			super( UpdateProject_Event );
		}
	}
}