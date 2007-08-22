package com.mindalliance.channels.util
{
	
	import mx.collections.ArrayCollection;
	import mx.utils.ObjectProxy;
	public class XMLHelper
	{
		
		public static  function generateElementListXML(listTag : String, elementTag : String, list : ArrayCollection) :XML {
			var xml : XML = <{listTag}></{listTag}>;
			for each (var obj : Object in list) {
				xml.appendChild(<{elementTag}><id>{obj.id}</id></{elementTag}>);	
			}
			return xml;
		}
		
		public static function fromXMLList(key : String, results : Object) : ArrayCollection {
			if (results.list == null || results.list[key] == null) {
				return new ArrayCollection();
			} else if (results.list[key] is ObjectProxy) {
				return new ArrayCollection([results.list[key]]);
			} else { 
				return results.list[key] as ArrayCollection;
			}		
		}
	}
}