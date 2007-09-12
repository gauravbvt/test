// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.events.application
{
	import com.adobe.cairngorm.control.CairngormEvent;

	public class LoadRepositoryNodeEditorEvent extends CairngormEvent
	{
		public static const LoadRepositoryNodeEditor_Event:String = "<LoadRepositoryNodeEditorEvent>";
		public var repositoryId : String;
		public var organizationId : String;
		public function LoadRepositoryNodeEditorEvent(repositoryId : String, OrganizationId : String) 
		{
			super( LoadRepositoryNodeEditor_Event );
			this.repositoryId = repositoryId;
			this.organizationId = organizationId;
		}
	}
}