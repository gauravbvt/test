// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.categories.events
{
	import com.mindalliance.channels.common.events.GetElementListEvent;

	public class GetDisciplineListEvent extends GetElementListEvent
	{
		public function GetDisciplineListEvent(taxonomy : String) 
		{
			super( "disciplinesInTaxonomy", "disciplines" + taxonomy, {"taxonomy" : taxonomy} );
		}
	}
}