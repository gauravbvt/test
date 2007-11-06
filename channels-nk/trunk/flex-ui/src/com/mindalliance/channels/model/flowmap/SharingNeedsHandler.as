package com.mindalliance.channels.model.flowmap
{
	import com.mindalliance.channels.flowmap.view.FlowMap;
	import com.mindalliance.channels.vo.SharingNeedVO;
	import com.mindalliance.channels.vo.common.ElementVO;
	
	import mx.collections.ArrayCollection;
	import mx.events.CollectionEvent;
	
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
		
		private var flowmap:FlowMap ;
		public function SharingNeedsHandler(value:FlowMap) {
			super("sharingneeds") ;
			flowmap = value ;
		}
	}
}