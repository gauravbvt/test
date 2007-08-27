package com.mindalliance.channels.business
{
	import com.mindalliance.channels.model.ChannelsModelLocator;
	
	import mx.rpc.AsyncToken;
	import mx.rpc.IResponder;
	import mx.rpc.http.HTTPService;
	import flash.utils.Dictionary;
	import mx.collections.ArrayCollection;
	import mx.utils.ObjectProxy;
	import com.mindalliance.channels.vo.ElementVO;
	
	public class BaseDelegate
	{
		public var responder:IResponder;
	
		public var typeName : String;
	
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
				for (var key:String in parameters) {
					url += "&" + key + "=" + parameters[key];	
				}
			}
			send(url, null, "GET", "query");
		}
		
		public function getElement(id : String) : void {
			send("element?id=" + id, null, "GET", "read");	
		}
		
		public function deleteElement(id : String) : void {
			send("element?id=" + id, null, "DELETE", "delete");	
		}
		
		public function createElement(doc : XML) : void{
			send(typeName, doc, "POST", "create");	
		}
		
		public function updateElement(obj : ElementVO) : void {
			send("element?id=" + obj.id, toXML(obj), "PUT", "update");	
		}
		
		public function send(key : String, doc : Object, method : String, requestType : String) : void {
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
			token.addResponder( new ProxyResponder(requestType, this) );	
		}
		
		public function generateElementListXML(listTag : String, elementTag : String, list : ArrayCollection) :XML {
			var xml : XML = <{listTag}></{listTag}>;
			for each (var obj : Object in list) {
				xml.appendChild(<{elementTag}><id>{obj.id}</id></{elementTag}>);	
			}
			return xml;
		}
		/**
		 * Produces a list from XML of the form:
		 * 
		 * <list>
		 *   <project>
		 *     <id>{id}</id>
		 *     <name>{name}</id>
		 *   </project>
		 *   ...
		 * </list>
		 * 
		 */
		public function fromXMLElementList(key : String, list : XML) : ArrayCollection {
			var results : ArrayCollection = new ArrayCollection();
			for each (var el : XML in list.elements(key)) {
				results.addItem(new ElementVO(el.id, el.name));	
			}
			return results;
//			if (results.list == null || results.list[key] == null) {
//				return new ArrayCollection();
//			} else if (results.list[key] is ObjectProxy) {
//				return new ArrayCollection([results.list[key]]);
//			} else { 
//				return results.list[key] as ArrayCollection;
//			}		
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
		
	}
}


