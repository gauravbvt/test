// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.commands.categories
{
	import com.adobe.cairngorm.control.CairngormEvent;
	import com.mindalliance.channels.commands.BaseCommand;
	import com.mindalliance.channels.events.categories.AddCategoriesToSetEvent;
	import com.mindalliance.channels.model.categories.CategoryViewerModel;
	import com.mindalliance.channels.vo.common.ElementVO;
	
	public class AddCategoriesToSetCommand extends BaseCommand
	{
		override public function execute(event:CairngormEvent):void
		{
			var evt:AddCategoriesToSetEvent = event as AddCategoriesToSetEvent;
			var categories : Array = evt.categories;
			var model : CategoryViewerModel = evt.model;
			for each (var el : ElementVO in categories) {
			   model.categories.categories.addItem(el);
            }
		}
	}
}