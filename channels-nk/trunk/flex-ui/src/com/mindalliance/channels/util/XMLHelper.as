package com.mindalliance.channels.util
{
	
	import mx.collections.ArrayCollection;
	import mx.utils.ObjectProxy;
	import com.mindalliance.channels.vo.common.*;
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
		
		public static function fromIdList(elementName:String, list : XMLList) : ArrayCollection {
			var results : ArrayCollection = new ArrayCollection();

			for each (var el:XML in list.child(elementName)) {
				results.addItem(new ElementVO(el.valueOf(), el.(@name)));
			}
			return results;
		}
		
		/**
		 * Produces a list from XML of the form:
		 * 
		 * <list>
		 *   <project>
		 *     <id>{id}</id>
		 *     <name>{name}</id>
		 *   </project>
		 *   ...
		 * </list>
		 * 
		 */
		public static function fromXMLElementList(key : String, list : XML) : ArrayCollection {
			var results : ArrayCollection = new ArrayCollection();
			for each (var el : XML in list.elements(key)) {
				results.addItem(new ElementVO(el.id, el.name));	
			}
			return results;	
		}
		
		public static function addressToXML(address : AddressVO) : XML {
			return <address>
					<street>{address.street}</street>
					<city>{address.city}</city>
					<state>{address.state}</state>
				</address>;			
		}
		
		public static function xmlToCategorySet(obj : Object) : CategorySetVO {
			return new CategorySetVO(obj.@taxonomy, XMLHelper.fromIdList("categoryId", obj.categoryId), obj.@atMostOne);
		}
		
		public static function categorySetToXML(obj : CategorySetVO) : XML {
			var xml : XML = <categories atMostOne="{obj.atMostOne}" taxonomy="{obj.taxonomy}"></categories>
			
			for each (var element:ElementVO in obj.categories) {
				xml.appendChild(<categoryId>{element.id}</categoryId>);
			}	
			return xml;		
		}
		
	}
}