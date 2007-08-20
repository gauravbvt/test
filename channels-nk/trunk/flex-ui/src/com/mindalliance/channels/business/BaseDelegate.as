package com.mindalliance.channels.business
{
	import com.mindalliance.channels.model.ChannelsModelLocator;
	
	import mx.rpc.AsyncToken;
	import mx.rpc.IResponder;
	import mx.rpc.http.HTTPService;
	import flash.utils.Dictionary;
	
	public class BaseDelegate
	{
		protected var responder:IResponder;
	
		public function BaseDelegate(responder:IResponder)
		{
			this.responder = responder;
		}
		
		/**
		 * Performs a query based on 
		 */
		public function performQuery(name : String, parameters : Array) : void
		{
			var url : String = "model?query=" + name;
			if (parameters != null) {
				for (var pair:Object in parameters) {
					url += pair.key + "=" +pair.value;	
				}
			}
			send(url, null, "GET");
		}
		
		public function getElement(id : String) : void {
			send("element?id=" + id, null, "GET");	
		}
		
		public function deleteElement(id : String) : void {
			send("element?id=" + id, null, "DELETE");	
		}
		
		public function createElement(type: String, doc : XML) : void{
			send(type, doc, "POST");	
		}
		
		public function updateElement(id : String, doc : XML) : void {
			send("element?id=" + id, doc, "PUT");	
		}
		
		public function send(key : String, doc : Object, method : String) : void {
			var service : HTTPService = new HTTPService();
			service.url = ChannelsModelLocator.getInstance().urlRoot + '/' + key
			if (method == "PUT" || method == "DELETE") {
				service.url += "&method=" + method;
				service.method = "POST";
			} else {
				service.method = method;	
			}
			if (doc != null) {
				service.contentType = "application/xml";	
				service.request = doc;
			}
			var token:AsyncToken = service.send();
			token.addResponder( responder );	
		}
		
	}
}