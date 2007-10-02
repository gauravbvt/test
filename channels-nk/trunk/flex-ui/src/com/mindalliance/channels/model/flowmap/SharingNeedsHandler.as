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
		
		public function SharingNeedsHandler(flowmap:FlowMap) {
			super("sharingneeds", flowmap:FlowMap) ;
		}
	}
}