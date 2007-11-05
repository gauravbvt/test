
package com.mindalliance.channels.application.events
{
	import com.mindalliance.channels.common.events.CreateElementEvent;

	public class CreateScenarioEvent extends CreateElementEvent
	{

		
		public function CreateScenarioEvent(name : String, projectId : String) 
		{
			super( "scenario", {"name" : name, "projectId" : projectId} );
			
		}
	}
}