package com.mindalliance.channels.view.scenario
{
	import com.mindalliance.channels.events.scenario.GetArtifactEvent;
	import com.mindalliance.channels.events.scenario.GetArtifactListEvent;
	import com.mindalliance.channels.util.CairngormHelper;
	import com.mindalliance.channels.view.common.Chooser;
	import com.mindalliance.channels.events.scenario.CreateArtifactEvent;

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
            CairngormHelper.fireEvent( new GetArtifactEvent(id, model.editorModel));
        }
        
	}
}