
package com.mindalliance.channels.events
{
	import com.adobe.cairngorm.control.CairngormEvent;

	public class GetScenarioListEvent extends CairngormEvent
	{
		public static const GetScenarioList_Event:String = "<GetScenarioListEvent>";
		
		public var projectId : String;
		
		public function GetScenarioListEvent( projectId : String ) 
		{
			super( GetScenarioList_Event );
			this.projectId = projectId;
		}
	}
}