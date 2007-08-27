
package com.mindalliance.channels.commands.application
{
	import com.adobe.cairngorm.control.CairngormEvent;
	import com.mindalliance.channels.business.application.ProjectDelegate;
	import com.mindalliance.channels.commands.BaseDelegateCommand;
	import com.mindalliance.channels.events.application.UpdateProjectEvent;
	import com.mindalliance.channels.model.application.ProjectScenarioBrowserModel;
	import com.mindalliance.channels.util.ElementHelper;
	
	public class UpdateProjectCommand extends BaseDelegateCommand
	{
		
		override public function execute(event:CairngormEvent):void
		{
			if (model.projectScenarioBrowserModel.shouldUpdateProject) {
				log.debug("Updating project");
				var evt:UpdateProjectEvent = event as UpdateProjectEvent;
				
				var delegate:ProjectDelegate = new ProjectDelegate( this );
				model.projectScenarioBrowserModel.selectedProject = evt.project;
				
				delegate.updateElement(model.projectScenarioBrowserModel.selectedProject);
			}
		}
		
		override public function result(data:Object):void
		{
			log.debug("Project successfully updated");
			
			var obj : Object = ElementHelper.findElementById(
											model.projectScenarioBrowserModel.selectedProject.id, 
											model.projectScenarioBrowserModel.projectList);
			if (obj.name != model.projectScenarioBrowserModel.selectedProject.name) {
				obj.name = model.projectScenarioBrowserModel.selectedProject.name;	
			}
			model.projectScenarioBrowserModel.shouldUpdateProject = false;
			
		}
		
	}
}