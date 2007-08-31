package com.mindalliance.channels.model.people
{
	import com.mindalliance.channels.model.*;

	[Bindable]
	public class PersonalProfileEditorModel extends  BaseChannelsModel
    {
        public function PersonalProfileEditorModel(elements : Object, elementLists : Object) {
            super(elements, elementLists);
        }
		public var personEditorModel : PersonEditorModel = new PersonEditorModel(elements, elementLists);
		public var userEditorModel : UserEditorModel = new UserEditorModel(elements,elementLists);
		
	}
}