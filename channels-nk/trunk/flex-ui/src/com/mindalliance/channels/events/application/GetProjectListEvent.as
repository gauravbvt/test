
package com.mindalliance.channels.events.application
{
	import com.adobe.cairngorm.control.CairngormEvent;

	public class GetProjectListEvent extends CairngormEvent
	{
		public static const GetProjectList_Event:String = "<GetProjectListEvent>";
		
		public function GetProjectListEvent() 
		{
			super( GetProjectList_Event );
		}
	}
}