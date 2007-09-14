package com.mindalliance.channels.view.flowmap
{
	import com.mindalliance.channels.model.ChannelsModelLocator;
	import com.mindalliance.channels.model.ElementListModel;
	import com.mindalliance.channels.model.ElementListNames;
	import com.mindalliance.channels.vo.EventVO;
	import com.mindalliance.channels.vo.OrganizationVO;
	import com.mindalliance.channels.vo.RepositoryVO;
	import com.mindalliance.channels.vo.RoleVO;
	import com.mindalliance.channels.vo.common.ElementVO;
	
	import flash.events.Event;
	
	import mx.binding.utils.ChangeWatcher;
	import mx.events.CollectionEvent;
	import mx.events.CollectionEventKind;
	
	public class FlowMapCairngormInterfacer
	{
		private var _repositoryListWatcher:ChangeWatcher ;
		private var _taskListWatcher:ChangeWatcher ;
		private var _organizationListWatcher:ChangeWatcher ;
		private var _eventListWatcher:ChangeWatcher ;
		private var _roleListWatcher:ChangeWatcher ;
		
		private static var instance:FlowMapCairngormInterfacer ;
		
		public static function initialize():void {
			FlowMapCairngormInterfacer.getInstance() ;
		}
		
		public static function getInstance():FlowMapCairngormInterfacer {
			if (instance == null) {
				instance = new FlowMapCairngormInterfacer() ;
				instance.init() ;
			}
			return instance ;
		}
		
		public function init():void {
			_taskListWatcher = createElementListModelWatcher(ElementListNames.TASK_LIST_KEY, taskChangeHandler) ;
			_repositoryListWatcher = createElementListModelWatcher(ElementListNames.REPOSITORY_LIST_KEY, repositoryChangeHandler) ;
			_eventListWatcher = createElementListModelWatcher(ElementListNames.EVENT_LIST_KEY, eventChangeHandler) ;
			_organizationListWatcher = createElementListModelWatcher(ElementListNames.ORGANIZATION_LIST_KEY, organizationChangeHandler) ;
			_roleListWatcher = createElementListModelWatcher(ElementListNames.ROLE_LIST_KEY, roleChangeHandler) ;
		}
		
		private function createElementListModelWatcher(modelKey:String, handler:Function):ChangeWatcher {
			var elementListModel:ElementListModel = ChannelsModelLocator.getInstance().getElementListModel(modelKey) ;
			//var watcher:ChangeWatcher = ChangeWatcher.watch(elementListModel, ['data'], handler) ;
			elementListModel.data.addEventListener(CollectionEvent.COLLECTION_CHANGE, handler);
			return null ;
		} 
		
		protected function repositoryChangeHandler(event:Event):void {
			if (event is CollectionEvent) {
				var colEvent:CollectionEvent = event as CollectionEvent ;
				var items:Array = colEvent.items ;
				for (var i:int = 0 ; i < items.length ; i++) {
					var reposVO:ElementVO = items[i] as ElementVO ;
					if (!reposVO) continue ;
					switch (colEvent.kind) {
						case CollectionEventKind.ADD:
							FlowMap.addRepository(FlowMap.defaultPhaseID, reposVO.id, reposVO.name) ;
						break ;
						case CollectionEventKind.REMOVE:
							FlowMap.removeRepository(reposVO.id) ;
						break ;
						case CollectionEventKind.UPDATE:
							FlowMap.renameRepository(reposVO.id, reposVO.name) ;
/* 							FlowMap.setOrganization(reposVO.id, reposVO.organization.id, reposVO.organization.name) ; */
						break ;
						
					}
				}
			}
		}
		
		protected function organizationChangeHandler(event:Event):void {
			if (event is CollectionEvent) {
				var colEvent:CollectionEvent = event as CollectionEvent ;
				var items:Array = colEvent.items ;
				for (var i:int = 0 ; i < items.length ; i++) {
					var organizationVO:ElementVO = items[i] as ElementVO ;
					if (!organizationVO) continue ;
/* 					switch (colEvent.kind) {
						case CollectionEventKind.REMOVE:
							FlowMap.removeOrganization(organizationVO.id) ;
						break ;
						case CollectionEventKind.UPDATE:
							FlowMap.renameOrganization(organizationVO.id, organizationVO.name) ;
						break ;
						
					} */
				}
			}
		}

		protected function eventChangeHandler(event:Event):void {
			if (event is CollectionEvent) {
				var colEvent:CollectionEvent = event as CollectionEvent ;
				var items:Array = colEvent.items ;
				for (var i:int = 0 ; i < items.length ; i++) {
					var eventVO:ElementVO = items[i] as ElementVO ;
					if (!eventVO) continue ;
					switch (colEvent.kind) {
						case CollectionEventKind.ADD:
							FlowMap.addEvent(FlowMap.defaultPhaseID, eventVO.id, eventVO.name) ;
							// TODO: SETUP CAUSATION HERE
						break ;
						case CollectionEventKind.REMOVE:
							FlowMap.removeEvent(eventVO.id) ;
						break ;
						case CollectionEventKind.UPDATE:
							FlowMap.renameEvent(eventVO.id, eventVO.name) ;
						break ;
					}
				}
			}
		}
		
		// TODO: WHERE IS THE AGENT STUFF HANDLED?
		protected function roleChangeHandler(event:Event):void {
			if (event is CollectionEvent) {
				var colEvent:CollectionEvent = event as CollectionEvent ;
				var items:Array = colEvent.items ;
				for (var i:int = 0 ; i < items.length ; i++) {
					var roleVO:ElementVO = items[i] as ElementVO ;
					if (!roleVO) continue ;
					switch (colEvent.kind) {
						case CollectionEventKind.REMOVE:
							FlowMap.removeRole(roleVO.id) ;
						break ;
						case CollectionEventKind.UPDATE:
							FlowMap.renameRole(roleVO.id, roleVO.name) ;
						break ;
						
					}
				}
			}
		}
		
		protected function taskChangeHandler(event:Event):void {
			if (event is CollectionEvent) {
				var colEvent:CollectionEvent = event as CollectionEvent ;
				var items:Array = colEvent.items ;
				for (var i:int = 0 ; i < items.length ; i++) {
					var taskVO:ElementVO = items[i] as ElementVO ;
					if (!ElementVO) continue ;
					switch (colEvent.kind) {
						case CollectionEventKind.ADD:
							FlowMap.addTask(FlowMap.defaultPhaseID, taskVO.id, taskVO.name) ;
						break ;
						case CollectionEventKind.REMOVE:
							FlowMap.removeTask(taskVO.id) ;
						break ;
						case CollectionEventKind.UPDATE:
							FlowMap.renameTask(taskVO.id, taskVO.name) ;
						break ;
						
					}
				}
			}
		}
		
	}
}