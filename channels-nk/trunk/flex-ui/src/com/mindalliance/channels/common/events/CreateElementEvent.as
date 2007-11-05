package com.mindalliance.channels.common.events
{
	import com.adobe.cairngorm.control.CairngormEvent;

	public class CreateElementEvent extends CairngormEvent
	{
		public static const CreateElement_Event : String="<CreateElementEvent>";
		
		public var elementType : String;
		public var parameters : Object;
		
		public function CreateElementEvent(type : String, parameters : Object) {
		  super(CreateElement_Event);
		  this.elementType = type;
		  this.parameters = parameters;	
		}
		
	}
}