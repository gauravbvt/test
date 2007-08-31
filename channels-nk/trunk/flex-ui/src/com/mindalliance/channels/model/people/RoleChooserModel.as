package com.mindalliance.channels.model.people
{
	import com.mindalliance.channels.model.*;
	import mx.collections.ArrayCollection;
	[Bindable]
	public class RoleChooserModel extends  BaseChannelsModel
    {
        public function RoleChooserModel(elements : Object, elementLists : Object) {
            super(elements, elementLists);
        }
		public var list : ArrayCollection;
		public var selected : ArrayCollection;
		public var roleEditorModel : RoleEditorModel = new RoleEditorModel(elements,elementLists);
		
	}
}