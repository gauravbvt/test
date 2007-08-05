package com.mindalliance.channels.model
{
	import com.mindalliance.channels.vo.ProjectVO;
	import com.mindalliance.channels.vo.ScenarioVO;
	
	import mx.collections.ArrayCollection;
	
	[Bindable]
	public class ProjectScenarioBrowserModel
	{
		public var projectList : ArrayCollection;
		
		public var selectedProject : ProjectVO;
		
		public var scenarioList : ArrayCollection;
		
		public var selectedScenario : ScenarioVO;

		public var canEditProject:Boolean = false ;

		public var canAddProject:Boolean = false ;

		public var canRemoveProject:Boolean = false ;

		public var canEditScenario:Boolean = false ;

		public var canAddScenario:Boolean = false ;

		public var canRemoveScenario:Boolean = false ;
	}
}