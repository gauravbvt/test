
package com.mindalliance.channels.vo
{
	import com.adobe.cairngorm.vo.IValueObject;

	[Bindable]
	public class ElementVO implements IValueObject
	{
		public function ElementVO( id : String = "0",
		                           name : String = "" ) {
		    this.id = id;
			this.name = name;
		}
		public var id:String;
		public var name:String;
	}
}