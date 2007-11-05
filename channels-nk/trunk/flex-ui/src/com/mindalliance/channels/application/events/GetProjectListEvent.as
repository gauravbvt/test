
package com.mindalliance.channels.application.events
{
	import com.mindalliance.channels.common.events.GetElementListEvent;

	public class GetProjectListEvent extends GetElementListEvent
	{
		
		public function GetProjectListEvent() 
		{
			super( "allProjects", "projects", new Object() );
		}
	}
}