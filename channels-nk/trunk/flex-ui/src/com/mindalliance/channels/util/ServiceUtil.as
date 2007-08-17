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
		public static function convertServiceResults(results : Object) : ArrayCollection {
			if (results == null) {
				return new ArrayCollection();
			} else if (results is ObjectProxy) {
				return new ArrayCollection([results]);
			} else { 
				return results as ArrayCollection;
			}		
		}

	}
}