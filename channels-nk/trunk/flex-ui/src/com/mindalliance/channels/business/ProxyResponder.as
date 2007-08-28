package com.mindalliance.channels.business
{
	
	import com.mindalliance.channels.util.XMLHelper;
	
	import mx.rpc.IResponder;
	import mx.rpc.events.FaultEvent;
	import mx.rpc.events.ResultEvent;
	
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
			if (requestType == 'delete') {
				if (result == true) {
					value=handleDelete((result as Boolean));
				} else {
					delegate.responder.fault("Delete failed");
				}
			} else {
				var xml : XML = (result as XML);
				if (xml.error.length()!=0) {
					delegate.responder.fault(xml.error.toXMLString());
					return;
				}
				var value : Object;
				switch(requestType) {
					case 'query' : value=handleQuery(xml); break;
					case 'read' : value=handleRead(xml); break;
					case 'update' : value=handleUpdate(xml); break;
					case 'create' : value=handleCreate(xml); break;
				};
			}
			delegateResponder.result(value);
		}
		
		private function handleQuery(result : XML) : Object {
			return XMLHelper.fromXMLElementList(typeName, result);
		}
		
		private function handleRead(result : XML) : Object {
			return delegate.fromXML(result);
		}		
		private function handleUpdate(result : XML) : Object {
			return result;
		}		
		private function handleDelete(result : Boolean) : Object {
			return result;
		}		
		private function handleCreate(result : XML) : Object {
			return delegate.fromXML(result);
		}
		public function fault(info:Object) : void {
			var fault:FaultEvent = info as FaultEvent;
			delegateResponder.fault(fault.toString());
		}
	}
}