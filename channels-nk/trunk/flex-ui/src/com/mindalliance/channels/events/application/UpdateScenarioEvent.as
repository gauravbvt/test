
package com.mindalliance.channels.events.application
{
	import com.adobe.cairngorm.control.CairngormEvent;

	public class UpdateScenarioEvent extends CairngormEvent
	{
		public static const UpdateScenario_Event:String = "<UpdateScenarioEvent>";
		
		public var id : String;
		
		public function UpdateScenarioEvent(id : String) 
		{
			super( UpdateScenario_Event );
			this.id = id;
		}
	}
}