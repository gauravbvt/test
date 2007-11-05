
package com.mindalliance.channels.application.events
{
	import com.mindalliance.channels.common.events.GetElementListEvent;

	public class GetScenarioListEvent extends GetElementListEvent
	{
		
		public function GetScenarioListEvent( projectId : String ) 
		{
			super( "scenariosInProject", "scenarios", {"projectId" : projectId} );
		}
	}
}