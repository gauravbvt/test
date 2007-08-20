
package com.mindalliance.channels.events.application
{
	import com.adobe.cairngorm.control.CairngormEvent;

	public class CreateScenarioEvent extends CairngormEvent
	{
		public static const CreateScenario_Event:String = "<CreateScenarioEvent>";
		
		public var name : String;
		public var projectId : String;
		
		public function CreateScenarioEvent(name : String, projectId : String) 
		{
			super( CreateScenario_Event );
			this.name = name;
			this.projectId = projectId;
		}
	}
}