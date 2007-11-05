package com.mindalliance.channels.view.scenario
{
	import com.mindalliance.channels.common.events.DeleteElementEvent;
	import com.mindalliance.channels.common.events.GetElementEvent;
	import com.mindalliance.channels.scenario.events.CreateEventEvent;
	import com.mindalliance.channels.scenario.events.GetEventListEvent;
	import com.mindalliance.channels.util.CairngormHelper;
	import com.mindalliance.channels.view.UtilFuncs;
	import com.mindalliance.channels.view.common.Chooser;
	import com.mindalliance.channels.vo.common.ElementVO;

	public class EventChooser extends Chooser
	{
		public function EventChooser()
		{
			super();
			elementListKey="events";
			elementName="Events";            
            editor = new EventEditor();
		}
		
	    override protected function populateList() : void {
            CairngormHelper.fireEvent( new GetEventListEvent(channelsModel.currentScenario.id) ); 
        }
        
        override protected function populateElement(id : String) : void {
            CairngormHelper.fireEvent( new GetElementEvent(id, model.editorModel));
        }
        
        override protected function btnAddClicked():void {
        	UtilFuncs.getUserTextInput(this, 
        		function anon(eventName:String):void {
        			CairngormHelper.fireEvent(new CreateEventEvent(eventName, channelsModel.currentScenario.id)) ;
        			listElements.scrollToIndex(list.length - 1) ;
        		}, "Enter Name of New Event", true) ;
        }
        
        override protected function btnRemoveClicked():void {
         	for each (var item:Object in listElements.selectedItems) {
        		CairngormHelper.fireEvent(new DeleteElementEvent((item as ElementVO).id)) ;
        	}
        }
	}
}