package com.mindalliance.channels.view.scenario
{
	import com.mindalliance.channels.events.scenario.GetAcquirementEvent;
	import com.mindalliance.channels.events.scenario.GetAcquirementListEvent;
	import com.mindalliance.channels.util.CairngormHelper;
	import com.mindalliance.channels.view.common.Chooser;

	public class AcquirementChooser extends Chooser
	{
		public function AcquirementChooser()
		{
			super();
			elementListKey="acquirements";
			elementName="Acquirements";            
            editor = new AcquirementEditor();
		}
		
	    override protected function populateList() : void {
            CairngormHelper.fireEvent( new GetAcquirementListEvent(channelsModel.currentScenario.id) ); 
        }
        
        override protected function populateElement(id : String) : void {
            CairngormHelper.fireEvent( new GetAcquirementEvent(id, model.editorModel));
        }
	}
}