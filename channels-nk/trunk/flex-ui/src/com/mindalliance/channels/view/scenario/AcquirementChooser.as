package com.mindalliance.channels.view.scenario
{
	import com.mindalliance.channels.common.events.GetElementEvent;
	import com.mindalliance.channels.scenario.events.GetAcquirementListEvent;
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
            CairngormHelper.fireEvent( new GetElementEvent(id, model.editorModel));
        }
	}
}