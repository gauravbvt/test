package com.mindalliance.channels.scenario.view
{
	import com.mindalliance.channels.common.events.DeleteElementEvent;
	import com.mindalliance.channels.scenario.events.CreateTaskEvent;
	import com.mindalliance.channels.scenario.events.GetTaskEvent;
	import com.mindalliance.channels.scenario.events.GetTaskListEvent;
	import com.mindalliance.channels.util.CairngormHelper;
	import com.mindalliance.channels.util.ViewUtils;
	import com.mindalliance.channels.common.view.Chooser;
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
        	ViewUtils.getUserTextInput(this, 
	        	function anon(taskName:String):void {
	         		CairngormHelper.fireEvent(new CreateTaskEvent(taskName, channelsModel.currentScenario.id)) ;
	         		//listElements.scrollToIndex(list.length - 1) ;
	    		},"Enter Name of New Task", true) ;
        }
        
        override protected function btnRemoveClicked():void {
         	for each (var item:Object in listElements.selectedItems) {
        		CairngormHelper.fireEvent(new DeleteElementEvent((item as ElementVO).id)) ;
        	}
        }        
	}
}