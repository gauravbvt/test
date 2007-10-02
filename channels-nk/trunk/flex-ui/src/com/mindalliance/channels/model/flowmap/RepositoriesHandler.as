package com.mindalliance.channels.model.flowmap
{
	import com.adobe.cairngorm.CairngormError;
	import com.adobe.cairngorm.CairngormMessageCodes;
	import com.mindalliance.channels.events.people.GetOrganizationEvent;
	import com.mindalliance.channels.model.BaseCollectionChangeHandler;
	import com.mindalliance.channels.model.ElementListNames;
	import com.mindalliance.channels.model.ElementModel;
	import com.mindalliance.channels.util.CairngormHelper;
	import com.mindalliance.channels.util.ElementHelper;
	import com.mindalliance.channels.view.flowmap.FlowMap;
	import com.mindalliance.channels.vo.RepositoryVO;
	import com.mindalliance.channels.vo.common.ElementVO;
	
	import flash.events.Event;
	
	import mx.binding.utils.ChangeWatcher;
	import mx.collections.ArrayCollection;
	import mx.events.CollectionEvent;
	import mx.events.CollectionEventKind;
	import mx.events.PropertyChangeEvent;
	
	public class RepositoriesHandler extends BaseCollectionChangeHandler
	{

		private function dispatchGetOrganization(elemVO:ElementVO):void {
			CairngormHelper.fireEvent(new GetOrganizationEvent(elemVO.id, null)) ;
		}
		
		private function refreshOwner(elemVO:ElementVO):void {
			var reposVO:RepositoryVO = elemVO as RepositoryVO ;
			if (reposVO && reposVO.organization)
				flowmap.repositories.setRepositoryOwner(reposVO.id, reposVO.organization.id, reposVO.organization.name) ;
		}

		protected override function itemsAdded(colEvent:CollectionEvent):void {
			var toWatch:Array = new Array() ;
            for each (var item:Object in colEvent.items) {
				extractElementVO(item,
					function anon(elemVO:ElementVO):void {
						toWatch.push(elemVO) ;
						flowmap.repositories.addRepository(flowmap.defaultPhaseID, elemVO.id, elemVO.name) ;
					}) ; 
			}
			tracker.setupWatchers(toWatch) ;
		}

		protected override function itemsRemoved(colEvent:CollectionEvent):void {
            for each (var item:Object in colEvent.items) {
				extractElementVO(item,
					function anon(elemVO:ElementVO):void {
						tracker.removeWatcher(elemVO.id) ;
						flowmap.repositories.removeRepository(elemVO.id) ;
					}) ; 
			}
		}

		protected override function itemsUpdated(colEvent:CollectionEvent):void {
            for each (var item:Object in colEvent.items) {
				examinePropertyChange(item, 
					function anon(elemVO:ElementVO, newValue:Object):void {
						flowmap.repositories.renameRepository(elemVO.id, newValue as String) ;
					}) ;
			}
		}
		
		protected override function collectionReset(colEvent:CollectionEvent):void {
			tracker.removeAllWatchers() ;
			flowmap.repositories.removeAllRepositories() ;
			
			var reposAC:ArrayCollection = elementCollection ;
			var toWatch:Array = new Array() ;
			
			for each (var reposVO:ElementVO in reposAC) {
				toWatch.push(reposVO) ;
				flowmap.repositories.addRepository(flowmap.defaultPhaseID, reposVO.id, reposVO.name) ;
			}
			
			tracker.setupWatchers(toWatch) ;
		}

		private var tracker:ElementTracker ;

		private var flowmap:FlowMap ;
		
		public function RepositoriesHandler(value:FlowMap) {
			super(ElementListNames.REPOSITORY_LIST_KEY) ;
			flowmap = value ;
			tracker = new ElementTracker(refreshOwner, dispatchGetOrganization) ;
		}
	}
}