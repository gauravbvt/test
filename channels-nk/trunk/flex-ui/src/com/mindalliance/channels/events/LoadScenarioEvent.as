
package com.mindalliance.channels.events
{
	import com.adobe.cairngorm.control.CairngormEvent;

	public class LoadScenarioEvent extends CairngormEvent
	{
		public static const LoadScenario_Event:String = "<LoadScenarioEvent>";
		
		public function LoadScenarioEvent() 
		{
			super( LoadScenario_Event );
		}
	}
}