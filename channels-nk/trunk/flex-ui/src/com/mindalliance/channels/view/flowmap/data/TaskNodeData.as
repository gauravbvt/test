package com.mindalliance.channels.view.flowmap.data
{
	import com.yworks.graph.model.INode;

	public class TaskNodeData extends NodeData
	{
		private var _phaseID:String ;
		
		public function TaskNodeData(node:INode, id:String, phaseID:String)
		{
			super(node, id, NODE_TYPE_TASK);
		}
		
		public function get phaseID():String {
			return _phaseID ;
		}
		
		public function set phaseID(value:String):void {
			_phaseID = value ;
		}
		
	}
}