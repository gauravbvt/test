package com.mindalliance.channels.common.business
{
	import com.mindalliance.channels.model.ChannelsModelLocator;
	import com.mindalliance.channels.vo.common.ElementVO;
	
	import mx.rpc.AsyncToken;
	import mx.rpc.IResponder;
	import mx.rpc.events.FaultEvent;
	import mx.rpc.events.ResultEvent;
	import mx.rpc.http.HTTPService;

	public class UpdateElementDelegate implements IResponder
	{
		
		private var responder : IResponder;
        private var element : ElementVO;
        
        public function UpdateElementDelegate(responder : IResponder,
                                              element : ElementVO) {
            this.responder = responder;
            this.element = element;
        }
        
        public function update() : void {
            var service : HTTPService = new HTTPService();
            service.url = ChannelsModelLocator.getInstance().urlRoot +
                                "/element?id=" + element.id +
                                "&method=PUT";
            service.method="POST";
            service.contentType="application/xml";  
            service.request = ElementAdapterFactory.getInstance().fromType(element).toXML(element);
            service.resultFormat = "e4x";   
            var token:AsyncToken = service.send();
            token.addResponder( this );    
         	
        }
        
		public function result(data:Object):void
		{
			var result:Object = (data as ResultEvent).result;   
            var value : Object = new Object();
            if (result == true) {
                value["data"]=true;
                value["element"] = element;
            } else {
                responder.fault("Update failed");
                return;
            }
            responder.result(value);
		}
		
        public function fault(info:Object) : void {
            var fault:FaultEvent = info as FaultEvent;
            responder.fault(fault);
        }
		
	}
}