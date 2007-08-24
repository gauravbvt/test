package com.mindalliance.channels.util
{
	import mx.collections.ArrayCollection;
	
	public class ElementHelper
	{
		public static function findElementById(id : String, col : ArrayCollection) : Object {
			for each (var obj : Object in col) {
				var currId : String = obj.id;
				if (obj.id == id) {
					return obj;
				}
			}
			return null;
		}
	}
}