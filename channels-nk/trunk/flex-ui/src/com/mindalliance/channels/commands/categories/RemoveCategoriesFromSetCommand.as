// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.commands.categories
{
	import com.adobe.cairngorm.control.CairngormEvent;
	import com.mindalliance.channels.commands.BaseCommand;
	import com.mindalliance.channels.events.categories.RemoveCategoriesFromSetEvent;
	import com.mindalliance.channels.model.categories.CategoryViewerModel;
	import com.mindalliance.channels.util.ElementHelper;
	import com.mindalliance.channels.vo.common.ElementVO;
	
	public class RemoveCategoriesFromSetCommand extends BaseCommand
	{
		override public function execute(event:CairngormEvent):void
		{
			var evt:RemoveCategoriesFromSetEvent = event as RemoveCategoriesFromSetEvent;
			var model : CategoryViewerModel = evt.model;
			var categories : Array = evt.categories;
			for each (var el : ElementVO in categories) {
				
                model.categories.categories.removeItemAt(ElementHelper.findElementIndexById(el.id, model.categories.categories));
			}
		}
	}
}