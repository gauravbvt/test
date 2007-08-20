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
		public static function convertServiceList(key : String, results : Object) : ArrayCollection {
			if (results.list == null || results.list[key] == null) {
				return new ArrayCollection();
			} else if (results.list[key] is ObjectProxy) {
				return new ArrayCollection([results]);
			} else { 
				return results.list[key] as ArrayCollection;
			}		
		}

	}
}