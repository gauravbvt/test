package com.mindalliance.channels.business.common
{
	
	import mx.collections.ArrayCollection;
	import mx.rpc.IResponder;
	import mx.rpc.events.FaultEvent;
	import mx.rpc.events.ResultEvent;
	
	public class ProxyResponder implements mx.rpc.IResponder
	{
		private var delegate : BaseDelegate;
		private var requestType : String;
		private var typeName : String;
		private var delegateResponder : IResponder;
		private var params : Array;
		
		public function ProxyResponder(requestType : String, delegate : BaseDelegate, params : Array) {
			this.delegate = delegate;
			this.requestType = requestType;
			this.typeName = delegate.typeName;
			this.delegateResponder = delegate.responder;
			this.params = params;
		}
			
		
		public function result(data:Object):void {
			var result:Object = (data as ResultEvent).result;	
            var value : Object = new Object();
            var xml : XML;
            if (params != null) {
            	for (var key : String in params) { 
            	   value[key] = params[key];	
            	}
            	
            }
			if (requestType == 'delete') {
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
					delegate.responder.fault("Delete failed");
					return;
				}
			} if (requestType == 'update') {
			   if (result == true) {
                    value["data"]=true;
                } else {
                    delegate.responder.fault("Update failed");
                    return;
                }
			}else {	
				xml = (result as XML);
				if (xml.localName() == "error") {
					delegate.responder.fault(xml.error.toXMLString());
					return;
				}
				switch(requestType) {
					case 'query' : value["data"]=handleQuery(xml); break;
					case 'read' : value["data"]=handleRead(xml); break;
					case 'create' : value["data"]=handleCreate(xml); break;
				};
			}
			delegateResponder.result(value);
		}
		
		private function handleQuery(result : XML) : Object {
			if (result.localName() == "list") {
                return delegate.fromXMLElementList(result);
			} else  if (result != null) {
                return delegate.fromXML(result);	
			}
			return null;
		}
		
		private function handleRead(result : XML) : Object {
			return delegate.fromXML(result);
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