package com.mindalliance.channels.common.business
{
	import com.mindalliance.channels.vo.common.ElementVO;
	
	public interface IElementAdapter
	{
		function fromXML(xml : XML) : ElementVO;
		function toXML(element : ElementVO) : XML;
		function create(params : Object) : XML;
		function fromXMLListElement(element : XML) : ElementVO;
		function updateElement(element : ElementVO, values : Object): void;
		function postCreate(element : ElementVO, parameters : Object) : void;
		
		
		function get type() : Class;
		function get key() : String;
	}
}