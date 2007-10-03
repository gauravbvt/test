// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.events.categories
{
	import com.adobe.cairngorm.control.CairngormEvent;

	public class GetCategoryListByDisciplineEvent extends CairngormEvent
	{
		public static const GetCategoryListByDiscipline_Event:String = "<GetCategoryListByDisciplineEvent>";
		public var taxonomy : String;
		public var disciplineId : String;
		public function GetCategoryListByDisciplineEvent(taxonomy : String, disciplineId: String) 
		{
			super( GetCategoryListByDiscipline_Event );
			this.taxonomy = taxonomy;
			this.disciplineId = disciplineId;
		}
	}
}