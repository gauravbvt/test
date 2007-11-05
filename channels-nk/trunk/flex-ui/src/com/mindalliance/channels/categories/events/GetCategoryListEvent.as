// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.categories.events
{
	import com.mindalliance.channels.common.events.GetElementListEvent;

	public class GetCategoryListEvent extends GetElementListEvent
	{
		public function GetCategoryListEvent(taxonomy : String) 
		{
			super( "categoriesInTaxonomy", "categories" + taxonomy, {"taxonomy" : taxonomy});
		}
	}
}