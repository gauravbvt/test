package com.mindalliance.channels.model.flowmap
{
	import com.mindalliance.channels.model.ChannelsModelLocator;
	import com.mindalliance.channels.model.ElementListNames;
	import com.mindalliance.channels.util.ElementHelper;
	
	import mx.collections.errors.CollectionViewError;
	import mx.events.CollectionEvent;
	import com.mindalliance.channels.vo.common.ElementVO;
	import flash.events.Event;
	import com.mindalliance.channels.vo.common.OccurrenceVO;
	import mx.collections.ArrayCollection;
	import com.mindalliance.channels.view.flowmap.FlowMap;
	import mx.binding.utils.ChangeWatcher;
	import com.mindalliance.channels.events.scenario.GetTaskEvent;
	import com.mindalliance.channels.util.CairngormHelper;
	import mx.events.PropertyChangeEvent;
	import mx.events.CollectionEventKind;
	import com.mindalliance.channels.model.ElementModel;
	import com.adobe.cairngorm.CairngormError;
	import com.adobe.cairngorm.CairngormMessageCodes;
	
	public class TaskModel extends BaseModel
	{		
		private var causationWatchers:Object ;
		
		private function init():void {
			causationWatchers = new Object() ;
			ElementHelper.installCollectionChangeListener(ElementListNames.TASK_LIST_KEY, taskChangeHandler) ;
		}
		
		protected function causationChangeHandler(event:Event):void {
			if (!(event is PropertyChangeEvent))
				return ;
			refreshCausation((event as PropertyChangeEvent).newValue as OccurrenceVO) ;
		}
		
		private function setupCausationWatcher(elemVO:ElementVO):void {
			var elemModel:ElementModel = model.getElementModel(elemVO.id) as ElementModel ;
			if (elemModel.data)
				refreshCausation(elemModel.data as OccurrenceVO) ;
			var watcher:ChangeWatcher = causationWatchers[elemVO.id] as ChangeWatcher ;
			if (!(watcher && watcher.isWatching())) {
				watcher = ChangeWatcher.watch(elemModel, 'data', causationChangeHandler) ;
				causationWatchers[elemVO.id] = watcher ;
				CairngormHelper.fireEvent(new GetTaskEvent(elemVO.id, null)) ;
			}
		}
		
		private function refreshCausation(occVO:OccurrenceVO):void {
			if (occVO && occVO.cause)
				FlowMap.addCausation(occVO.cause.id, occVO.id) ;
		}
		
		private function removeCausationWatcher(elemID:String):void {
			var cw:ChangeWatcher = causationWatchers[elemID] as ChangeWatcher ;
			if (cw) {
				cw.unwatch() ;
				delete causationWatchers[elemID] ;
			}
		}
		
		private function tasksReset():void {
			for (var elemID:String in causationWatchers)
				removeCausationWatcher(elemID) ;
			var taskAC:ArrayCollection = model.getElementListModel(ElementListNames.TASK_LIST_KEY).data ;
			var causationsToWatch:Array = new Array() ;
			for each (var task:ElementVO in taskAC) {
				causationsToWatch.push(task) ;
				FlowMap.addTask(FlowMap.defaultPhaseID, task.id, task.name) ;
			}
			for each (var causation:ElementVO in causationsToWatch)
					setupCausationWatcher(causation) ;
		}
		
		private function tasksAdded(colEvent:CollectionEvent):void {
 			var causationsToWatch:Array = new Array() ;
		    for each (var item:Object in colEvent.items) {
		         extractElementVO(item,
                        function anon(elemVO:ElementVO):void {
							causationsToWatch.push(elemVO) ;
                            FlowMap.addTask(FlowMap.defaultPhaseID, elemVO.id, elemVO.name) ;
                        }) ;
		    }
			for each (var causation:ElementVO in causationsToWatch)
					setupCausationWatcher(causation) ;
		}
		
		private function tasksRemoved(colEvent:CollectionEvent):void {
			for each (var item:Object in colEvent.items) {
				extractElementVO(item,
					function anon(taskVO:ElementVO):void {
						FlowMap.removeTask(taskVO.id) ;
						removeCausationWatcher(taskVO.id) ;
					}) ;
			}
		}
		
		private function tasksUpdated(colEvent:CollectionEvent):void {
			var causationsToWatch:Array = new Array() ;
			for each (var item:Object in colEvent.items) {
				examinePropertyChange(item, 
					function anon(elemVO:ElementVO, newValue:Object):void {
						causationsToWatch.push(elemVO) ;
						FlowMap.renameTask(elemVO.id, newValue as String) ;
					}) ;
			}
			for each (var causation:ElementVO in causationsToWatch)
				setupCausationWatcher(causation) ;
		}
		
		protected function taskChangeHandler(event:Event):void {
			if (!(event is CollectionEvent))
				return ;
			var colEvent:CollectionEvent = event as CollectionEvent ;
			switch (colEvent.kind) {
				case CollectionEventKind.RESET:
					tasksReset() ;
				break ;
				case CollectionEventKind.ADD:
					tasksAdded(colEvent) ;
				break ;
				case CollectionEventKind.REMOVE:
					tasksRemoved(colEvent) ;
				break ;
				case CollectionEventKind.UPDATE:
					tasksUpdated(colEvent) ;
				break ;
			}
		}
		
		private static var instance:TaskModel;

		public function TaskModel(access:Private) {
			super() ;
			if (access != null)
				if (instance == null)
					instance = this;
			else
				throw new CairngormError( CairngormMessageCodes.SINGLETON_EXCEPTION, "TaskModel" );
			init() ;
		}
		 
		/**
		 * Returns the Singleton instance of ChannelsModelLocator
		 */
		public static function getInstance() : TaskModel {
			if (instance == null)
				instance = new TaskModel( new Private );
			return instance;
		}
	}
}

/**
 * @private
 * Inner class which restricts contructor access to Private
 */
class Private {}