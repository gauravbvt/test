// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.commands.categories
{
	import com.adobe.cairngorm.control.CairngormEvent;
	import com.adobe.cairngorm.control.CairngormEventDispatcher;
	import com.mindalliance.channels.business.categories.CategoryDelegate;
	import com.mindalliance.channels.events.categories.*;
	import com.mindalliance.channels.commands.BaseDelegateCommand;
	
	public class GetCategoryListCommand extends BaseDelegateCommand
	{
	
		override public function execute(event:CairngormEvent):void
		{
			var evt:GetCategoryListEvent = event as GetCategoryListEvent;
			var delegate:CategoryDelegate = new CategoryDelegate( this );
		}
		
		override public function result(data:Object):void
		{
			
		}
	}
}