package com.mindalliance.channels.people.view
{
	import com.mindalliance.channels.common.events.DeleteElementEvent;
	import com.mindalliance.channels.common.events.GetElementEvent;
	import com.mindalliance.channels.people.events.CreateOrganizationEvent;
	import com.mindalliance.channels.people.events.GetOrganizationListEvent;
	import com.mindalliance.channels.util.CairngormHelper;
	import com.mindalliance.channels.util.ViewUtils;
	import com.mindalliance.channels.common.view.Chooser;
	import com.mindalliance.channels.vo.common.ElementVO;

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
            CairngormHelper.fireEvent( new GetElementEvent(id, model.editorModel));
        }
        
        override protected function btnAddClicked():void {
    		ViewUtils.getUserTextInput(this,
				function anon(orgName:String):void {
					CairngormHelper.fireEvent(new CreateOrganizationEvent(orgName)) ;
					//listElements.scrollToIndex(list.length - 1) ;
					// Set last element selected and editor in edit state somehow
				}, "Enter Name of New Organization", true) ;
        }
        
        override protected function btnRemoveClicked():void {
         	for each (var item:Object in listElements.selectedItems) {
        		CairngormHelper.fireEvent(new DeleteElementEvent((item as ElementVO).id)) ;
        	}
        	
        }
	}
}