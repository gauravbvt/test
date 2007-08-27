package com.mindalliance.channels.model.application
{
	import com.mindalliance.channels.vo.*;
	import mx.collections.ArrayCollection;
	
	[Bindable]
	public class PropertyEditorModel
	{
		public var shouldUpdateOrganization : Boolean;
		
		public var organization : OrganizationVO = new OrganizationVO("23455", "New Organization", "Blah blah", "NOOO", null, new AddressVO("234 blah street", "Pasadena", "MD"));
		
		public var organizationList : ArrayCollection;
	}
}