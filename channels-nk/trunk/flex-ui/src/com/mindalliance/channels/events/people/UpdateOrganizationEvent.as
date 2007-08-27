// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.events.people
{
	import com.adobe.cairngorm.control.CairngormEvent;
	import com.mindalliance.channels.vo.OrganizationVO;

	public class UpdateOrganizationEvent extends CairngormEvent
	{
		public static const UpdateOrganization_Event:String = "<UpdateOrganizationEvent>";
		public var organization : OrganizationVO;
		
		public function UpdateOrganizationEvent(organization : OrganizationVO) 
		{
			super( UpdateOrganization_Event );
			this.organization = organization;
		}
	}
}