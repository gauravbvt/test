package com.mindalliance.channels.model.flowmap
{
	import com.adobe.cairngorm.CairngormError;
	import com.adobe.cairngorm.CairngormMessageCodes;
	import com.mindalliance.channels.model.ElementListNames;
	import com.mindalliance.channels.util.ElementHelper;
	import com.mindalliance.channels.view.flowmap.FlowMap;
	import com.mindalliance.channels.vo.common.ElementVO;
	
	import flash.events.Event;
	
	import mx.collections.ArrayCollection;
	import mx.events.CollectionEvent;
	import mx.events.CollectionEventKind;
	
	public class EventModel extends BaseModel
	{
		
		private function eventsAdded(colEvent:CollectionEvent):void {
            for each (var item:Object in colEvent.items) {
				extractElementVO(item,
					function anon(elemVO:ElementVO):void {
						FlowMap.addEvent(FlowMap.defaultPhaseID, elemVO.id, elemVO.name) ;
					}) ; 
			}
		}
		
		private function eventsRemoved(colEvent:CollectionEvent):void {
	        for each (var item:Object in colEvent.items) {
				extractElementVO(item,
					function anon(elemVO:ElementVO):void {
						FlowMap.removeEvent(elemVO.id) ;
					}) ; 
			}						
		}
		
		private function eventsUpdated(colEvent:CollectionEvent):void {
            for each (var item:Object in colEvent.items) {
				examinePropertyChange(item, 
					function anon(elemVO:ElementVO, newValue:Object):void {
						FlowMap.renameEvent(elemVO.id, newValue as String) ;
					}) ;
				
			}
		}
		
		private function eventsReset(colEvent:CollectionEvent):void {
			var eventAC:ArrayCollection = model.getElementListModel(ElementListNames.EVENT_LIST_KEY).data ;
			for each (var evt:ElementVO in eventAC) {
				FlowMap.addEvent(FlowMap.defaultPhaseID, evt.id, evt.name) ;
			}
		}
		
		
		protected function eventChangeHandler(event:Event):void {
			if (!(event is CollectionEvent))
				return ;
			var colEvent:CollectionEvent = event as CollectionEvent ;
			switch (colEvent.kind) {
				case CollectionEventKind.RESET:
					eventsReset(colEvent) ;
				break ;
				case CollectionEventKind.ADD:
					eventsAdded(colEvent) ;
				break ;
				case CollectionEventKind.REMOVE:
					eventsRemoved(colEvent) ;
				break ;
				case CollectionEventKind.UPDATE:
					eventsUpdated(colEvent) ;
				break ;
			}
		}
		
		private function init():void {
			ElementHelper.installCollectionChangeListener(ElementListNames.EVENT_LIST_KEY, eventChangeHandler) ;
		}
		
		private static var instance:EventModel;

		public function EventModel(access:Private) {
			super() ;
			if (access != null)
				if (instance == null)
					instance = this;
			else
				throw new CairngormError( CairngormMessageCodes.SINGLETON_EXCEPTION, "EventModel" );
			init() ;
		}
		 
		/**
		 * Returns the Singleton instance of ChannelsModelLocator
		 */
		public static function getInstance() : EventModel {
			if (instance == null)
				instance = new EventModel( new Private );
			return instance;
		}
	}
}

/**
 * @private
 * Inner class which restricts contructor access to Private
 */
class Private {}