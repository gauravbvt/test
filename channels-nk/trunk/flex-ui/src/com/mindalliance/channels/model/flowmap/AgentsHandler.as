package com.mindalliance.channels.model.flowmap
{
	import com.mindalliance.channels.model.BaseCollectionChangeHandler;
	import com.mindalliance.channels.flowmap.view.FlowMap;
	import com.mindalliance.channels.vo.AgentVO;
	import com.mindalliance.channels.vo.common.ElementVO;
	
	import mx.collections.ArrayCollection;
	import mx.events.CollectionEvent;
	
	public class AgentsHandler extends BaseCollectionChangeHandler
	{
		protected override function itemsAdded(colEvent:CollectionEvent):void {
            for each (var item:Object in colEvent.items) {
				extractElementVO(item,
					function anon(elemVO:ElementVO):void {
						var avo:AgentVO = elemVO as AgentVO ;
						if (!avo)
							return ;
						flowmap.agents.setAgent(avo.task.id, avo.role.id, avo.role.name) ;
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
						flowmap.agents.removeAgent(avo.task.id, avo.role.id) ;
					}) ; 
			}
		}
		
		protected override function collectionReset(colEvent:CollectionEvent):void {
			var agentAC:ArrayCollection = elementCollection ;
			for each (var agent:AgentVO in agentAC) {
					flowmap.agents.setAgent(agent.task.id, agent.role.id, agent.role.name) ;
			}
		}
		
		protected override function itemsUpdated(colEvent:CollectionEvent):void {
			trace('Message for Shashi: Agents collectionChange UPDATE called against all odds!') ;
		}

		private var flowmap:FlowMap ;
		public function AgentsHandler(value:FlowMap) {
			super("agents") ;
			flowmap = value ;
		}
	}
}