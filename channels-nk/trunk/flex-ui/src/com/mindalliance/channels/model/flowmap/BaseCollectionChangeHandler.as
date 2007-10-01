package com.mindalliance.channels.model.flowmap
{
	import com.mindalliance.channels.model.ChannelsModelLocator;
	import com.mindalliance.channels.vo.common.ElementVO;
	
	import mx.events.PropertyChangeEvent;
	import mx.events.PropertyChangeEventKind;
	import com.mindalliance.channels.util.ElementHelper;
	import mx.events.CollectionEvent;
	import mx.collections.ArrayCollection;
	import flash.events.Event;
	import mx.events.CollectionEventKind;
	
	public class BaseCollectionChangeHandler
	{
		protected var model:ChannelsModelLocator ;
		
		private var _key:String ;
		
		public function BaseCollectionChangeHandler(key:String) {
			model = ChannelsModelLocator.getInstance() ;
			ElementHelper.installCollectionChangeListener(key, collectionChangeHandler) ;
			_key = key ;
		}

		protected function get key():String {
			return _key ;
		}
		
		protected function get elementCollection():ArrayCollection {
			return model.getElementListModel(key).data ;
		}
		
		protected function collectionChangeHandler(event:Event):void {
			if (!(event is CollectionEvent))
				return ;
			var colEvent:CollectionEvent = event as CollectionEvent ;
			switch (colEvent.kind) {
				case CollectionEventKind.RESET:
					collectionReset(colEvent) ;
				break ;
				case CollectionEventKind.ADD:
					itemsAdded(colEvent) ;
				break ;
				case CollectionEventKind.REMOVE:
					itemsRemoved(colEvent) ;
				break ;
				case CollectionEventKind.UPDATE:
					itemsUpdated(colEvent) ;
				break ;
			}
		}
		
		/* SUBCLASSES MUST OVERRIDE THESE METHODS  */
		protected function collectionReset(colEvent:CollectionEvent):void {
			
		}
		
		protected function itemsAdded(colEvent:CollectionEvent):void {
			
		}
		
		protected function itemsRemoved(colEvent:CollectionEvent):void {
			
		}
		
		protected function itemsUpdated(colEvent:CollectionEvent):void {
			
		}
		
		protected function extractElementVO(item:Object, callback:Function):void {
			var elemVO:ElementVO = item as ElementVO ;
			if (!elemVO)
				return ;
			callback(elemVO) ;
		}
		
		protected function examinePropertyChange(event:Object, 
												callback:Function, 
												property:String='name', 
												propertyChangeEventKind:String=PropertyChangeEventKind.UPDATE):void {
			var propChangeEvent:PropertyChangeEvent = event as PropertyChangeEvent ;
			if (!propChangeEvent)
				return ;
			if (propChangeEvent.kind != propertyChangeEventKind)
				return ;
			if (propChangeEvent.property == property) {
				var elemVO:ElementVO = propChangeEvent.source as ElementVO ;
				callback(elemVO, propChangeEvent.newValue) ;
			}
		}		
	}
}