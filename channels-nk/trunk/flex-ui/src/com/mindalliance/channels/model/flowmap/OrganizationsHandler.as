package com.mindalliance.channels.model.flowmap
{
	import com.adobe.cairngorm.CairngormError;
	import com.adobe.cairngorm.CairngormMessageCodes;
	import com.mindalliance.channels.model.BaseCollectionChangeHandler;
	import com.mindalliance.channels.model.ElementListNames;
	import com.mindalliance.channels.util.ElementHelper;
	import com.mindalliance.channels.flowmap.view.FlowMap;
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
							flowmap.repositories.removeRepositoryOwner(elemVO.id) ;
					}) ;
			}
		}
		
		protected override function itemsUpdated(colEvent:CollectionEvent):void {
			for each (var item:Object in colEvent.items) {
				examinePropertyChange(item, 
				function anon(elemVO:ElementVO, newValue:Object):void {
					flowmap.repositories.renameRepositoryOwner(elemVO.id, newValue as String) ;
				}) ;
			}
		}

		private var flowmap:FlowMap ;
		
		public function OrganizationsHandler(value:FlowMap) {
			super(ElementListNames.ORGANIZATION_LIST_KEY) ;
			flowmap = value ;
		}
	}
}