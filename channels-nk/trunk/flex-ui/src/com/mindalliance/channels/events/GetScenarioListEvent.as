
package com.mindalliance.channels.events
{
	import com.adobe.cairngorm.control.CairngormEvent;

	public class GetScenarioListEvent extends CairngormEvent
	{
		public static const GetScenarioList_Event:String = "<GetScenarioListEvent>";
		
		public function GetScenarioListEvent() 
		{
			super( GetScenarioList_Event );
		}
	}
}