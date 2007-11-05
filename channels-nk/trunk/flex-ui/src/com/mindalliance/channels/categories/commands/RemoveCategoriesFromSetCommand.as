// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.categories.commands
{
	import com.adobe.cairngorm.control.CairngormEvent;
	import com.mindalliance.channels.common.commands.BaseCommand;
	import com.mindalliance.channels.categories.events.RemoveCategoriesFromSetEvent;
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