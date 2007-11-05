package com.mindalliance.channels.view.people
{
	import com.mindalliance.channels.common.events.DeleteElementEvent;
	import com.mindalliance.channels.common.events.GetElementEvent;
	import com.mindalliance.channels.people.events.CreateRoleEvent;
	import com.mindalliance.channels.people.events.GetRoleListEvent;
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
            CairngormHelper.fireEvent( new GetElementEvent(id, model.editorModel));
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
        		CairngormHelper.fireEvent(new DeleteElementEvent((item as ElementVO).id)) ;
        	}
        }        
	}
}