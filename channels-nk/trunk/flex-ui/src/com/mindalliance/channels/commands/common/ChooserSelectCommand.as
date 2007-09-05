// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.commands.common
{
	import com.adobe.cairngorm.control.CairngormEvent;
	import com.mindalliance.channels.commands.BaseCommand;
	import com.mindalliance.channels.events.common.ChooserSelectEvent;
	import com.mindalliance.channels.vo.common.ElementVO;
	public class ChooserSelectCommand extends BaseCommand
	{
		override public function execute(event:CairngormEvent):void
		{
			var evt:ChooserSelectEvent = event as ChooserSelectEvent;
			if (evt.selection != null && evt.selection.length > 0) {
               for each (var el : ElementVO in evt.selection) {
                   evt.model.selection.addItem(el);  
                }
            }
		}
	}
}