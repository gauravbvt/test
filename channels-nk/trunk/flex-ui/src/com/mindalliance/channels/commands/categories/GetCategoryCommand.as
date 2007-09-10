// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.commands.categories
{
	import com.adobe.cairngorm.control.CairngormEvent;
	import com.mindalliance.channels.business.categories.CategoryDelegate;
	import com.mindalliance.channels.commands.common.GetElementCommand;
	import com.mindalliance.channels.events.categories.*;
	
	public class GetCategoryCommand extends GetElementCommand
	{
	   public function GetCategoryCommand() {
		  super(CategoryDelegate);
	   }
	}
}