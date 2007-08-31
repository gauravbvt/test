
package com.mindalliance.channels.commands.application
{
	import com.adobe.cairngorm.control.CairngormEvent;
	import com.adobe.cairngorm.control.CairngormEventDispatcher;
	import com.mindalliance.channels.business.application.ProjectDelegate;
	import com.mindalliance.channels.commands.BaseDelegateCommand;
	import com.mindalliance.channels.events.application.GetProjectEvent;
	import com.mindalliance.channels.events.application.GetProjectListEvent;
	import com.mindalliance.channels.model.application.ProjectScenarioBrowserModel;
	
	import mx.collections.ArrayCollection;
	
	public class GetProjectListCommand extends BaseDelegateCommand
	{
		
		override public function execute(event:CairngormEvent):void
		{
			var evt:GetProjectListEvent = event as GetProjectListEvent;
			var delegate:ProjectDelegate = new ProjectDelegate( this );
			log.debug("Retrieving project list");
			delegate.getProjectList();
		}
		
		override public function result(data:Object):void
		{
			channelsModel.projectScenarioBrowserModel.projectList = (data as ArrayCollection);
			log.debug("Successfully retrieved project list");
        	CairngormEventDispatcher.getInstance().dispatchEvent( new GetProjectEvent(null) );
		}
		
		override public function fault(info:Object):void
		{
			channelsModel.projectScenarioBrowserModel.projectList = null;
        	CairngormEventDispatcher.getInstance().dispatchEvent( new GetProjectEvent(null) );
			super.fault(info);
		}
		
	}
}