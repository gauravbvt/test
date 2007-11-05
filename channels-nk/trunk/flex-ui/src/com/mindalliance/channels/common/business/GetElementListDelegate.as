package com.mindalliance.channels.common.business
{
	import com.mindalliance.channels.model.ChannelsModelLocator;
	
	import mx.rpc.IResponder;
	import mx.rpc.events.FaultEvent;
	import mx.rpc.http.HTTPService;
	
	public class GetElementListDelegate extends BaseQueryDelegate
	{
		private var queryName : String;
		
		public function GetElementListDelegate(responder : IResponder, 
		                                      queryName : String, 
		                                      listKey : String, 
		                                      parameters : Object) 
		{
          this.queryName = queryName;
          parameters["listKey"] = listKey;
		  super(responder, parameters);	
		}
		
		public function performQuery() : void
        {
            var url : String = "/model?query=" + queryName;
            if (parameters != null) {
                for (var key:String in parameters) {
                	if (key != "listKey")
                        url += "&" + key + "=" + parameters[key];   
                }
            }
            invokeService(url);        }
	}
}