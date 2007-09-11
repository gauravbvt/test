package com.mindalliance.channels.view.scenario
{
	import com.mindalliance.channels.events.scenario.GetEventEvent;
	import com.mindalliance.channels.events.scenario.GetEventListEvent;
	import com.mindalliance.channels.util.CairngormHelper;
	import com.mindalliance.channels.view.common.Chooser;

	public class EventChooser extends Chooser
	{
		public function EventChooser()
		{
			super();
			elementListKey="events";
			elementName="Events";            
            editor = new EventEditor();
		}
		
	    override protected function populateList() : void {
            CairngormHelper.fireEvent( new GetEventListEvent(channelsModel.currentScenario.id) ); 
        }
        
        override protected function populateElement(id : String) : void {
            CairngormHelper.fireEvent( new GetEventEvent(id, model.editorModel));
        }
	}
}