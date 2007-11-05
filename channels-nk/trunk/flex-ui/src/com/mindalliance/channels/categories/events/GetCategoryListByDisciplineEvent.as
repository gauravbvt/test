// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.categories.events
{
	import com.mindalliance.channels.common.events.GetElementListEvent;

	public class GetCategoryListByDisciplineEvent extends GetElementListEvent
	{
		public function GetCategoryListByDisciplineEvent(taxonomy : String, disciplineId: String) 
		{
			super( "categoriesInTaxonomyAndDiscipline", "categories" + taxonomy + disciplineId, 
			         {"taxonomy" : taxonomy, "disciplineId" : disciplineId} );
		}
	}
}