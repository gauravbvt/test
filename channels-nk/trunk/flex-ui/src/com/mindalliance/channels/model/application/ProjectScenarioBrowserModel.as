package com.mindalliance.channels.model.application
{
	import com.mindalliance.channels.vo.ProjectVO;
	import com.mindalliance.channels.vo.ScenarioVO;
	
	import mx.collections.ArrayCollection;
	
	[Bindable]
	public class ProjectScenarioBrowserModel
	{
		public var projectList : ArrayCollection;
		
		public var selectedProjectId : String;
		
		public var selectedProject : ProjectVO;
		
		public var scenarioList : ArrayCollection;
		
		public var selectedScenarioId : String;
		
		public var selectedScenario : ScenarioVO;

	}
}