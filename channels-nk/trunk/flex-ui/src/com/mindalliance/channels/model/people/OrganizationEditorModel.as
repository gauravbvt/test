package com.mindalliance.channels.model.people
{
	import com.mindalliance.channels.vo.OrganizationVO;
	import mx.collections.ArrayCollection;
	
	[Bindable]
	public class OrganizationEditorModel
	{
		public var shouldUpdateOrganization : Boolean;
		
		public var organization : OrganizationVO;
		
		public var organizationList : ArrayCollection;
	}
}