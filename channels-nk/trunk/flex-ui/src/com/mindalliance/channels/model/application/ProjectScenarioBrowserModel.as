package com.mindalliance.channels.model.application
{
	import com.mindalliance.channels.vo.ProjectVO;
	import com.mindalliance.channels.vo.ScenarioVO;
	
	import mx.collections.ArrayCollection;
	
	[Bindable]
	public class ProjectScenarioBrowserModel
	{
		public var projectList : ArrayCollection;
		
		public var selectedProject : ProjectVO;
		
		public var updateProject : Boolean = false;
		
		public var scenarioList : ArrayCollection;
		
		public var selectedScenario : ScenarioVO;
		
		public var updateScenario : Boolean = false;

	}
}