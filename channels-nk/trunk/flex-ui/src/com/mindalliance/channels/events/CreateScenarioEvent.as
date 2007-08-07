
package com.mindalliance.channels.events
{
	import com.adobe.cairngorm.control.CairngormEvent;

	public class CreateScenarioEvent extends CairngormEvent
	{
		public static const CreateScenario_Event:String = "<CreateScenarioEvent>";
		
		public function CreateScenarioEvent() 
		{
			super( CreateScenario_Event );
		}
	}
}