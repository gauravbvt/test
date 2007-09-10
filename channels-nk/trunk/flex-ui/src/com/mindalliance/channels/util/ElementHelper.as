package com.mindalliance.channels.util
{
	import com.mindalliance.channels.vo.common.ElementVO;
	
	import mx.collections.ArrayCollection;
	
	public class ElementHelper
	{
		public static function findElementById(id : String, col : ArrayCollection) : ElementVO {
			for each (var obj : ElementVO in col) {
				var currId : String = obj.id;
				if (obj.id == id) {
					return obj;
				}
			}
			return null;
		}
		
		public static function findElementIndexById(id : String, col : ArrayCollection) : int {
			for (var inx : int = 0 ; inx < col.length ; inx++) {
                if (col[inx].id == id) {
                    return inx;
                }
            }
            return -1;
		}
	}
}