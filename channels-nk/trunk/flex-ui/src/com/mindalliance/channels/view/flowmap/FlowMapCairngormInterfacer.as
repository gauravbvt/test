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
	import mx.events.PropertyChangeEvent;
	import mx.events.PropertyChangeEventKind;
	import com.mindalliance.channels.vo.TaskVO;
	import mx.validators.ValidationResult;
	import com.mindalliance.channels.vo.AgentVO;
	import mx.controls.Alert;
	
	public class FlowMapCairngormInterfacer
	{
		
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
			createElementListModelWatcher(ElementListNames.TASK_LIST_KEY, taskChangeHandler) ;
			createElementListModelWatcher(ElementListNames.REPOSITORY_LIST_KEY, repositoryChangeHandler) ;
			createElementListModelWatcher(ElementListNames.EVENT_LIST_KEY, eventChangeHandler) ;
			createElementListModelWatcher(ElementListNames.ORGANIZATION_LIST_KEY, organizationChangeHandler) ;
 			createElementListModelWatcher("agents", agentChangeHandler) ;
		}
		
		private function createElementListModelWatcher(modelKey:String, handler:Function):void {
			var elementListModel:ElementListModel = ChannelsModelLocator.getInstance().getElementListModel(modelKey) ;
			elementListModel.data.addEventListener(CollectionEvent.COLLECTION_CHANGE, handler);
		} 
		
		protected function agentChangeHandler(event:Event):void {
			trace(event.type) ;
			if (event is CollectionEvent) {
				var colEvent:CollectionEvent = event as CollectionEvent ;
				trace(colEvent.kind) ;
				switch (colEvent.kind) {
					case CollectionEventKind.ADD:
						colEvent.items.forEach(
							function procItem(item:*, i:int, a:Array):void {
								extractElementVO(item,
									function anon(elemVO:ElementVO):void {
										var avo:AgentVO = elemVO as AgentVO ;
										if (!avo)
											return ;
										FlowMap.setAgent(avo.task.id, avo.role.id, avo.role.name) ;
									}) ; 
							}) ;
					break ;
					case CollectionEventKind.REMOVE:
						colEvent.items.forEach(
							function procItem(item:*, i:int, a:Array):void {
								extractElementVO(item,
									function anon(elemVO:ElementVO):void {
										var avo:AgentVO = elemVO as AgentVO ;
										if (!avo)
											return ;
										FlowMap.removeAgent(avo.task.id, avo.role.id) ;
									}) ; 
							}) ;
					break ;
					case CollectionEventKind.UPDATE:
/* 						Can this ever happen? */
					break ;
				}
			}
		}
		
		protected function repositoryChangeHandler(event:Event):void {
 			if (event is CollectionEvent) {
				var colEvent:CollectionEvent = event as CollectionEvent ;
				switch (colEvent.kind) {
					case CollectionEventKind.ADD:
						colEvent.items.forEach(
							function procItem(item:*, i:int, a:Array):void {
								extractElementVO(item,
									function anon(elemVO:ElementVO):void {
										FlowMap.addRepository(FlowMap.defaultPhaseID, elemVO.id, elemVO.name) ;
										// This does not seem to work.
										/* var orgVO:ElementVO = (elemVO as RepositoryVO).organization ;
										FlowMap.setRepositoryOwner(elemVO.id, orgVO.id, orgVO.name) ; */
									}) ; 
							}) ;
					break ;
					case CollectionEventKind.REMOVE:
						colEvent.items.forEach(
							function procItem(item:*, i:int, a:Array):void {
								extractElementVO(item,
									function anon(elemVO:ElementVO):void {
										FlowMap.removeRepository(elemVO.id) ;
									}) ; 
							}) ;
					break;
					case CollectionEventKind.UPDATE:
						colEvent.items.forEach(
							function procItem(item:*, i:int, a:Array):void {
								examinePropertyChange(item, 
									function anon(elemVO:ElementVO, newValue:Object):void {
										var orgVO:ElementVO = newValue as ElementVO ;
										if (!orgVO)
											return ;
										FlowMap.setRepositoryOwner(elemVO.id, orgVO.id, orgVO.name) ;
									}, 'organization') ;
							}) ;
					break ;
				}
			} 
		}
		
		protected function organizationChangeHandler(event:Event):void {
 			if (event is CollectionEvent) {
				var colEvent:CollectionEvent = event as CollectionEvent ;
 				switch (colEvent.kind) {
					case CollectionEventKind.REMOVE:
						colEvent.items.forEach(
							function procItem(item:*, i:int, a:Array):void {
								extractElementVO(item,
									function anon(elemVO:ElementVO):void {
											FlowMap.removeRepositoryOwner(elemVO.id) ;
									}) ;
							}) ;
					break ;
					case CollectionEventKind.UPDATE:
						colEvent.items.forEach(
							function procItem(item:*, i:int, a:Array):void {
								examinePropertyChange(item, 
									function anon(elemVO:ElementVO, newValue:Object):void {
										FlowMap.renameRepositoryOwner(elemVO.id, newValue as String) ;
									}) ;
							}) ;
					break ;
				}
			} 
		}

		protected function eventChangeHandler(event:Event):void {
			trace(event.type) ;
			if (event is CollectionEvent) {
				var colEvent:CollectionEvent = event as CollectionEvent ;
				trace(colEvent.kind) ;
				switch (colEvent.kind) {
					case CollectionEventKind.ADD:
						colEvent.items.forEach(
							function procItem(item:*, i:int, a:Array):void {
								trace('procItem ' + i) ;
								extractElementVO(item,
									function anon(elemVO:ElementVO):void {
										trace('adding '+elemVO.id + ' ' + elemVO.name) ;
										FlowMap.addEvent(FlowMap.defaultPhaseID, elemVO.id, elemVO.name) ;
									}) ; 
							}) ;
					break ;
					case CollectionEventKind.REMOVE:
						colEvent.items.forEach(
							function procItem(item:*, i:int, a:Array):void {
								extractElementVO(item,
									function anon(elemVO:ElementVO):void {
										FlowMap.removeEvent(elemVO.id) ;
									}) ; 
							}) ;						
					break ;
					case CollectionEventKind.UPDATE:
						colEvent.items.forEach(
							function procItem(item:*, i:int, a:Array):void {
								examinePropertyChange(item, 
									function anon(elemVO:ElementVO, newValue:Object):void {
										FlowMap.renameEvent(elemVO.id, newValue as String) ;
									}) ;
							}) ;
					break ;
				}
			}
		}
				
		protected function taskChangeHandler(event:Event):void {
			if (event is CollectionEvent) {
				var colEvent:CollectionEvent = event as CollectionEvent ;
				switch (colEvent.kind) {
					case CollectionEventKind.ADD:
						colEvent.items.forEach(
							function procItem(item:*, i:int, a:Array):void {
								extractElementVO(item,
									function anon(taskVO:ElementVO):void {
										FlowMap.addTask(FlowMap.defaultPhaseID, taskVO.id, taskVO.name) ;
									}) ;
							}) ;
					break ;
					case CollectionEventKind.REMOVE:
						colEvent.items.forEach(
							function procItem(item:*, i:int, a:Array):void {
								extractElementVO(item,
									function anon(taskVO:ElementVO):void {
										FlowMap.removeTask(taskVO.id) ;
									}) ;
							}) ;
					break ;
					case CollectionEventKind.UPDATE:
						colEvent.items.forEach(
							function procItem(item:*, i:int, a:Array):void {
								examinePropertyChange(item, 
									function anon(elemVO:ElementVO, newValue:Object):void {
										FlowMap.renameTask(elemVO.id, newValue as String) ;
									}) ;
							}) ;
					break ;
				}
			}
		}
		
		private function extractElementVO(item:Object, callback:Function):void {
			var elemVO:ElementVO = item as ElementVO ;
			if (!elemVO)
				return ;
			callback(elemVO) ;
		}
		
		private function examinePropertyChange(event:Object, callback:Function, property:String='name'):void {
			var propChangeEvent:PropertyChangeEvent = event as PropertyChangeEvent ;
			if (!propChangeEvent)
				return ;
			if (propChangeEvent.kind != PropertyChangeEventKind.UPDATE)
				return ;
			if (propChangeEvent.property == property) {
				var elemVO:ElementVO = propChangeEvent.source as ElementVO ;
				callback(elemVO, propChangeEvent.newValue) ;
			}
		}
						
	}
}