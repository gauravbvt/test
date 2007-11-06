package com.mindalliance.channels.model.flowmap
{
	import com.adobe.cairngorm.CairngormError;
	import com.adobe.cairngorm.CairngormMessageCodes;
	import com.mindalliance.channels.scenario.events.GetTaskEvent;
	import com.mindalliance.channels.model.BaseCollectionChangeHandler;
	import com.mindalliance.channels.model.ChannelsModelLocator;
	import com.mindalliance.channels.model.ElementListNames;
	import com.mindalliance.channels.model.ElementModel;
	import com.mindalliance.channels.util.CairngormHelper;
	import com.mindalliance.channels.util.ElementHelper;
	import com.mindalliance.channels.flowmap.view.FlowMap;
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
		
/* 		public static function forceResetWatchers():void {
			instance.tracker.removeAllWatchers() ;
			var tasks:Array = new Array() ;
			for each (var elem:ElementVO in instance.elementCollection)
				tasks.push(elem) ;
			instance.tracker.setupWatchers(tasks) ;
		} */
		
		private function refreshCausation(value:Object):void {
			var occVO:OccurrenceVO = value as OccurrenceVO ;
			if (occVO && occVO.cause)
				flowmap.causations.addCausation(occVO.cause.id, occVO.id) ;
		}
		
		private function dispatchGetTask(elemVO:ElementVO):void {
			CairngormHelper.fireEvent(new GetTaskEvent(elemVO.id, null)) ;
		}
		
		protected override function collectionReset(colEvent:CollectionEvent):void {
			tracker.removeAllWatchers() ;
			flowmap.tasks.removeAllTasks() ;
			
			var tasks:Array = new Array() ;

			for each (var elem:ElementVO in elementCollection) {
				tasks.push(elem) ;
				flowmap.tasks.addTask(flowmap.defaultPhaseID, elem.id, elem.name) ;
			}

			tracker.setupWatchers(tasks) ;
			
/* 			EventsHandler.forceResetWatchers() ; */
		}
		
		protected override function itemsAdded(colEvent:CollectionEvent):void {
 			var tasks:Array = new Array() ;
		    for each (var item:Object in colEvent.items) {
		         extractElementVO(item,
                        function anon(elemVO:ElementVO):void {
							tasks.push(elemVO) ;
                            flowmap.tasks.addTask(flowmap.defaultPhaseID, elemVO.id, elemVO.name) ;
                        }) ;
		    }
		    tracker.setupWatchers(tasks) ;
		}
		
		protected override function itemsRemoved(colEvent:CollectionEvent):void {
			for each (var item:Object in colEvent.items) {
				extractElementVO(item,
					function anon(taskVO:ElementVO):void {
						flowmap.tasks.removeTask(taskVO.id) ;
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
						flowmap.tasks.renameTask(elemVO.id, newValue as String) ;
					}) ;
			}
			tracker.setupWatchers(tasks) ;
		}
				
		private var tracker:ElementTracker ;
		
		private var flowmap:FlowMap ;
		public function TasksHandler(value:FlowMap) {
			super(ElementListNames.TASK_LIST_KEY) ;
			flowmap = value ;
			tracker = new ElementTracker(refreshCausation, dispatchGetTask) ;
		}
		 
	}
}