
package com.mindalliance.channels.commands.application
{
	import com.adobe.cairngorm.control.CairngormEvent;
	import com.adobe.cairngorm.control.CairngormEventDispatcher;
	import com.mindalliance.channels.business.application.ProjectDelegate;
	import com.mindalliance.channels.commands.BaseDelegateCommand;
	import com.mindalliance.channels.events.application.CreateProjectEvent;
	import com.mindalliance.channels.events.application.GetProjectListEvent;
	import com.mindalliance.channels.vo.ProjectVO;
	
	public class CreateProjectCommand extends BaseDelegateCommand
	{
		
		override public function execute(event:CairngormEvent):void
		{
			var evt:CreateProjectEvent = event as CreateProjectEvent;
			var name : String = evt.name;
			
			log.debug("Creating project " + name);
			
			var delegate:ProjectDelegate = new ProjectDelegate( this );
			delegate.createProject(name);
		}
		
		override public function result(data:Object):void
		{
			var result:ProjectVO = data as ProjectVO;
			if (result!=null) {
				log.info("Project created");
				CairngormEventDispatcher.getInstance().dispatchEvent( new GetProjectListEvent() );
			}
		}
		
	}
}