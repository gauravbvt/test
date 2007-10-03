// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.commands.categories
{
	import com.adobe.cairngorm.control.CairngormEvent;
	import com.mindalliance.channels.business.categories.CategoryDelegate;
	import com.mindalliance.channels.commands.BaseDelegateCommand;
	import com.mindalliance.channels.events.categories.*;
	
	import mx.collections.ArrayCollection;
	
	public class GetCategoryListCommand extends BaseDelegateCommand
	{
	
		override public function execute(event:CairngormEvent):void
		{
			var evt:GetCategoryListEvent = event as GetCategoryListEvent;
			var delegate:CategoryDelegate = new CategoryDelegate( this );
			delegate.getCategoryList(evt.taxonomy);
		}
		
		override public function result(data:Object):void
		{
			channelsModel.getElementListModel("categories" + data["taxonomy"]).data = (data["data"] as ArrayCollection);
		}
		

        
	}
}