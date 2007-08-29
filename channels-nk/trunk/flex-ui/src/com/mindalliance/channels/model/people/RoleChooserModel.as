package com.mindalliance.channels.model.people
{
	import com.mindalliance.channels.model.IChannelsModel;
	import mx.collections.ArrayCollection;
	[Bindable]
	public class RoleChooserModel implements IChannelsModel
	{
		public var list : ArrayCollection;
		public var selected : ArrayCollection;
		public var roleEditorModel : RoleEditorModel = new RoleEditorModel();
		
	}
}