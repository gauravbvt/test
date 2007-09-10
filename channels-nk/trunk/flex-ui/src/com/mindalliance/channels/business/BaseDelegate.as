package com.mindalliance.channels.business
{
	import com.mindalliance.channels.model.ChannelsModelLocator;
	import com.mindalliance.channels.vo.common.ElementVO;
	
	import mx.collections.ArrayCollection;
	import mx.rpc.AsyncToken;
	import mx.rpc.IResponder;
	import mx.rpc.http.HTTPService;
	
	public class BaseDelegate
	{
		public var responder:IResponder;
	
		public var typeName : String;
	
		public function BaseDelegate(responder:IResponder)
		{
			this.responder = responder;
		}
		
		public function performQuery(name : String, parameters : Array) : void
		{
			var url : String = "model?query=" + name;
			if (parameters != null) {
				for (var key:String in parameters) {
					url += "&" + key + "=" + parameters[key];	
				}
			}
			send(url, null, "GET", "query", parameters);
		}
		

		
		public function getElement(id : String) : void {
			var parameters:Array = new Array();
            parameters["id"] = id;
			send("element?id=" + id + "&nameReferenced=true", null, "GET", "read", parameters);	
		}
		
		public function deleteElement(id : String) : void {           
		    var parameters:Array = new Array();
            parameters["id"] = id;
			send("element?id=" + id, null, "DELETE", "delete", parameters);	
		}
		
		public function createElement(doc : XML) : void{
			send(typeName, doc, "POST", "create");	
		}
		
		public function updateElement(obj : ElementVO) : void {           
            var parameters:Array = new Array();
            parameters["id"] = obj.id;
			send("element?id=" + obj.id, toXML(obj), "PUT", "update", parameters);	
		}
		
		public function send(key : String, doc : Object, method : String, requestType : String, parameters : Array = null) : void {
			var service : HTTPService = new HTTPService();
			service.url = ChannelsModelLocator.getInstance().urlRoot + '/' + key
			switch(requestType) {
				case 'query':
				case 'read':
						service.method="GET";
						service.resultFormat = "e4x";
						break;
				case 'update':
						service.url +="&method=PUT";
				case 'create':
						service.method="POST";
						service.contentType="application/xml";	
						service.request = doc;
						service.resultFormat = "e4x";
						break;
				case 'delete':
						service.method="POST";
						service.url +="&method=DELETE";
						service.contentType="application/xml";	
						service.request = "<delete/>";
						break;
			};
			var token:AsyncToken = service.send();
			token.addResponder( new ProxyResponder(requestType, this, parameters) );	
		}
		
		/**
		 * Extending delegates should override this method to parse XML
		 * into the appropriate Value Object.
		 */
		public function fromXML(results : XML) : ElementVO {
			return null;
		}
		/**
		 * Extending delegates should override this method to generate XML
		 * from the passed in Value Object.
		 */
		public function toXML(obj : ElementVO) : XML {
			return null;
		}
		public function fromXMLElementList(list : XML) : ArrayCollection {
            var results : ArrayCollection = new ArrayCollection();
            for each (var el : XML in list.elements(typeName)) {
                results.addItem(new ElementVO(el.id, el.name)); 
            }
            return results; 
        }
        

	}
}


