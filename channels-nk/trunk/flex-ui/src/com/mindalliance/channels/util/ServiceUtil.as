package com.mindalliance.channels.util
{
	import mx.collections.ArrayCollection;
	import mx.utils.ObjectProxy;
	
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