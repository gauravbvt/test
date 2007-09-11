package com.mindalliance.channels.view.people
{
	import com.mindalliance.channels.events.people.GetRoleEvent;
	import com.mindalliance.channels.events.people.GetRoleListEvent;
	import com.mindalliance.channels.util.CairngormHelper;
	import com.mindalliance.channels.view.common.Chooser;

	public class RoleChooser extends Chooser
	{
		public function RoleChooser()
		{
			super();
			elementListKey="roles";
			elementName="Roles";            
            editor = new RoleEditor();
		}
		
	    override protected function populateList() : void {
            CairngormHelper.fireEvent( new GetRoleListEvent() ); 
        }
        
        override protected function populateElement(id : String) : void {
            CairngormHelper.fireEvent( new GetRoleEvent(id, model.editorModel));
        }
	}
}