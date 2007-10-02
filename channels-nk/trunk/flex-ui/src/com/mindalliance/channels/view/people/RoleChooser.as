package com.mindalliance.channels.view.people
{
	import com.mindalliance.channels.events.people.CreateRoleEvent;
	import com.mindalliance.channels.events.people.DeleteRoleEvent;
	import com.mindalliance.channels.events.people.GetRoleEvent;
	import com.mindalliance.channels.events.people.GetRoleListEvent;
	import com.mindalliance.channels.util.CairngormHelper;
	import com.mindalliance.channels.view.common.Chooser;
	import com.mindalliance.channels.vo.common.ElementVO;
	
	import mx.managers.PopUpManager;

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
        
        override protected function btnAddClicked():void {
    		var rc:RoleCreator = PopUpManager.createPopUp(this, RoleCreator, true) as RoleCreator ;
    		rc.resultHandler = function anon(roleName:String, orgID:String):void {
         		CairngormHelper.fireEvent(new CreateRoleEvent(roleName, orgID));
         		//listElements.scrollToIndex(list.length - 1) ;
    		}
        }
        
        override protected function btnRemoveClicked():void {
         	for each (var item:Object in listElements.selectedItems) {
        		CairngormHelper.fireEvent(new DeleteRoleEvent((item as ElementVO).id)) ;
        	}
        }        
	}
}