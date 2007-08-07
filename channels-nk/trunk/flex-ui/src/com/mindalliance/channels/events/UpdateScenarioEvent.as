
package com.mindalliance.channels.events
{
	import com.adobe.cairngorm.control.CairngormEvent;

	public class UpdateScenarioEvent extends CairngormEvent
	{
		public static const UpdateScenario_Event:String = "<UpdateScenarioEvent>";
		
		public function UpdateScenarioEvent() 
		{
			super( UpdateScenario_Event );
		}
	}
}