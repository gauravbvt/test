package com.mindalliance.channels.model.application
{
	import com.mindalliance.channels.model.EditorModel;
	
	[Bindable]
	public class TaskNodeEditorModel
	{
		public function TaskNodeEditorModel(taskModel : EditorModel, roleModel : EditorModel)
		{
		  this.taskModel = taskModel;	
		  this.roleModel = roleModel;
		}
		public var taskModel : EditorModel;
		public var roleModel : EditorModel;
	}
}