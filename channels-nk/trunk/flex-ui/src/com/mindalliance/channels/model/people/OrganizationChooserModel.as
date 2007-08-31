package com.mindalliance.channels.model.people
{
	import com.mindalliance.channels.model.*;
	import mx.collections.ArrayCollection;
	[Bindable]
	public class OrganizationChooserModel extends  BaseChannelsModel
    {
        public function OrganizationChooserModel(elements : Object, choosers : Object) {
            super(elements, choosers);
        }
		public var organizationList : ArrayCollection;
	}
}