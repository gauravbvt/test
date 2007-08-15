
package com.mindalliance.channels.events.application
{
	import com.adobe.cairngorm.control.CairngormEvent;

	public class CreateProjectEvent extends CairngormEvent
	{
		public static const CreateProject_Event:String = "<CreateProjectEvent>";
		public var name : String;
		
		
		public function CreateProjectEvent(name : String) 
		{
			super( CreateProject_Event );
			this.name = name;
		}
	}
}