package com.mindalliance.channels.view.scenario
{
	import com.mindalliance.channels.events.scenario.GetTaskEvent;
	import com.mindalliance.channels.events.scenario.GetTaskListEvent;
	import com.mindalliance.channels.util.CairngormHelper;
	import com.mindalliance.channels.view.common.Chooser;

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
	}
}