
package com.mindalliance.channels.events.application
{
	import com.adobe.cairngorm.control.CairngormEvent;
	import com.mindalliance.channels.vo.ProjectVO;
	
	public class UpdateProjectEvent extends CairngormEvent
	{
		public static const UpdateProject_Event:String = "<UpdateProjectEvent>";
		
		public var project : ProjectVO;
		
		public function UpdateProjectEvent(project : ProjectVO) 
		{
			super( UpdateProject_Event );
			this.project= project;
		}
	}
}