
package com.mindalliance.channels.application.events
{
	import com.adobe.cairngorm.control.CairngormEvent;

	public class LoadScenarioEvent extends CairngormEvent
	{
		public static const LoadScenario_Event:String = "<LoadScenarioEvent>";
		public var id : String;
		public function LoadScenarioEvent(id : String) 
		{
			super( LoadScenario_Event );
			this.id = id;
		}
	}
}