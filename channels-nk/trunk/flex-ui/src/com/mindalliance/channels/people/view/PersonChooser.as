package com.mindalliance.channels.people.view
{
	import com.mindalliance.channels.common.events.DeleteElementEvent;
	import com.mindalliance.channels.common.events.GetElementEvent;
	import com.mindalliance.channels.people.events.CreatePersonEvent;
	import com.mindalliance.channels.people.events.GetPersonListEvent;
	import com.mindalliance.channels.util.CairngormHelper;
	import com.mindalliance.channels.common.view.Chooser;
	import com.mindalliance.channels.vo.common.ElementVO;
	
	import mx.managers.PopUpManager;

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
            CairngormHelper.fireEvent( new GetElementEvent(id, model.editorModel));
        }
        
        override protected function btnAddClicked():void {
    		var pc:PersonCreator = PopUpManager.createPopUp(this, PersonCreator, true) as PersonCreator ;
			pc.resultHandler = function anon(firstname:String, lastname:String):void {
				CairngormHelper.fireEvent(new CreatePersonEvent(firstname, lastname)) ;
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