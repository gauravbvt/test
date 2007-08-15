
package com.mindalliance.channels.events.application
{
	import com.adobe.cairngorm.control.CairngormEvent;

	public class DeleteProjectEvent extends CairngormEvent
	{
		public static const DeleteProject_Event:String = "<DeleteProjectEvent>";
		
		public var id : String;
		
		public function DeleteProjectEvent(id:String) 
		{
			super( DeleteProject_Event );
			this.id = id;
		}
	}
}