package com.mindalliance.channels.view.flowmap.data
{
	import com.yworks.graph.model.INode;
	
	import mx.events.IndexChangedEvent;

	public class RoleNodeData extends NodeData
	{
		public function RoleNodeData(node:INode, id:String)
		{
			super(node, id, NODE_TYPE_ROLE);
		}
		
	}
}