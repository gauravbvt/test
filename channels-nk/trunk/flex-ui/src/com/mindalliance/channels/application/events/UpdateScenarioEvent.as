
package com.mindalliance.channels.application.events
{
	import com.mindalliance.channels.common.events.UpdateElementEvent;
	import com.mindalliance.channels.model.EditorModel;
	import com.mindalliance.channels.vo.common.ElementVO;

	public class UpdateScenarioEvent extends UpdateElementEvent
	{

		
		public function UpdateScenarioEvent(model : EditorModel,
		                                  name : String, 
		                                  description : String, 
		                                  project : ElementVO) : void 
		{
			super( model, {"name" : name,
			                 "description" : description,
			                 "project" : project });
		}
	}
}