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
	
	public class OrganizationsHandler extends BaseCollectionChangeHandler
	{
		
		protected override function itemsRemoved(colEvent:CollectionEvent):void {
            for each (var item:Object in colEvent.items) {
				extractElementVO(item,
					function anon(elemVO:ElementVO):void {
							FlowMap.removeRepositoryOwner(elemVO.id) ;
					}) ;
			}
		}
		
		protected override function itemsUpdated(colEvent:CollectionEvent):void {
			for each (var item:Object in colEvent.items) {
				examinePropertyChange(item, 
				function anon(elemVO:ElementVO, newValue:Object):void {
					FlowMap.renameRepositoryOwner(elemVO.id, newValue as String) ;
				}) ;
			}
		}
		
		private static var instance:OrganizationsHandler;

		public function OrganizationsHandler(access:Private) {
			super(ElementListNames.ORGANIZATION_LIST_KEY) ;
			if (access != null)
				if (instance == null)
					instance = this;
			else
				throw new CairngormError( CairngormMessageCodes.SINGLETON_EXCEPTION, "OrganizationsHandler" );
		}
		 
		/**
		 * Returns the Singleton instance of ChannelsModelLocator
		 */
		public static function getInstance() : OrganizationsHandler {
			if (instance == null)
				instance = new OrganizationsHandler( new Private );
			return instance;
		}
	}
}

/**
 * @private
 * Inner class which restricts contructor access to Private
 */
class Private {}