package com.mindalliance.channels.util
{
	
	import mx.collections.ArrayCollection;
	import mx.utils.ObjectProxy;
	import com.mindalliance.channels.vo.AddressVO;
	import com.mindalliance.channels.vo.ElementVO;
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
		
		public static function fromIdList(elementName:String, list : XMLList) : ArrayCollection {
			var results : ArrayCollection = new ArrayCollection();
//			if (list[listName] != null && list[listName][elementName] != null) {
//				var ids : Object = list[listName][elementName];
//				if (ids is ObjectProxy) {
//					
//				} else if (ids is ArrayCollection) {		
//					for each (var obj :Object in ids) {
//						//results.addItem(new ElementVO(obj, 	
//					}
//				}
//				results.addItem(new ElementVO(obj[elementName], obj.@name));
//			}

			for each (var el:XML in list.child(elementName)) {
				results.addItem(new ElementVO(el.valueOf(), el.@name));
			}
			return results;
		}
		
		public static function fromAddress(address : AddressVO) : XML {
			return <address>
					<street>{address.street}</street>
					<city>{address.city}</city>
					<state>{address.state}</state>
				</address>;			
		}
	}
}