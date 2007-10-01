package com.mindalliance.channels.model.flowmap
{
	import flash.events.Event;
	import mx.events.PropertyChangeEvent;
	import com.mindalliance.channels.model.ChannelsModelLocator;
	import mx.binding.utils.ChangeWatcher;
	import com.mindalliance.channels.vo.common.ElementVO;
	import com.mindalliance.channels.model.ElementModel;
	
	[Bindable]
	public class ElementTracker
	{
		private var watchers:Object ;
		
		private var _receiver:Function ;
		
		private var _updateTrigger:Function ;
		
		private var _watchedProperty:String ;

		private var model:ChannelsModelLocator ;
		
		public function get watchedProperty():String {
			return _watchedProperty ;
		}
		
		public function get receiver():Function {
			return _receiver ;
		}
		
		public function set receiver(value:Function):void {
			_receiver = receiver ;
		}
		
		public function get updateTrigger():Function {
			return _updateTrigger ;
		}
		
		public function set updateTrigger(value:Function):void {
			_updateTrigger = value ;
		}
		
		public function ElementTracker(receiver:Function=null, updateTrigger:Function=null, watchedProperty:String='data') {
			watchers = new Object() ;
			_receiver = receiver ;
			_updateTrigger = updateTrigger ;
			_watchedProperty = watchedProperty ;
			model = ChannelsModelLocator.getInstance() ;
		}
		
		protected function elementChangeHandler(event:Event):void {
			if (event is PropertyChangeEvent)
				receiver((event as PropertyChangeEvent).newValue) ;
		}
		
		public function setupWatcher(elemVO:ElementVO):void {
			var elemModel:ElementModel = model.getElementModel(elemVO.id) as ElementModel ;

			if (elemModel.data)
				receiver(elemModel.data) ;

			var watcher:ChangeWatcher = watchers[elemVO.id] as ChangeWatcher ;

			if (watcher && watcher.isWatching())
				removeWatcher(elemVO.id) ;
			else {
				watcher = ChangeWatcher.watch(elemModel, watchedProperty, elementChangeHandler) ;
				watchers[elemVO.id] = watcher ;
				updateTrigger(elemVO) ;
			}
		}
		
		public function setupWatchers(array:Array):void {
			for each (var elemVO:ElementVO in array)
				setupWatcher(elemVO) ;
		}
		
		public function removeWatcher(elemID:String):void {
			var cw:ChangeWatcher = watchers[elemID] as ChangeWatcher ;
			if (cw) {
				cw.unwatch() ;
				delete watchers[elemID] ;
			}
		}
		
		public function removeAllWatchers():void {
			for (var elemID:String in watchers)
				removeWatcher(elemID) ;
		}
		
	}
}