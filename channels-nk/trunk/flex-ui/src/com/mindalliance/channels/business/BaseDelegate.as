package com.mindalliance.channels.business
{
	import com.mindalliance.channels.model.ChannelsModelLocator;
	
	import mx.rpc.AsyncToken;
	import mx.rpc.IResponder;
	import mx.rpc.http.HTTPService;

	public class BaseDelegate
	{
		protected var responder:IResponder;
	
		public function BaseDelegate(responder:IResponder)
		{
			this.responder = responder;
		}
		
		public function send(key : String, request : Object, method : String, responder : IResponder) : void {
			var service : HTTPService = new HTTPService();
			service.url = ChannelsModelLocator.getInstance().urlRoot + '/' + key
			if (method == "PUT" || method == "DELETE") {
				if (request == null) {
					request = new Object();	
				}
				request["method"] = method;
				service.method = "POST";
			} else {
				service.method = method;	
			}
			service.request = request;
			var token:AsyncToken = service.send();
			token.addResponder( responder );	
		}
		
		public function getElement(id : String) : void {
			send(id, null, "GET", responder);	
		}
	}
}