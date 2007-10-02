package com.mindalliance.channels.view.people
{
	import com.mindalliance.channels.events.people.GetOrganizationEvent;
	import com.mindalliance.channels.events.people.GetOrganizationListEvent;
	import com.mindalliance.channels.util.CairngormHelper;
	import com.mindalliance.channels.view.common.Chooser;
	import com.mindalliance.channels.view.UtilFuncs;
	import com.mindalliance.channels.events.people.CreateOrganizationEvent;
	import com.mindalliance.channels.vo.common.ElementVO;
	import com.mindalliance.channels.events.people.DeleteOrganizationEvent;
	import mx.core.ClassFactory;
	import com.adobe.cairngorm.control.CairngormEvent;

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
        
        override protected function btnAddClicked():void {
    		UtilFuncs.getUserTextInput(this,
				function anon(orgName:String):void {
					CairngormHelper.fireEvent(new CreateOrganizationEvent(orgName)) ;
					//listElements.scrollToIndex(list.length - 1) ;
					// Set last element selected and editor in edit state somehow
				}, "Enter Name of New Organization", true) ;
        }
        
        override protected function btnRemoveClicked():void {
         	for each (var item:Object in listElements.selectedItems) {
        		CairngormHelper.fireEvent(new DeleteOrganizationEvent((item as ElementVO).id)) ;
        	}
        	
        }
	}
}