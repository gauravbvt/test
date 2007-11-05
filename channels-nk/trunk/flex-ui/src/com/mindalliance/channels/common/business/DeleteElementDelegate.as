package com.mindalliance.channels.common.business
{
	import com.mindalliance.channels.model.ChannelsModelLocator;
	
	import mx.collections.ArrayCollection;
	import mx.rpc.AsyncToken;
	import mx.rpc.IResponder;
	import mx.rpc.events.FaultEvent;
	import mx.rpc.events.ResultEvent;
	import mx.rpc.http.HTTPService;
	
	public class DeleteElementDelegate implements IResponder
	{
        private var responder : IResponder;
		public function DeleteElementDelegate(responder : IResponder) {
			this.responder = responder;
		}
		
		public function deleteElement(id : String) : void {
		    var service : HTTPService = new HTTPService();
            service.url = ChannelsModelLocator.getInstance().urlRoot 
                        + '/element?id=' + id;
            service.method="POST";
            service.url +="&method=DELETE";
            service.contentType="application/xml";
            service.resultFormat="e4x"  
            service.request = "<delete/>";	
            var token:AsyncToken = service.send();
            token.addResponder( this );    
        
		}
		
		public function result(data:Object) : void {
			var result:Object = (data as ResultEvent).result;   
            var value : Object = new Object();
            var xml : XML;
            xml  = (result as XML);
            if (xml.deleted.length() > 0) {
                value["data"] = true;
                var deleted : ArrayCollection = new ArrayCollection();
                var el : XML;
                for each (el in xml.deleted.id) { 
                    deleted.addItem(el.text());
                }
                value["deleted"] = deleted;
                var updated : ArrayCollection = new ArrayCollection();
                for each (el in xml.updated.id) { 
                   updated.addItem(el.text());  
                }
                value["updated"] = updated;
            } else {
                responder.fault("Delete failed");
                return;
            }
            responder.result(value);
		}
		
		public function fault(info:Object) : void {
            var fault:FaultEvent = info as FaultEvent;
            responder.fault(fault.toString());
        }
		
	}
}