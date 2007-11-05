
package com.mindalliance.channels.application.events
{
	import com.mindalliance.channels.common.events.UpdateElementEvent;
	import com.mindalliance.channels.model.EditorModel;
	import com.mindalliance.channels.vo.common.ElementVO;
	
	public class UpdateProjectEvent extends UpdateElementEvent
	{
		
		public function UpdateProjectEvent( model : EditorModel,
		                                name:String,
                                        description:String,
                                        manager:ElementVO) 
		{
			super( model, {"name" : name,
			                 "description" : description,
			                 "manager" : manager} );
		}
	}
}