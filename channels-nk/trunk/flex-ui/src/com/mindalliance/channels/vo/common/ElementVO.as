
package com.mindalliance.channels.vo.common
{
	import com.adobe.cairngorm.vo.IValueObject;
	
	[Bindable]
	public class ElementVO extends IdentifiedVO implements IValueObject
	{
		public function ElementVO( id : String,
		                           name : String,
		                           description : String = null) {
		    super(name, description);
		    this._id = id;
		}
		private var _id:String;
		
		public function get id () : String {
			return _id;	
		}
		
		public function set id (id:String) : void {
			this._id = id;
		}
	}
}