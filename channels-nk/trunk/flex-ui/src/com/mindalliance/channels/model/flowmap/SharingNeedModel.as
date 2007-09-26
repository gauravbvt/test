package com.mindalliance.channels.model.flowmap
{
	import com.adobe.cairngorm.CairngormError;
	import com.adobe.cairngorm.CairngormMessageCodes;
	import com.mindalliance.channels.util.ElementHelper;
	import com.mindalliance.channels.view.flowmap.FlowMap;
	import com.mindalliance.channels.vo.common.ElementVO;
	
	import flash.events.Event;
	
	import mx.collections.ArrayCollection;
	import mx.events.CollectionEvent;
	import mx.events.CollectionEventKind;
	
	public class SharingNeedModel extends BaseModel
	{
		private function init():void {
			ElementHelper.installCollectionChangeListener("sharingneeds", sharingNeedChangeHandler) ;
		}
		
		private function sharingNeedsAdded(colEvent:CollectionEvent):void {
			for each (var item:Object in colEvent.items) {
				extractElementVO(item,
					function anon(elemVO:ElementVO):void {
						
					}) ;
			}
		}
		
		private function sharingNeedsRemoved(colEvent:CollectionEvent):void {
			for each (var item:Object in colEvent.items) {
				extractElementVO(item,
					function anon(elemVO:ElementVO):void {
						FlowMap.removeSharingNeed(elemVO.id) ;
					}) ;
			}
		}
		
		private function sharingNeedsReset(colEvent:CollectionEvent):void {
			var sharingNeedsAC:ArrayCollection = model.getElementListModel("sharingneeds").data as ArrayCollection ;
			for each (var sn:ElementVO in sharingNeedsAC) {
				// blah blah
			}
		}
		
		private function sharingNeedsUpdated(colEvent:CollectionEvent):void {
			for each (var item:Object in colEvent.items) {
				extractElementVO(item,
					function anon(elemVO:ElementVO):void {
						// get sharing need VO
						// extract elements out of it
/*  						FlowMap.addSharingNeed(elemVO.id, ...) ; */
					}) ;
			}
		}
		
		
		protected function sharingNeedChangeHandler(event:Event):void {
			if (!(event is CollectionEvent))
				return ;
				
			var colEvent:CollectionEvent = event as CollectionEvent ;
			switch (colEvent.kind) {
				case CollectionEventKind.RESET:
					sharingNeedsReset(colEvent) ;
				break ;
				case CollectionEventKind.ADD:
					sharingNeedsAdded(colEvent) ;
				break ;
				case CollectionEventKind.REMOVE:
					sharingNeedsRemoved(colEvent) ;
				break ;
				case CollectionEventKind.UPDATE:
					sharingNeedsUpdated(colEvent) ;
				break ;
			}
		}
		
		private static var instance:SharingNeedModel;

		public function SharingNeedModel(access:Private) {
			super() ;
			if (access != null)
				if (instance == null)
					instance = this;
			else
				throw new CairngormError( CairngormMessageCodes.SINGLETON_EXCEPTION, "SharingNeedModel" );
			init() ;
		}
		 
		/**
		 * Returns the Singleton instance of ChannelsModelLocator
		 */
		public static function getInstance() : SharingNeedModel {
			if (instance == null)
				instance = new SharingNeedModel( new Private );
			return instance;
		}
	}
}

/**
 * @private
 * Inner class which restricts contructor access to Private
 */
class Private {}