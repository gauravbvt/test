package com.mindalliance.channels.util
{
	import com.mindalliance.channels.model.ChannelsModelLocator;
	
	import mx.collections.ArrayCollection;
	import mx.utils.ObjectProxy;
	import mx.rpc.http.HTTPService;
	import mx.rpc.IResponder;
	import mx.rpc.AsyncToken;
	
	public class ServiceUtil
	{
		public static function convertServiceResults(results : Object) : ArrayCollection {
			if (results == null) {
				return new ArrayCollection();
			} else if (results is ObjectProxy) {
				return new ArrayCollection([results]);
			} else { 
				return results as ArrayCollection;
			}		
		}
//		public static function getReadService(key : String, request : Object, responder : IResponder) : HTTPService {
//			return getService(key, request, "GET");
//		}
//		
//		public static function getCreateService(type : String, request : Object) : HTTPService {
//			
//			return getService(type, request, "POST");
//		}
//		
//		public static function getDeleteService(key : String, request : Object) : HTTPService {
//			return getService(key, request, "DELETE");
//		}
//		
//		public static function getUpdateService(key : String, request : Object) : HTTPService {
//			return getService(key, request, "PUT");
//		}
		
		public static function send(key : String, request : Object, method : String, responder : IResponder) : void {
			var service : HTTPService = new HTTPService();
			service.url = ChannelsModelLocator.getInstance().urlRoot + '/' + key
			if (method == "PUT" || method == "DELETE") {
				if (request == null) {
					request = new Object();	
				}
				request["_method"] = method;
				service.method = "POST";
			} else {
				service.method = method;	
			}
			service.request = request;
			var token:AsyncToken = service.send();
			token.addResponder( responder );	
		}
	}
}