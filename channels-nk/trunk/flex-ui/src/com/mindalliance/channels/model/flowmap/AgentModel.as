package com.mindalliance.channels.model.flowmap
{
	import com.adobe.cairngorm.CairngormError;
	import com.adobe.cairngorm.CairngormMessageCodes;
	import com.mindalliance.channels.util.ElementHelper;
	import com.mindalliance.channels.view.flowmap.FlowMap;
	import com.mindalliance.channels.vo.AgentVO;
	import com.mindalliance.channels.vo.common.ElementVO;
	
	import flash.events.Event;
	
	import mx.collections.ArrayCollection;
	import mx.controls.Alert;
	import mx.events.CollectionEvent;
	import mx.events.CollectionEventKind;
	
	public class AgentModel extends BaseModel
	{

		private function agentsAdded(colEvent:CollectionEvent):void {
            for each (var item:Object in colEvent.items) {
				extractElementVO(item,
					function anon(elemVO:ElementVO):void {
						var avo:AgentVO = elemVO as AgentVO ;
						if (!avo)
							return ;
						FlowMap.setAgent(avo.task.id, avo.role.id, avo.role.name) ;
					}) ; 
			}
		}
		
		private function agentsRemoved(colEvent:CollectionEvent):void {
            for each (var item:Object in colEvent.items) {
				extractElementVO(item,
					function anon(elemVO:ElementVO):void {
						var avo:AgentVO = elemVO as AgentVO ;
						if (!avo)
							return ;
						FlowMap.removeAgent(avo.task.id, avo.role.id) ;
					}) ; 
			}
		}
		
		private function agentsReset(colEvent:CollectionEvent):void {
			var agentAC:ArrayCollection = model.getElementListModel("agents").data ;
			if (!agentAC)
				return ;
			for each (var agent:AgentVO in agentAC) {
					FlowMap.setAgent(agent.task.id, agent.role.id, agent.role.name) ;
			}
		}
		
		private function agentsUpdated(colEvent:CollectionEvent):void {
			trace('Message for Shashi: flowmap.AgentModel.agentsUpdated called against all odds!') ;
		}
		
		protected function agentChangeHandler(event:Event):void {
			if (!(event is CollectionEvent))
				return ; 
			var colEvent:CollectionEvent = event as CollectionEvent ;
			switch (colEvent.kind) {
				case CollectionEventKind.RESET:
					agentsReset(colEvent) ;
				break ;
				case CollectionEventKind.ADD:
					agentsAdded(colEvent) ;
				break ;
				case CollectionEventKind.REMOVE:
					agentsRemoved(colEvent) ;
				break ;
				case CollectionEventKind.UPDATE:
					agentsUpdated(colEvent) ;
				break ;
			}
		}
		
		private function init():void {
			ElementHelper.installCollectionChangeListener("agents", agentChangeHandler) ;
		}
		
		private static var instance:AgentModel;

		public function AgentModel(access:Private) {
			super() ;
			if (access != null)
				if (instance == null)
					instance = this;
			else
				throw new CairngormError( CairngormMessageCodes.SINGLETON_EXCEPTION, "AgentModel" );
			init() ;
		}
		 
		/**
		 * Returns the Singleton instance of ChannelsModelLocator
		 */
		public static function getInstance() : AgentModel {
			if (instance == null)
				instance = new AgentModel( new Private );
			return instance;
		}
	}
}

/**
 * @private
 * Inner class which restricts contructor access to Private
 */
class Private {}