package com.mindalliance.channels.common.events
{
	import com.adobe.cairngorm.control.CairngormEvent;
	
	public class GetElementListEvent extends CairngormEvent
	{
        public static const GetElementList_Event : String = "<GetElementListEvent>";
		public var listKey : String;
		public var query : String;
		public var params : Object;
		public function GetElementListEvent(query : String, listKey : String, params : Object, event : String = GetElementList_Event)
		{
			super(event);
			this.query = query;
            this.listKey = listKey;
            this.params = params;	
		}

	}
}