// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.commands.categories
{
	import com.adobe.cairngorm.control.CairngormEvent;
	import com.mindalliance.channels.business.categories.DisciplineDelegate;
	import com.mindalliance.channels.commands.BaseDelegateCommand;
	import com.mindalliance.channels.events.categories.*;
	
	import mx.collections.ArrayCollection;
	
	public class GetDisciplineListCommand extends BaseDelegateCommand
	{
	
		override public function execute(event:CairngormEvent):void
		{
			var evt:GetDisciplineListEvent = event as GetDisciplineListEvent;
			var delegate:DisciplineDelegate = new DisciplineDelegate( this );
			delegate.getDisciplineList(evt.taxonomy);
		}
		
		override public function result(data:Object):void
		{
			channelsModel.getElementListModel("disciplines" + data["taxonomy"]).data = (data["data"] as ArrayCollection);
		}
	}
}