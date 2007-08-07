
package com.mindalliance.channels.events
{
	import com.adobe.cairngorm.control.CairngormEvent;

	public class CreateProjectEvent extends CairngormEvent
	{
		public static const CreateProject_Event:String = "<CreateProjectEvent>";
		
		public function CreateProjectEvent() 
		{
			super( CreateProject_Event );
		}
	}
}