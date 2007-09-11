package com.mindalliance.channels.view.people
{
	import com.mindalliance.channels.events.people.GetPersonEvent;
	import com.mindalliance.channels.events.people.GetPersonListEvent;
	import com.mindalliance.channels.util.CairngormHelper;
	import com.mindalliance.channels.view.common.Chooser;

	public class PersonChooser extends Chooser
	{
		public function PersonChooser()
		{
			super();
			elementListKey="people";
			elementName="People";            
            editor = new PersonEditor();
		}
		
	    override protected function populateList() : void {
            CairngormHelper.fireEvent( new GetPersonListEvent() ); 
        }
        
        override protected function populateElement(id : String) : void {
            CairngormHelper.fireEvent( new GetPersonEvent(id, model.editorModel));
        }
	}
}