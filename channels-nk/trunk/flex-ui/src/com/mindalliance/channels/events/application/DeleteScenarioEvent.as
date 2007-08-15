
package com.mindalliance.channels.events.application
{
	import com.adobe.cairngorm.control.CairngormEvent;

	public class DeleteScenarioEvent extends CairngormEvent
	{
		public static const DeleteScenario_Event:String = "<DeleteScenarioEvent>";
		
		private var id:String;
		public function DeleteScenarioEvent(id : String) 
		{
			super( DeleteScenario_Event );
			this.id = id;
		}
	}
}