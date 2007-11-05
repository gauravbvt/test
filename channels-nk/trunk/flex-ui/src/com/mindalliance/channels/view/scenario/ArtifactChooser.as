package com.mindalliance.channels.view.scenario
{
	import com.mindalliance.channels.common.events.GetElementEvent;
	import com.mindalliance.channels.scenario.events.GetArtifactListEvent;
	import com.mindalliance.channels.util.CairngormHelper;
	import com.mindalliance.channels.view.common.Chooser;

	public class ArtifactChooser extends Chooser
	{
		public function ArtifactChooser()
		{
			super();
			elementListKey="artifacts";
			elementName="Artifacts";            
            editor = new ArtifactEditor();
		}
		
	    override protected function populateList() : void {
            CairngormHelper.fireEvent( new GetArtifactListEvent(channelsModel.currentScenario.id) ); 
        }
        
        override protected function populateElement(id : String) : void {
            CairngormHelper.fireEvent( new GetElementEvent(id, model.editorModel));
        }
        
	}
}