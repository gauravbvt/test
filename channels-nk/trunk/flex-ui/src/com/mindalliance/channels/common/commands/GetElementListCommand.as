package com.mindalliance.channels.common.commands
{
	import com.adobe.cairngorm.control.CairngormEvent;
	import com.mindalliance.channels.common.business.GetElementListDelegate;
	import com.mindalliance.channels.common.events.GetElementListEvent;
	
	import mx.collections.ArrayCollection;
	import mx.rpc.events.FaultEvent;

	public class GetElementListCommand extends BaseDelegateCommand
	{
       override public function execute(event:CairngormEvent):void
        {
            var evt:GetElementListEvent = event as GetElementListEvent;
            channelsModel.getElementListModel(evt.listKey).data  = null;
            var delegate:GetElementListDelegate = new GetElementListDelegate( this,
                                                                              evt.query,
                                                                              evt.listKey,
                                                                              evt.params );
            log.debug("Retrieving " + evt.listKey + " list");
            delegate.performQuery();
        }
        
        override public function result(data:Object):void
        {
            channelsModel.getElementListModel(data["listKey"]).data = (data["data"] as ArrayCollection);
            log.debug("Successfully retrieved "  + data["listKey"] + " list");
        }
       
        override public function fault(info:Object):void
        {
            super.fault((info as FaultEvent).toString());
        }
	}
}