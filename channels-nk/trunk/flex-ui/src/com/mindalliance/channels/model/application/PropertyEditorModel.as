package com.mindalliance.channels.model.application
{
	

	
	[Bindable]
	public class PropertyEditorModel
    {
    	public function PropertyEditorModel(modelFactory : Function) {
    		repositoryNodeEditorModel = new RepositoryNodeEditorModel(modelFactory(), modelFactory());
    		taskNodeEditorModel = new TaskNodeEditorModel(modelFactory(), modelFactory());
    		eventNodeEditorModel = new EventNodeEditorModel(modelFactory());
    	}
    	
    	
        public var repositoryNodeEditorModel : RepositoryNodeEditorModel;
        public var taskNodeEditorModel : TaskNodeEditorModel;
        public var eventNodeEditorModel : EventNodeEditorModel;
	}
}