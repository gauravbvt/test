package com.mindalliance.channels.model.application
{
	import com.mindalliance.channels.model.BaseChannelsModel;
	import com.mindalliance.channels.model.ChooserModel;
	
	[Bindable]
	public class ProjectScenarioBrowserModel
	{
		
		public function ProjectScenarioBrowserModel(projectModel : ChooserModel, scenarioModel : ChooserModel) {
			this.projectModel = projectModel;
			this.scenarioModel = scenarioModel;
			
		}
			
		public var projectModel : ChooserModel;
		public var scenarioModel : ChooserModel;
	}
}