package com.mindalliance.channels.view.flowmap.data
{
	import com.yworks.graph.model.INode;

	public class RepositoryNodeData extends NodeData
	{
		public function RepositoryNodeData(node:INode, id:String)
		{
			super(node, id, NODE_TYPE_REPOSITORY);
		}
		
	}
}