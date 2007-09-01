
package com.mindalliance.channels.commands.application
{
	import com.adobe.cairngorm.control.CairngormEvent;
	import com.mindalliance.channels.business.application.ProjectDelegate;
	import com.mindalliance.channels.commands.BaseDelegateCommand;
	import com.mindalliance.channels.events.application.UpdateProjectEvent;
	import com.mindalliance.channels.util.ElementHelper;
	import com.mindalliance.channels.vo.common.ElementVO;
	
	public class UpdateProjectCommand extends BaseDelegateCommand
	{
		
		override public function execute(event:CairngormEvent):void
		{
			if (channelsModel.projectScenarioBrowserModel.shouldUpdateProject) {
				log.debug("Updating project");
				var evt:UpdateProjectEvent = event as UpdateProjectEvent;
				
				var delegate:ProjectDelegate = new ProjectDelegate( this );
				channelsModel.projectScenarioBrowserModel.selectedProject = evt.project;
				
				delegate.updateElement(channelsModel.projectScenarioBrowserModel.selectedProject);
			}
		}
		
		override public function result(data:Object):void
		{
			log.debug("Project successfully updated");
			
			var obj : ElementVO = ElementHelper.findElementById(
											channelsModel.projectScenarioBrowserModel.selectedProject.id, 
											channelsModel.projectScenarioBrowserModel.projectList);
			if (obj.name != channelsModel.projectScenarioBrowserModel.selectedProject.name) {
				obj.name = channelsModel.projectScenarioBrowserModel.selectedProject.name;	
			}
			channelsModel.projectScenarioBrowserModel.shouldUpdateProject = false;
			
		}
		
	}
}