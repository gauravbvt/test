package com.mindalliance.channels.model.people
{
	import com.mindalliance.channels.vo.OrganizationVO;
	import com.mindalliance.channels.vo.common.AddressVO;
	import mx.collections.ArrayCollection;
	import com.mindalliance.channels.vo.common.AddressVO;
	import com.mindalliance.channels.model.IChannelsModel;
	
	[Bindable]
	public class OrganizationEditorModel implements IChannelsModel
	{
		public var shouldUpdateOrganization : Boolean;
		
		public var organization : OrganizationVO = new OrganizationVO("23455", "New Organization", "Blah blah", new ArrayCollection(),"NOOO", null, new AddressVO("234 blah street", "Pasadena", "MD"), null);
	}
}