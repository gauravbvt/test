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
	import mx.collections.ArrayCollection;
	import com.mindalliance.channels.vo.common.CauseVO;
	import com.mindalliance.channels.model.ElementModel;
	
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
			if (event is CollectionEvent) {
				var colEvent:CollectionEvent = event as CollectionEvent ;
				switch (colEvent.kind) {
					case CollectionEventKind.RESET:
						var modelLocator:ChannelsModelLocator = ChannelsModelLocator.getInstance() ;
						var agentAC:ArrayCollection = modelLocator.getElementListModel("agents").data ;
						if (!agentAC)
							return ;
						for each (var agent:AgentVO in agentAC) {
							try {
								var roleName:String = modelLocator.getElementModel(agent.role.id).data.name ;
								FlowMap.setAgent(agent.task.id, agent.role.id, roleName) ;
							} catch (error:TypeError) {
								trace('Caught and ignored: ' + error.toString()) ;
							}
						}
					break ;
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
						trace('nothing') ;
/* 						Can this ever happen? */
					break ;
				}
			}
		}
		
		private function addRepository(phaseID:String, elemVO:ElementVO):void {
			FlowMap.addRepository(phaseID, elemVO.id, elemVO.name) ;
			var elementModel:ElementModel = ChannelsModelLocator.getInstance().getElementModel(elemVO.id) as ElementModel ;
			if (!elementModel)
				return ;
			var reposVO:RepositoryVO = elementModel.data as RepositoryVO ;
			if (!reposVO)
				return ;
			var orgVO:ElementVO = reposVO.organization ;
			if (!orgVO)
				return ;
			FlowMap.setRepositoryOwner(reposVO.id, orgVO.id, orgVO.name) ;
		}
		
		protected function repositoryChangeHandler(event:Event):void {
 			if (event is CollectionEvent) {
				var colEvent:CollectionEvent = event as CollectionEvent ;
				switch (colEvent.kind) {
					case CollectionEventKind.RESET:
						var modelLocator:ChannelsModelLocator = ChannelsModelLocator.getInstance() ;
						var reposAC:ArrayCollection = modelLocator.getElementListModel(ElementListNames.REPOSITORY_LIST_KEY).data ;
						for each (var reposVO:ElementVO in reposAC) {
							addRepository(FlowMap.defaultPhaseID, reposVO) ;
						}
					break ;
					case CollectionEventKind.ADD:
						colEvent.items.forEach(
							function procItem(item:*, i:int, a:Array):void {
								extractElementVO(item,
									function anon(elemVO:ElementVO):void {
										addRepository(FlowMap.defaultPhaseID, elemVO) ;
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
/* 								var propChangeEvent:PropertyChangeEvent = item as PropertyChangeEvent ;
								if (!propChangeEvent)
									return ;
								if (propChangeEvent */
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
			if (event is CollectionEvent) {
				var colEvent:CollectionEvent = event as CollectionEvent ;
				switch (colEvent.kind) {
					case CollectionEventKind.RESET:
						var modelLocator:ChannelsModelLocator = ChannelsModelLocator.getInstance() ;
						var eventAC:ArrayCollection = modelLocator.getElementListModel(ElementListNames.EVENT_LIST_KEY).data ;
						for each (var evt:ElementVO in eventAC) {
							FlowMap.addEvent(FlowMap.defaultPhaseID, evt.id, evt.name) ;
						}
					break ;
					case CollectionEventKind.ADD:
						colEvent.items.forEach(
							function procItem(item:*, i:int, a:Array):void {
								extractElementVO(item,
									function anon(elemVO:ElementVO):void {
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
					case CollectionEventKind.RESET:
						var modelLocator:ChannelsModelLocator = ChannelsModelLocator.getInstance() ;
						var taskAC:ArrayCollection = modelLocator.getElementListModel(ElementListNames.TASK_LIST_KEY).data ;
						for each (var task:ElementVO in taskAC) {
							FlowMap.addTask(FlowMap.defaultPhaseID, task.id, task.name) ;
						}
					break ;
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
		
		private function examinePropertyChange(event:Object, callback:Function, property:String='name', propertyChangeEventKind:String=PropertyChangeEventKind.UPDATE):void {
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