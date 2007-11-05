
package com.mindalliance.channels.application.commands
{
	import com.adobe.cairngorm.control.CairngormEvent;
	import com.mindalliance.channels.common.commands.BaseCommand;
	import com.mindalliance.channels.application.events.LoadScenarioEvent;
	import com.mindalliance.channels.scenario.events.GetAcquirementListEvent;
	import com.mindalliance.channels.scenario.events.GetArtifactListEvent;
	import com.mindalliance.channels.scenario.events.GetEventListEvent;
	import com.mindalliance.channels.scenario.events.GetTaskListEvent;
	import com.mindalliance.channels.sharingneed.events.GetSharingNeedListEvent;
	import com.mindalliance.channels.util.CairngormHelper;
	import com.mindalliance.channels.vo.ProjectVO;
	import com.mindalliance.channels.vo.ScenarioVO;
	
	public class LoadScenarioCommand extends BaseCommand
	{
		override public function execute(event:CairngormEvent):void
		{
			var evt:LoadScenarioEvent = event as LoadScenarioEvent;
			
			channelsModel.currentProject = channelsModel.getElementModel(channelsModel.projectScenarioBrowserModel.projectModel.editorModel.id).data as ProjectVO;
			channelsModel.currentScenario = channelsModel.getElementModel(channelsModel.projectScenarioBrowserModel.scenarioModel.editorModel.id).data as ScenarioVO;	
			
            CairngormHelper.fireEvent(new GetTaskListEvent(evt.id));
            CairngormHelper.fireEvent(new GetEventListEvent(evt.id));
            CairngormHelper.fireEvent(new GetArtifactListEvent(evt.id));
            CairngormHelper.fireEvent(new GetAcquirementListEvent(evt.id));
            CairngormHelper.fireEvent(new GetSharingNeedListEvent(evt.id));
			log.debug("Loaded scenario {0}", [evt.id]);
		}
	}
}