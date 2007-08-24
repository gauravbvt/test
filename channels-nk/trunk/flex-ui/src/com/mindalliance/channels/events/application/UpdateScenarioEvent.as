
package com.mindalliance.channels.events.application
{
	import com.adobe.cairngorm.control.CairngormEvent;
	import com.mindalliance.channels.vo.ScenarioVO;

	public class UpdateScenarioEvent extends CairngormEvent
	{
		public static const UpdateScenario_Event:String = "<UpdateScenarioEvent>";
		
		public var scenario : ScenarioVO
		
		public function UpdateScenarioEvent(scenario : ScenarioVO) : void 
		{
			super( UpdateScenario_Event );
			this.scenario = scenario;
		}
	}
}