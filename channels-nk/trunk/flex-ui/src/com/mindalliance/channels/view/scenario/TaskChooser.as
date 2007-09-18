package com.mindalliance.channels.view.scenario
{
	import com.mindalliance.channels.events.scenario.CreateTaskEvent;
	import com.mindalliance.channels.events.scenario.DeleteTaskEvent;
	import com.mindalliance.channels.events.scenario.GetTaskEvent;
	import com.mindalliance.channels.events.scenario.GetTaskListEvent;
	import com.mindalliance.channels.util.CairngormHelper;
	import com.mindalliance.channels.view.UtilFuncs;
	import com.mindalliance.channels.view.common.Chooser;
	import com.mindalliance.channels.vo.common.ElementVO;

	public class TaskChooser extends Chooser
	{
		public function TaskChooser()
		{
			super();
			elementListKey="tasks";
			elementName="Tasks";            
            editor = new TaskEditor();
		}
		
	    override protected function populateList() : void {
            CairngormHelper.fireEvent( new GetTaskListEvent(channelsModel.currentScenario.id) ); 
        }
        
        override protected function populateElement(id : String) : void {
            CairngormHelper.fireEvent( new GetTaskEvent(id, model.editorModel));
        }
        
        override protected function btnAddClicked():void {
        	UtilFuncs.GetUserTextInput(this, 
	        	function anon(taskName:String):void {
	         		CairngormHelper.fireEvent(new CreateTaskEvent(taskName, channelsModel.currentScenario.id)) ;
	         		listElements.scrollToIndex(list.length - 1) ;
	    		},"Enter Name of New Task", true) ;
        }
        
        override protected function btnRemoveClicked():void {
         	for each (var item:Object in listElements.selectedItems) {
        		CairngormHelper.fireEvent(new DeleteTaskEvent((item as ElementVO).id)) ;
        	}
        }        
	}
}