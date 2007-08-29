package com.mindalliance.channels.model.people
{
	import com.mindalliance.channels.model.IChannelsModel;
	import com.mindalliance.channels.vo.OrganizationVO;
	import com.mindalliance.channels.vo.common.AddressVO;

	
	[Bindable]
	public class OrganizationEditorModel implements IChannelsModel
	{
		public var shouldUpdateOrganization : Boolean;
		
		public var organization : OrganizationVO;
	}
}