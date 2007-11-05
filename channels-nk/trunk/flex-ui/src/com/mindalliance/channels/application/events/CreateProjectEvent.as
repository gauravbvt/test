
package com.mindalliance.channels.application.events
{
	import com.mindalliance.channels.common.events.CreateElementEvent;

	public class CreateProjectEvent extends CreateElementEvent
	{
		
		public function CreateProjectEvent(name : String) 
		{
			super( "project", {"name" : name} );
		}
	}
}