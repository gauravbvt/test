package com.mindalliance.channels.model.people
{
	import com.mindalliance.channels.model.*;

	[Bindable]
	public class PersonalProfileEditorModel extends  BaseChannelsModel
    {
        public function PersonalProfileEditorModel(elements : Object, elementLists : Object) {
            super(elements, elementLists);
        }
		public var personEditorModel : EditorModel = new EditorModel(elements, elementLists);
		public var userEditorModel : EditorModel = new EditorModel(elements, elementLists);
		
	}
}