package com.mindalliance.channels.util
{
	import com.mindalliance.channels.vo.common.ElementVO;
	
	import mx.collections.ArrayCollection;
	import com.mindalliance.channels.model.ChannelsModelLocator;
	import com.mindalliance.channels.model.ElementListModel;
	import mx.events.CollectionEvent;
	
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
		
		public static function installCollectionChangeListener(modelKey:String, handler:Function):void {
			var elementListModel:ElementListModel = ChannelsModelLocator.getInstance().getElementListModel(modelKey) ;
			elementListModel.data.addEventListener(CollectionEvent.COLLECTION_CHANGE, handler);
		}
		
		public static function uninstallCollectionChangeListener(modelKey:String, handler:Function):void {
			var elementListModel:ElementListModel = ChannelsModelLocator.getInstance().getElementListModel(modelKey) ;
			elementListModel.data.removeEventListener(CollectionEvent.COLLECTION_CHANGE, handler);
		}
	}
}