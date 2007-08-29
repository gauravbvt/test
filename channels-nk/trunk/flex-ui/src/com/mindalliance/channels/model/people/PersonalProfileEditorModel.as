package com.mindalliance.channels.model.people
{
	import com.mindalliance.channels.model.IChannelsModel;

	[Bindable]
	public class PersonalProfileEditorModel implements IChannelsModel
	{
		public var personEditorModel : PersonEditorModel = new PersonEditorModel();
		public var userEditorModel : UserEditorModel = new UserEditorModel();
		
	}
}