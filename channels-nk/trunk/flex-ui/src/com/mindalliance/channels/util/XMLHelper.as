package com.mindalliance.channels.util
{
	
	import com.mindalliance.channels.vo.common.*;
	
	import mx.collections.ArrayCollection;
	
	public class XMLHelper
	{
		public static function idListToXML(header : String, key : String, list : ArrayCollection) : XML {
            var xml : XML = <{header}></{header}>;
            for each (var el : ElementVO in list) {
                xml.appendChild(<{key}>{el.id}</{key}>);    
            }
            return xml;
        }
		public static function xmlToIdList(elementName:String, list : XMLList) : ArrayCollection {
			var results : ArrayCollection = new ArrayCollection();

			for each (var el:XML in list.child(elementName)) {
				results.addItem(new ElementVO(el.valueOf(), el.attribute("name")));
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
		
		public static function xmlToAddress(xml : XMLList) : AddressVO{
			return new AddressVO(xml.street,xml.city,xml.state);	
		}
		
		public static function xmlToCategorySet(obj : XML) : CategorySetVO {
			return new CategorySetVO(obj.categories.@taxonomy, XMLHelper.xmlToIdList("categoryId", obj.categories), obj.categories.attribute("atMostOne")=="true");
		}
		
		public static function categorySetToXML(obj : CategorySetVO) : XML {
			var xml : XML = <categories atMostOne={obj.atMostOne} taxonomy={obj.taxonomy}></categories>;
			for each (var element:ElementVO in obj.categories) {
				xml.appendChild(<categoryId>{element.id}</categoryId>);
			}	
			return xml;		
		}
		
		public static function xmlToTopic(xml : XML) : TopicVO {
			var eois : ArrayCollection;
			if (xml.eoi.length > 0) {
				eois = new ArrayCollection();
				for each (var el : XML in xml.eoi) {
					
					eois.addItem(new IdentifiedVO(el.name, el.description));
					
				}
			}
			var confidence : String = (xml.confidence.level.length() > 0) ? xml.confidence.level : null;
			return new TopicVO(xml.name, 
								xml.description, 
								confidence, 
								XMLHelper.xmlToIdList("roleId", xml.privacy),
								eois);
			
		}
		public static function topicToXML(obj : TopicVO) : XML {
			
			var xml : XML = <topic>
			                  <name>{obj.name}</name>
			                  <description>{obj.description}</description>
			                </topic>;
			if (obj.confidence != null) {
				xml.appendChild(<confidence>
			                     <level>{obj.confidence}</level>
			                  </confidence>);
			}
			
			if (obj.privacy != null) {
				var priv : XML = <privacy></privacy>;
				for each (var el : ElementVO in obj.privacy) {
					priv.appendChild(<roleId>{el.id}</roleId>);
				}	
				xml.appendChild(priv);
			}     
			if (obj.eoi != null) {
				for each (var id : IdentifiedVO in obj.eoi) {
					xml.appendChild(<eoi>
										<name>{id.name}</name>
										<description>{id.description}</description>
									</eoi>);
				}
			}
			return xml;
				
		}
		
		public static function informationToXML(obj : InformationVO) : XML {
			var xml:XML = <information></information>;
			for each (var topic : TopicVO in obj.topics) {
				xml.appendChild(topicToXML(topic));
			}
			return xml;	
		}
		
		public static function xmlToInformation(xml : XML) : InformationVO {
			var topics : ArrayCollection = new ArrayCollection();
			for each (var el : XML in xml.topic) {
				topics.addItem(xmlToTopic(el));
			}	
			return new InformationVO(topics);
		}
		
		public static function latlongToXML(obj : LatLongVO) : XML {
            return <latlong>
                    <latitude>{obj.latitude}</latitude>
                    <longitude>{obj.longitude}</longitude>
                    </latlong>
		}
		
		public static function xmlToLatlong(xml : XML) : LatLongVO {
			return new LatLongVO(xml.latitude, xml.longitude);
			
		}
		
		public static function spatialToXML(obj : SpatialCoordinatesVO) : XML {
			var xml : XML = <spatialCoordinates></spatialCoordinates>;	
			if (obj.spatial is AddressVO) {
                xml.appendChild(XMLHelper.addressToXML((obj.spatial as AddressVO)));	
			} else if (obj.spatial is LatLongVO) {
                xml.appendChild(XMLHelper.latlongToXML((obj.spatial as LatLongVO)));	
			}
			return xml;
		}
		
		public static function xmlToSpatial(xml : XML) : SpatialCoordinatesVO {
			var spatial : ISpatial;
			if (xml.address.length() !=0) {
                spatial = XMLHelper.xmlToAddress(xml.address);
			} else if (xml.latlong.length() != 0) {
			     spatial = XMLHelper.xmlToLatlong(xml.address);	
			}
			return new SpatialCoordinatesVO(spatial);
		}
		
		public static function durationToXML(obj : DurationVO) : XML {
			return <duration><value>{obj.value}</value><unit>{obj.unit}</unit></duration>;		
		}
		
		public static function xmlToDuration(xml : XMLList) : DurationVO  {
            return new DurationVO(xml.value, xml.unit);	
		}
		
		public static function causeToXML(obj : CauseVO) : XML {
			var xml : XML = <cause></cause>;
			if (obj.type == CauseVO.CAUSE_EVENT) {
			     xml.appendChild(<eventId>{obj.id}</eventId>);	
			} else {
                 xml.appendChild(<taskId>{obj.id}</taskId>);
			}
			if (obj.duration != null && obj.from!=null) {
                var delay : XML = <delay></delay>;
                delay.appendChild(XMLHelper.durationToXML(obj.duration));
                delay.appendChild(<from>{obj.from}</from>);
                xml.appendChild(delay);	
			}
			return xml;
			
		}
		
		public static function xmlToCause(xml : XML) : CauseVO {
			var type : String;
			var id : String;
			var duration : DurationVO = null;
			var from : String = null;
			if (xml.cause.taskId.length() != 0) {
				type=CauseVO.CAUSE_TASK;
				id = xml.cause.taskId;
			} else {
			 	type = CauseVO.CAUSE_EVENT;
			 	id = xml.cause.eventId;
			}
			if (xml.delay.length() != 0) {
				duration = XMLHelper.xmlToDuration(xml.cause.delay);
				from = xml.cause.delay.from;
			}
			return new CauseVO(type,id,duration,from);
		} 
        public static function occurrenceWhereToXML(where : ISpatial) : XML {
            var xml : XML = <where></where>;
            if (where is AddressVO) {
                xml.appendChild(XMLHelper.addressToXML((where as AddressVO)));    
            } else if (where is LatLongVO) {
                xml.appendChild(XMLHelper.latlongToXML((where as LatLongVO)));    
            } else if (where is SpatialElementVO) {
                xml.appendChild(<locationId>{(where as SpatialElementVO).id}</locationId>);
            }	
            return xml;
        }
        
        public static function xmlToOccurenceWhere(xml : XML) :ISpatial {
        	if (xml == null) {
        		return null;
        	} else if (xml.where.addressId.length() != 0) {
                return new SpatialElementVO(xml.where.addressId, null);	
            } else if (xml.where.latlong.length() !=0) {
            	return XMLHelper.xmlToLatlong(xml.where.latlong);
            } else if (xml.where.address.length() !=0) {
                return XMLHelper.xmlToAddress(xml.where.address);	
            }	
            return null;
        }
	}

}