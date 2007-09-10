package com.mindalliance.channels.model.people
{
	import com.mindalliance.channels.model.*;

	[Bindable]
	public class PersonalProfileEditorModel
    {
    	public function PersonalProfileEditorModel(personEditorModel : EditorModel, userEditorModel : EditorModel) {
    		this.personEditorModel = personEditorModel;
    		this.userEditorModel = userEditorModel;
    	}
    	
		public var personEditorModel : EditorModel;
		public var userEditorModel : EditorModel;	
	}
}