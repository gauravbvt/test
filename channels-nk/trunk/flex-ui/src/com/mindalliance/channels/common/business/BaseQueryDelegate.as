package com.mindalliance.channels.common.business
{
	import com.mindalliance.channels.model.ChannelsModelLocator;
	import com.mindalliance.channels.vo.common.ElementVO;
	
	import mx.collections.ArrayCollection;
	import mx.rpc.AsyncToken;
	import mx.rpc.IResponder;
	import mx.rpc.events.FaultEvent;
	import mx.rpc.events.ResultEvent;
	import mx.rpc.http.HTTPService;

	public class BaseQueryDelegate implements IResponder
	{
        protected var responder : IResponder;
        protected var parameters : Object;
        
        public function BaseQueryDelegate(responder : IResponder,
                                            parameters : Object = null)
        {
            this.responder = responder;
            this.parameters = parameters
        }
        
        public function result(data : Object) : void {
            var result:Object = (data as ResultEvent).result; 
              
            var value : Object = new Object();
            if (parameters != null) {
                for (var key : String in parameters) { 
                   value[key] = parameters[key];    
                }
                
            }
            var xml : XML = (result as XML);
            if (xml.localName() == "error") {
               responder.fault(xml.error.toXMLString());
               return;
            }
            value["data"] = parseResults(xml);
            
            responder.result(value);
        }
        
        public function fault(info:Object) : void {
            var fault:FaultEvent = info as FaultEvent;
            responder.fault(fault);
        }
        
        protected function parseResults(result : XML) : Object {
            if (result.localName() == "list") {
                var results : ArrayCollection = new ArrayCollection();
                for each (var el : XML in result.children()) {
                    var vo : ElementVO = ElementAdapterFactory.getInstance().fromKey(el.localName()).fromXMLListElement(el);
                    if (vo != null) {
                       results.addItem(vo); 
                    } 
                }
                return results;
            } else  if (result != null) {
                return ElementAdapterFactory.getInstance().fromKey(result.localName()).fromXML(result); 
            }
            return null;
        }
		
		protected function invokeService(url : String) : void {
		    var service : HTTPService = new HTTPService ();
            service.url = ChannelsModelLocator.getInstance().urlRoot + url
            service.method="GET";
            service.resultFormat = "e4x";
            var token:AsyncToken = service.send();
            token.addResponder( this ); 
	   }
	}
}