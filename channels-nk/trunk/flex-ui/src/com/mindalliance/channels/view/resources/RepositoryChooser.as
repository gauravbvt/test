package com.mindalliance.channels.view.resources
{
	import com.mindalliance.channels.events.resources.GetRepositoryEvent;
	import com.mindalliance.channels.events.resources.GetRepositoryListEvent;
	import com.mindalliance.channels.util.CairngormHelper;
	import com.mindalliance.channels.view.common.Chooser;

	public class RepositoryChooser extends Chooser
	{
		public function RepositoryChooser()
		{
			super();
			elementListKey="repositories";
			elementName="Repositories";            
            editor = new RepositoryEditor();
		}
		
	    override protected function populateList() : void {
            CairngormHelper.fireEvent( new GetRepositoryListEvent() ); 
        }
        
        override protected function populateElement(id : String) : void {
            CairngormHelper.fireEvent( new GetRepositoryEvent(id, model.editorModel));
        }
	}
}