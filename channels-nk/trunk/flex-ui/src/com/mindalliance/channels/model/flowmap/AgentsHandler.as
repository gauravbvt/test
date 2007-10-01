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
	
	public class AgentModel extends BaseCollectionChangeHandler
	{
		protected override function itemsAdded(colEvent:CollectionEvent):void {
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
		
		protected override function itemsRemoved(colEvent:CollectionEvent):void {
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
		
		protected override function collectionReset(colEvent:CollectionEvent):void {
			var agentAC:ArrayCollection = elementCollection ;
			for each (var agent:AgentVO in agentAC) {
					FlowMap.setAgent(agent.task.id, agent.role.id, agent.role.name) ;
			}
		}
		
		protected override function itemsUpdated(colEvent:CollectionEvent):void {
			trace('Message for Shashi: Agent collected update called against all odds!') ;
		}

		private static var instance:AgentModel;
		
		public function AgentsHandler(access:Private) {
			super("agents") ;

			if (access != null)
				if (instance == null)
					instance = this;
			else
				throw new CairngormError( CairngormMessageCodes.SINGLETON_EXCEPTION, "AgentsHandler" );
			
		}
		 
		/**
		 * Returns the Singleton instance of ChannelsModelLocator
		 */
		public static function getInstance() : AgentsHandler {
			if (instance == null)
				instance = new AgentsHandler( new Private );
			return instance;
		}
	}
}

/**
 * @private
 * Inner class which restricts contructor access to Private
 */
class Private {}