package com.mindalliance.channels.model.flowmap
{
	import com.adobe.cairngorm.CairngormError;
	import com.adobe.cairngorm.CairngormMessageCodes;
	import com.mindalliance.channels.model.ElementListNames;
	import com.mindalliance.channels.util.ElementHelper;
	import com.mindalliance.channels.view.flowmap.FlowMap;
	import com.mindalliance.channels.vo.common.ElementVO;
	
	import flash.events.Event;
	
	import mx.events.CollectionEvent;
	import mx.events.CollectionEventKind;
	
	public class OrganizationModel extends BaseModel
	{
		
		private function organizationsRemoved(colEvent:CollectionEvent):void {
            for each (var item:Object in colEvent.items) {
				extractElementVO(item,
					function anon(elemVO:ElementVO):void {
							FlowMap.removeRepositoryOwner(elemVO.id) ;
					}) ;
			}
		}
		
		private function organizationsUpdated(colEvent:CollectionEvent):void {
			for each (var item:Object in colEvent.items) {
				examinePropertyChange(item, 
				function anon(elemVO:ElementVO, newValue:Object):void {
					FlowMap.renameRepositoryOwner(elemVO.id, newValue as String) ;
				}) ;
			}
		}
		
		protected function organizationChangeHandler(event:Event):void {
 			if (!(event is CollectionEvent))
 				return ;
			var colEvent:CollectionEvent = event as CollectionEvent ;
 			switch (colEvent.kind) {
				case CollectionEventKind.REMOVE:
					organizationsRemoved(colEvent) ;
				break ;
				case CollectionEventKind.UPDATE:
					organizationsUpdated(colEvent) ;
				break ;
			}
		}
		
		private function init():void {
			ElementHelper.installCollectionChangeListener(ElementListNames.ORGANIZATION_LIST_KEY, organizationChangeHandler) ;
		}
		
		private static var instance:OrganizationModel;

		public function OrganizationModel(access:Private) {
			super() ;
			if (access != null)
				if (instance == null)
					instance = this;
			else
				throw new CairngormError( CairngormMessageCodes.SINGLETON_EXCEPTION, "OrganizationModel" );
			init() ;
		}
		 
		/**
		 * Returns the Singleton instance of ChannelsModelLocator
		 */
		public static function getInstance() : OrganizationModel {
			if (instance == null)
				instance = new OrganizationModel( new Private );
			return instance;
		}
	}
}

/**
 * @private
 * Inner class which restricts contructor access to Private
 */
class Private {}