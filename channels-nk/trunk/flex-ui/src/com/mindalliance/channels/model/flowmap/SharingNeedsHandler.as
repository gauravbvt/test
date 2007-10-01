package com.mindalliance.channels.model.flowmap
{
	import com.adobe.cairngorm.CairngormError;
	import com.adobe.cairngorm.CairngormMessageCodes;
	import com.mindalliance.channels.util.ElementHelper;
	import com.mindalliance.channels.view.flowmap.FlowMap;
	import com.mindalliance.channels.vo.SharingNeedVO;
	import com.mindalliance.channels.vo.common.ElementVO;
	
	import flash.events.Event;
	
	import mx.collections.ArrayCollection;
	import mx.events.CollectionEvent;
	import mx.events.CollectionEventKind;
	
	public class SharingNeedsHandler extends BaseCollectionChangeHandler
	{
		
		protected override function itemsAdded(colEvent:CollectionEvent):void
			for each (var item:Object in colEvent.items) {
				extractElementVO(item,
					function anon(elemVO:ElementVO):void {
						var sharingVO:SharingNeedVO = elemVO as SharingNeedVO ;
					}) ;
			}
		}
		
		protected override function itemsRemoved(colEvent:CollectionEvent):void {
			for each (var item:Object in colEvent.items) {
				extractElementVO(item,
					function anon(elemVO:ElementVO):void {
						var sharingVO:SharingNeedVO = elemVO as SharingNeedVO ;
						
					}) ;
			}
		}
		
		protected override function collectionReset(colEvent:CollectionEvent):void {
			var sharingNeedsAC:ArrayCollection = elementCollection 
			for each (var sn:SharingNeedVO in sharingNeedsAC) {
				
			}
		}
		
		protected override function itemsUpdated(colEvent:CollectionEvent):void {
			for each (var item:Object in colEvent.items) {
				extractElementVO(item,
					function anon(elemVO:ElementVO):void {
						// get sharing need VO
						// extract elements out of it
/*  						FlowMap.addSharingNeed(elemVO.id, ...) ; */
					}) ;
			}
		}
		
		private static var instance:SharingNeedModel;

		public function SharingNeedsHandler(access:Private) {
			super(sharingneeds) ;
			if (access != null)
				if (instance == null)
					instance = this;
			else
				throw new CairngormError( CairngormMessageCodes.SINGLETON_EXCEPTION, "SharingNeedsHandler" );
			init() ;
		}
		 
		/**
		 * Returns the Singleton instance of ChannelsModelLocator
		 */
		public static function getInstance() : SharingNeedsHandler {
			if (instance == null)
				instance = new SharingNeedsHandler( new Private );
			return instance;
		}
	}
}

/**
 * @private
 * Inner class which restricts contructor access to Private
 */
class Private {}