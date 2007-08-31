
package com.mindalliance.channels.commands.application
{
	import com.adobe.cairngorm.control.CairngormEvent;
	import com.mindalliance.channels.business.application.ProjectDelegate;
	import com.mindalliance.channels.commands.BaseDelegateCommand;
	import com.mindalliance.channels.events.application.GetProjectEvent;
	import com.mindalliance.channels.model.application.ProjectScenarioBrowserModel;
	import com.mindalliance.channels.vo.ProjectVO;
	
	public class GetProjectCommand extends BaseDelegateCommand
	{
		
		override public function execute(event:CairngormEvent):void
		{
			var evt:GetProjectEvent = event as GetProjectEvent;			
			var id : String = evt.id;
			
			if (id != null) {
				log.debug("Retrieving project {0}", [id]);
				var delegate:ProjectDelegate = new ProjectDelegate( this );
				delegate.getElement(id);
			} else {
				log.debug("Setting selected Project to null");
				channelsModel.projectScenarioBrowserModel.selectedProject = null;
			}
			
		}
		
		override public function result(data:Object):void
		{
			var result:ProjectVO = (data as ProjectVO);
			if (result != null) {
				log.debug("Setting selected project to {0}", [result.id]);
				channelsModel.projectScenarioBrowserModel.selectedProject = result;// new ProjectVO(result.id, result.name, result.description, result.manager);
			} else {
				log.warn("Unable to retrieve project");	
			}
		}

	}
}