package com.mindalliance.channels.common.business
{
	import com.mindalliance.channels.model.ChannelsModelLocator;
	
	import mx.rpc.AsyncToken;
	import mx.rpc.IResponder;
	import mx.rpc.events.ResultEvent;
	import mx.rpc.http.HTTPService;

	public class CreateElementDelegate implements IResponder
	{
		private var responder : IResponder;
		private var type : String;
		private var params : Object;
		
		public function CreateElementDelegate(responder : IResponder,
		                                      type : String,
		                                      params : Object) {
			this.responder = responder;
			this.type = type;
			this.params = params;
		}
		
		public function createElement() : void {
            var adapter : IElementAdapter = ElementAdapterFactory.getInstance().fromKey(type);
            var xml : XML  = adapter.create(params);	
            var service : HTTPService = new HTTPService();
            
            service.url = ChannelsModelLocator.getInstance().urlRoot + '/' + type;
            service.method="POST";
            service.contentType="application/xml";  
            service.request = xml;
            service.resultFormat = "e4x";
            var token:AsyncToken = service.send();
            token.addResponder( this );    
		}
		
		public function result(data:Object):void
		{
			var result:Object = (data as ResultEvent).result;   
            var value : Object = new Object();
            var xml : XML= (result as XML);
            if (xml.localName() == "error") {
                responder.fault(xml.error.toXMLString());
                return;
            }
            var adapter : IElementAdapter = ElementAdapterFactory.getInstance().fromKey(xml.localName());
            value["data"] = adapter.fromXML(xml);
            value["parameters"] = params;
            responder.result(value);

            
		}
		
		public function fault(info:Object):void
		{
			responder.fault(info);
		}
		
	}
}