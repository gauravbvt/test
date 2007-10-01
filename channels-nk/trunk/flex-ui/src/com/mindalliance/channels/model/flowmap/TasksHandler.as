package com.mindalliance.channels.model.flowmap
{
	import com.adobe.cairngorm.CairngormError;
	import com.adobe.cairngorm.CairngormMessageCodes;
	import com.mindalliance.channels.events.scenario.GetTaskEvent;
	import com.mindalliance.channels.model.ChannelsModelLocator;
	import com.mindalliance.channels.model.ElementListNames;
	import com.mindalliance.channels.model.ElementModel;
	import com.mindalliance.channels.util.CairngormHelper;
	import com.mindalliance.channels.util.ElementHelper;
	import com.mindalliance.channels.view.flowmap.FlowMap;
	import com.mindalliance.channels.vo.common.ElementVO;
	import com.mindalliance.channels.vo.common.OccurrenceVO;
	
	import flash.events.Event;
	
	import mx.binding.utils.ChangeWatcher;
	import mx.collections.ArrayCollection;
	import mx.collections.errors.CollectionViewError;
	import mx.events.CollectionEvent;
	import mx.events.CollectionEventKind;
	import mx.events.PropertyChangeEvent;
	
	public class TasksHandler extends BaseCollectionChangeHandler
	{		
		
		public static function forceResetWatchers():void {
			instance.tracker.removeAllWatchers() ;
			var tasks:Array = new Array() ;
			for each (var elem:ElementVO in instance.elementCollection)
				tasks.push(elem) ;
			instance.tracker.setupWatchers(tasks) ;
		}
		
		private function refreshCausation(value:Object):void {
			var occVO:OccurrenceVO = value as OccurrenceVO ;
			if (occVO && occVO.cause)
				FlowMap.addCausation(occVO.cause.id, occVO.id) ;
		}
		
		private function dispatchGetTask(elemVO:ElementVO):void {
			CairngormHelper.fireEvent(new GetTaskEvent(elemVO.id, null)) ;
		}
		
		protected override function collectionReset(colEvent:CollectionEvent):void {
			tracker.removeAllWatchers() ;
			FlowMap.removeAllTasks() ;
			
			var tasks:Array = new Array() ;

			for each (var elem:ElementVO in elementCollection) {
				tasks.push(elem) ;
				FlowMap.addTask(FlowMap.defaultPhaseID, elem.id, elem.name) ;
			}

			tracker.setupWatchers(tasks) ;
			
			EventsHandler.forceResetWatchers() ;
		}
		
		protected override function itemsAdded(colEvent:CollectionEvent):void {
 			var tasks:Array = new Array() ;
		    for each (var item:Object in colEvent.items) {
		         extractElementVO(item,
                        function anon(elemVO:ElementVO):void {
							tasks.push(elemVO) ;
                            FlowMap.addTask(FlowMap.defaultPhaseID, elemVO.id, elemVO.name) ;
                        }) ;
		    }
		    tracker.setupWatchers(tasks) ;
		}
		
		protected override function itemsRemoved(colEvent:CollectionEvent):void {
			for each (var item:Object in colEvent.items) {
				extractElementVO(item,
					function anon(taskVO:ElementVO):void {
						FlowMap.removeTask(taskVO.id) ;
						tracker.removeWatcher(taskVO.id) ;
					}) ;
			}
		}
		
		protected override function itemsUpdated(colEvent:CollectionEvent):void {
			var tasks:Array = new Array() ;
			for each (var item:Object in colEvent.items) {
				examinePropertyChange(item, 
					function anon(elemVO:ElementVO, newValue:Object):void {
						tasks.push(elemVO) ;
						FlowMap.renameTask(elemVO.id, newValue as String) ;
					}) ;
			}
			tracker.setupWatchers(tasks) ;
		}
				
		private static var instance:TasksHandler;

		private var tracker:ElementTracker ;
		
		public function TasksHandler(access:Private) {
			super(ElementListNames.TASK_LIST_KEY) ;

			if (access != null)
				if (instance == null)
					instance = this;
			else
				throw new CairngormError( CairngormMessageCodes.SINGLETON_EXCEPTION, "TasksHandler" );

			tracker = new ElementTracker(refreshCausation, dispatchGetTask) ;
		}
		 
		/**
		 * Returns the Singleton instance of ChannelsModelLocator
		 */
		public static function getInstance() : TasksHandler {
			if (instance == null)
				instance = new TasksHandler( new Private );
			return instance;
		}
	}
}

/**
 * @private
 * Inner class which restricts contructor access to Private
 */
class Private {}