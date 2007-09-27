package com.mindalliance.channels.model.flowmap
{
	import com.mindalliance.channels.events.people.GetOrganizationEvent;
	import com.mindalliance.channels.model.ElementListNames;
	import com.mindalliance.channels.util.ElementHelper;
	import com.mindalliance.channels.view.flowmap.FlowMap;
	import com.mindalliance.channels.vo.RepositoryVO;
	import com.mindalliance.channels.vo.common.ElementVO;
	
	import flash.events.Event;
	
	import mx.events.CollectionEvent;
	import mx.collections.ArrayCollection;
	import mx.events.CollectionEventKind;
	import com.adobe.cairngorm.CairngormError;
	import com.adobe.cairngorm.CairngormMessageCodes;
	import mx.events.PropertyChangeEvent;
	import mx.binding.utils.ChangeWatcher;
	import com.mindalliance.channels.util.CairngormHelper;
	import com.mindalliance.channels.model.ElementModel;
	
	public class RepositoryModel extends BaseModel
	{

		private var ownerWatchers:Object ;
				
		protected function ownerChangeHandler(event:Event):void {
			if (!(event is PropertyChangeEvent))
				return ;
			refreshOwner((event as PropertyChangeEvent).newValue as RepositoryVO) ;
			var cw:ChangeWatcher = ownerWatchers[(event as PropertyChangeEvent).newValue.id] as ChangeWatcher ;
			if (cw)
				cw.unwatch() ;
		}
		
		private function setupOwnerWatcher(elemVO:ElementVO):void {
			var elemModel:ElementModel = model.getElementModel(elemVO.id) as ElementModel ;
			
			if (elemModel.data)
				refreshOwner(elemModel.data as RepositoryVO) ;

			var watcher:ChangeWatcher = ownerWatchers[elemVO.id] as ChangeWatcher ;
			if (!(watcher && watcher.isWatching())) {
				watcher = ChangeWatcher.watch(elemModel, 'data', ownerChangeHandler) ;
				ownerWatchers[elemVO.id] = watcher ;
				CairngormHelper.fireEvent(new GetOrganizationEvent(elemVO.id, null)) ;
			}
		}
		
		private function removeOwnerWatcher(elemID:String):void {
			var watcher:ChangeWatcher = ownerWatchers[elemID] as ChangeWatcher ;
			if (watcher) {
				watcher.unwatch() ;
				delete ownerWatchers[elemID] ;
			}
		}
		
		private function refreshOwner(reposVO:RepositoryVO):void {
			if (reposVO && reposVO.organization)
				FlowMap.setRepositoryOwner(reposVO.id, reposVO.organization.id, reposVO.organization.name) ;
		}


		private function repositoriesAdded(colEvent:CollectionEvent):void {
			var ownersToWatch:Array = new Array() ;
            for each (var item:Object in colEvent.items) {
				extractElementVO(item,
					function anon(elemVO:ElementVO):void {
						ownersToWatch.push(elemVO) ;
						FlowMap.addRepository(FlowMap.defaultPhaseID, elemVO.id, elemVO.name) ;
					}) ; 
			}
			for each (var owner:ElementVO in ownersToWatch)
				setupOwnerWatcher(owner) ;
		}

		private function repositoriesRemoved(colEvent:CollectionEvent):void {
            for each (var item:Object in colEvent.items) {
				extractElementVO(item,
					function anon(elemVO:ElementVO):void {
						FlowMap.removeRepository(elemVO.id) ;
						removeOwnerWatcher(elemVO.id) ;
					}) ; 
			}
		}

		private function repositoriesUpdated(colEvent:CollectionEvent):void {
			var ownersToWatch:Array = new Array() ;
            for each (var item:Object in colEvent.items) {
				examinePropertyChange(item, 
					function anon(elemVO:ElementVO, newValue:Object):void {
						ownersToWatch.push(elemVO) ;
						FlowMap.renameRepository(elemVO.id, newValue as String) ;
					}) ;
			}
			for each (var owner:ElementVO in ownersToWatch)
				setupOwnerWatcher(owner) ;
		}

		private function repositoriesReset(colEvent:CollectionEvent):void {
			for (var elemID:String in ownerWatchers)
				removeOwnerWatcher(elemID) ;
			var reposAC:ArrayCollection = model.getElementListModel(ElementListNames.REPOSITORY_LIST_KEY).data ;
			var ownersToWatch:Array = new Array() ;
			for each (var reposVO:ElementVO in reposAC) {
				ownersToWatch.push(reposVO) ;
				FlowMap.addRepository(FlowMap.defaultPhaseID, reposVO.id, reposVO.name) ;
			}
			for each (var owner:ElementVO in ownersToWatch)
				setupOwnerWatcher(owner) ;
		}

		protected function repositoryChangeHandler(event:Event):void {
 			if (!(event is CollectionEvent))
 				return ;
			var colEvent:CollectionEvent = event as CollectionEvent ;
			switch (colEvent.kind) {
				case CollectionEventKind.RESET:
					repositoriesReset(colEvent) ;
				break ;
				case CollectionEventKind.ADD:
					repositoriesAdded(colEvent) ;
				break ;
				case CollectionEventKind.REMOVE:
					repositoriesRemoved(colEvent) ;
				break;
				case CollectionEventKind.UPDATE:
					repositoriesUpdated(colEvent) ;
				break ;
			}
		}
		
		private function init():void {
			ownerWatchers = new Object() ;
			ElementHelper.installCollectionChangeListener(ElementListNames.REPOSITORY_LIST_KEY, repositoryChangeHandler) ;
		}
		
		private static var instance:RepositoryModel;

		public function RepositoryModel(access:Private) {
			super() ;
			if (access != null)
				if (instance == null)
					instance = this;
			else
				throw new CairngormError( CairngormMessageCodes.SINGLETON_EXCEPTION, "RepositoryModel" );
			init() ;
		}
		 
		/**
		 * Returns the Singleton instance of ChannelsModelLocator
		 */
		public static function getInstance() : RepositoryModel {
			if (instance == null)
				instance = new RepositoryModel( new Private );
			return instance;
		}
	}
}

/**
 * @private
 * Inner class which restricts contructor access to Private
 */
class Private {}