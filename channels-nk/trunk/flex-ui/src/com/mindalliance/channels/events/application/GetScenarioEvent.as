
package com.mindalliance.channels.events.application
{
	import com.adobe.cairngorm.control.CairngormEvent;

	public class GetScenarioEvent extends CairngormEvent
	{
		public static const GetScenario_Event:String = "<GetScenarioEvent>";
		
		public var id : String;
		
		public function GetScenarioEvent( id : String ) 
		{
			super( GetScenario_Event );
			this.id = id;
		}
	}
}