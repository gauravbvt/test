package com.mindalliance.channels.flowmap.view.data
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