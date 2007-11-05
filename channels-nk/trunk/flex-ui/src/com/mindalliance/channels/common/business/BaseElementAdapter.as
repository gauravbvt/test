package com.mindalliance.channels.common.business
{
	import com.mindalliance.channels.vo.common.ElementVO;

	public class BaseElementAdapter implements IElementAdapter
	{
		public function BaseElementAdapter(key : String, type : Class) {
		  _type = type;
		  _key = key;	
		}
		
		private var _type : Class;
		private var _key : String;
		
		public function fromXML(xml:XML):ElementVO
		{
			return new ElementVO(xml.id, xml.name);
		}
		
		public function toXML(element:ElementVO):XML
		{
            var xml : XML = <{key} schema={"/channels/schema/" + key + ".rng"}></{key}>
            
            xml.appendChild(<id>{element.id}</id>);
            xml.appendChild(<name>{element.name}</name>);
            
            return xml;

		}
		
		public function create(params:Object):XML
		{
			var xml : XML = <{key}></{key}>
			
            xml.appendChild(<name>{params["name"]}</name>);
			return xml;
		}
		
		public function fromXMLListElement(element:XML):ElementVO
		{
			return new ElementVO(element.id,element.name);
		}
		
		
        public function postCreate(element : ElementVO, parameters : Object) : void {
        	
        	
        }
        public function updateElement(element : ElementVO, values : Object) : void {
            
        }		
		public function get type() : Class {
		  return _type;	
		}
		
		public function get key() : String {
		  return _key;	
		}
		
	}
}