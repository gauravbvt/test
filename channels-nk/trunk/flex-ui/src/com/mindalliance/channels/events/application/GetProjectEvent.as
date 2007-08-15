
package com.mindalliance.channels.events.application
{
	import com.adobe.cairngorm.control.CairngormEvent;

	public class GetProjectEvent extends CairngormEvent
	{
		public static const GetProject_Event:String = "<GetProjectEvent>";
		
		public var id : String;
		
		public function GetProjectEvent(id : String) 
		{
			super( GetProject_Event );
			this.id = id;
		}
	}
}