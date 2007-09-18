package com.mindalliance.channels.view.people
{
	import com.mindalliance.channels.events.people.CreatePersonEvent;
	import com.mindalliance.channels.events.people.DeletePersonEvent;
	import com.mindalliance.channels.events.people.GetPersonEvent;
	import com.mindalliance.channels.events.people.GetPersonListEvent;
	import com.mindalliance.channels.util.CairngormHelper;
	import com.mindalliance.channels.view.common.Chooser;
	
	import mx.managers.PopUpManager;
	import com.mindalliance.channels.vo.common.ElementVO;

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
        
        override protected function btnAddClicked():void {
    		var pc:PersonCreator = PopUpManager.createPopUp(this, PersonCreator, true) as PersonCreator ;
			pc.resultHandler = function anon(firstname:String, lastname:String):void {
				CairngormHelper.fireEvent(new CreatePersonEvent(firstname, lastname)) ;
				listElements.scrollToIndex(list.length - 1) ;
			}
        }
        
        override protected function btnRemoveClicked():void {
         	for each (var item:Object in listElements.selectedItems) {
        		CairngormHelper.fireEvent(new DeletePersonEvent((item as ElementVO).id)) ;
        	}
        }        
	}
}