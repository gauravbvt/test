package com.mindalliance.channels.model.flowmap
{
	import com.adobe.cairngorm.CairngormError;
	import com.adobe.cairngorm.CairngormMessageCodes;
	import com.mindalliance.channels.events.scenario.GetEventEvent;
	import com.mindalliance.channels.model.ElementListNames;
	import com.mindalliance.channels.util.CairngormHelper;
	import com.mindalliance.channels.util.ElementHelper;
	import com.mindalliance.channels.view.flowmap.FlowMap;
	import com.mindalliance.channels.vo.common.ElementVO;
	import com.mindalliance.channels.vo.common.OccurrenceVO;
	
	import flash.events.Event;
	
	import mx.collections.ArrayCollection;
	import mx.events.CollectionEvent;
	import mx.events.CollectionEventKind;
	
	public class EventsHandler extends BaseCollectionChangeHandler
	{
				
		private function dispatchGetEvent(elemVO:ElementVO):void {
			CairngormHelper.fireEvent(new GetEventEvent(elemVO.id, null)) ;
		}
		
		private function refreshCausation(value:Object):void {
			var occVO:OccurrenceVO = value as OccurrenceVO ;
			if (occVO && occVO.cause)
				FlowMap.addCausation(occVO.cause.id, occVO.id) ;
		}
		
		protected override function itemsAdded(colEvent:CollectionEvent):void {
			var toWatch:Array = new Array() ;
            for each (var item:Object in colEvent.items) {
				extractElementVO(item,
					function anon(elemVO:ElementVO):void {
						toWatch.push(elemVO) ;
						FlowMap.addEvent(FlowMap.defaultPhaseID, elemVO.id, elemVO.name) ;
					}) ; 
			}
			tracker.setupWatchers(toWatch) ;
		}
		
		protected override function itemsRemoved(colEvent:CollectionEvent):void {
	        for each (var item:Object in colEvent.items) {
				extractElementVO(item,
					function anon(elemVO:ElementVO):void {
						tracker.removeWatcher(elemVO.id) ;
						FlowMap.removeEvent(elemVO.id) ;
					}) ; 
			}						
		}
		
		protected override function itemsUpdated(colEvent:CollectionEvent):void {
            for each (var item:Object in colEvent.items) {
				examinePropertyChange(item, 
					function anon(elemVO:ElementVO, newValue:Object):void {
						FlowMap.renameEvent(elemVO.id, newValue as String) ;
					}) ;
			}
		}
		
		public static function forceResetWatchers():void {
			instance.tracker.removeAllWatchers() ;
			var toWatch:Array = new Array() ;
			for each (var evt:ElementVO in instance.elementCollection)
				toWatch.push(evt) ;
			instance.tracker.setupWatchers(toWatch) ;
		}
		
		protected override function collectionReset(colEvent:CollectionEvent):void {
			tracker.removeAllWatchers() ;
			FlowMap.removeAllEvents() ;
			
			var eventAC:ArrayCollection = elementCollection ;
			var toWatch:Array = new Array() ;
			for each (var evt:ElementVO in eventAC) {
				toWatch.push(evt) ;
				FlowMap.addEvent(FlowMap.defaultPhaseID, evt.id, evt.name) ;
			}
			tracker.setupWatchers(toWatch) ;
			TasksHandler.forceResetWatchers() ;
		}
		
		private static var instance:EventsHandler;

		private var tracker:ElementTracker ;

		public function EventsHandler(access:Private) {
			super(ElementListNames.EVENT_LIST_KEY) ;

			if (access != null)
				if (instance == null)
					instance = this;
			else
				throw new CairngormError( CairngormMessageCodes.SINGLETON_EXCEPTION, "EventsHandler" );
			
			tracker = new ElementTracker(refreshCausation, dispatchGetEvent) ;
		}
		 
		/**
		 * Returns the Singleton instance of ChannelsModelLocator
		 */
		public static function getInstance() : EventsHandler {
			if (instance == null)
				instance = new EventsHandler( new Private );
			return instance;
		}
	}
}

/**
 * @private
 * Inner class which restricts contructor access to Private
 */
class Private {}