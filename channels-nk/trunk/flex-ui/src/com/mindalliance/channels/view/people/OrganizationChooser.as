package com.mindalliance.channels.view.people
{
	import com.mindalliance.channels.events.people.GetOrganizationEvent;
	import com.mindalliance.channels.events.people.GetOrganizationListEvent;
	import com.mindalliance.channels.util.CairngormHelper;
	import com.mindalliance.channels.view.common.Chooser;

	public class OrganizationChooser extends Chooser
	{
		public function OrganizationChooser()
		{
			super();
			elementListKey="organizations";
			elementName="Organizations";            
            editor = new OrganizationEditor();
		}
		
	    override protected function populateList() : void {
            CairngormHelper.fireEvent( new GetOrganizationListEvent() ); 
        }
        
        override protected function populateElement(id : String) : void {
            CairngormHelper.fireEvent( new GetOrganizationEvent(id, model.editorModel));
        }
	}
}