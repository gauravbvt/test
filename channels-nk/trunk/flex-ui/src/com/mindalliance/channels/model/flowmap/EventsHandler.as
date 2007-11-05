package com.mindalliance.channels.model.flowmap
{
	import com.mindalliance.channels.common.events.GetElementEvent;
	import com.mindalliance.channels.model.BaseCollectionChangeHandler;
	import com.mindalliance.channels.model.ElementListNames;
	import com.mindalliance.channels.util.CairngormHelper;
	import com.mindalliance.channels.view.flowmap.FlowMap;
	import com.mindalliance.channels.vo.common.ElementVO;
	import com.mindalliance.channels.vo.common.OccurrenceVO;
	
	import mx.collections.ArrayCollection;
	import mx.events.CollectionEvent;
	
	public class EventsHandler extends BaseCollectionChangeHandler
	{
				
		private function dispatchGetEvent(elemVO:ElementVO):void {
			CairngormHelper.fireEvent(new GetElementEvent(elemVO.id, null)) ;
		}
		
		private function refreshCausation(value:Object):void {
			var occVO:OccurrenceVO = value as OccurrenceVO ;
			if (occVO && occVO.cause)
				flowmap.causations.addCausation(occVO.cause.id, occVO.id) ;
		}
		
		protected override function itemsAdded(colEvent:CollectionEvent):void {
			var toWatch:Array = new Array() ;
            for each (var item:Object in colEvent.items) {
				extractElementVO(item,
					function anon(elemVO:ElementVO):void {
						toWatch.push(elemVO) ;
						flowmap.events.addEvent(flowmap.defaultPhaseID, elemVO.id, elemVO.name) ;
					}) ; 
			}
			tracker.setupWatchers(toWatch) ;
		}
		
		protected override function itemsRemoved(colEvent:CollectionEvent):void {
	        for each (var item:Object in colEvent.items) {
				extractElementVO(item,
					function anon(elemVO:ElementVO):void {
						tracker.removeWatcher(elemVO.id) ;
						flowmap.events.removeEvent(elemVO.id) ;
					}) ; 
			}						
		}
		
		protected override function itemsUpdated(colEvent:CollectionEvent):void {
            for each (var item:Object in colEvent.items) {
				examinePropertyChange(item, 
					function anon(elemVO:ElementVO, newValue:Object):void {
						flowmap.events.renameEvent(elemVO.id, newValue as String) ;
					}) ;
			}
		}
		
/* 		public static function forceResetWatchers():void {
			instance.tracker.removeAllWatchers() ;
			var toWatch:Array = new Array() ;
			for each (var evt:ElementVO in instance.elementCollection)
				toWatch.push(evt) ;
			instance.tracker.setupWatchers(toWatch) ;
		} */
		
		protected override function collectionReset(colEvent:CollectionEvent):void {
			tracker.removeAllWatchers() ;
			flowmap.events.removeAllEvents() ;
			
			var eventAC:ArrayCollection = elementCollection ;
			var toWatch:Array = new Array() ;
			for each (var evt:ElementVO in eventAC) {
				toWatch.push(evt) ;
				flowmap.events.addEvent(flowmap.defaultPhaseID, evt.id, evt.name) ;
			}
			tracker.setupWatchers(toWatch) ;
/* 			TasksHandler.forceResetWatchers() ; */
		}
		
		private var tracker:ElementTracker ;

		private var flowmap:FlowMap ;
		public function EventsHandler(value:FlowMap) {
			super(ElementListNames.EVENT_LIST_KEY) ;
			flowmap = value ;
			tracker = new ElementTracker(refreshCausation, dispatchGetEvent) ;
		}
	}
}