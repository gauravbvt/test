package com.mindalliance.channels.business
{
	
	import mx.rpc.AsyncToken;
	import mx.rpc.IResponder;
	import mx.rpc.http.HTTPService;
	import flash.utils.Dictionary;
	import mx.collections.ArrayCollection;
	import mx.utils.ObjectProxy;
	import mx.utils.ObjectUtil;
	import mx.rpc.IResponder;
	import mx.rpc.events.FaultEvent;
	import mx.rpc.events.ResultEvent;	
	import mx.logging.Log;
	import mx.logging.ILogger;
	
	public class ProxyResponder implements mx.rpc.IResponder
	{
		private var delegate : com.mindalliance.channels.business.BaseDelegate;
		private var requestType : String;
		private var typeName : String;
		private var delegateResponder : IResponder;
		
		public function ProxyResponder(requestType : String, delegate : com.mindalliance.channels.business.BaseDelegate) {
			this.delegate = delegate;
			this.requestType = requestType;
			this.typeName = delegate.typeName;
			this.delegateResponder = delegate.responder;
		}
			
		
		public function result(data:Object):void {
			var result:Object = (data as ResultEvent).result;	
			if ((!ObjectUtil.isSimple(result)) && result["error"] != null) {
				delegate.responder.fault(result.error);
			} else {
				var value : Object;
				switch(requestType) {
					case 'query' : value=handleQuery(result); break;
					case 'read' : value=handleRead(result); break;
					case 'update' : value=handleUpdate(result); break;
					case 'delete' : value=handleDelete(result); break;
					case 'create' : value=handleCreate(result); break;
				};
				
				delegateResponder.result(value);
			}
		}
		
		private function handleQuery(result : Object) : Object {
			return delegate.fromXMLList(typeName, result);
		}
		
		private function handleRead(result : Object) : Object {
			return delegate.fromXML(result[typeName]);
		}		
		private function handleUpdate(result : Object) : Object {
			return result;
		}		
		private function handleDelete(result : Object) : Object {
			return result;
		}		
		private function handleCreate(result : Object) : Object {
			return delegate.fromXML(result[typeName]);
		}
		public function fault(info:Object) : void {
			var fault:FaultEvent = info as FaultEvent;
			delegateResponder.fault(fault.toString());
		}
	}
}