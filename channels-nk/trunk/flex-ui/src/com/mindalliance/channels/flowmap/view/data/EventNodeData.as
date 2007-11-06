package com.mindalliance.channels.flowmap.view.data
{
	import com.yworks.graph.model.DefaultPort;
	import com.yworks.graph.model.DefaultNode;
	import com.yworks.graph.model.DefaultLabel;
	import com.yworks.graph.model.INode;

	public class EventNodeData extends NodeData
	{
		private var _startPhaseID:String ;
		
		private var _endPhaseID:String ;
		
		public function EventNodeData(node:INode, id:String)
		{
			super(node, id, NODE_TYPE_EVENT);
		}
		
		public function get startPhaseID():String {
			return _startPhaseID ;
		}
		
		public function set startPhaseID(value:String):void {
			_startPhaseID = value ;
		}
		
		public function get endPhaseID():String {
			return _endPhaseID ;
		}
		
		public function set endPhaseID(value:String):void {
			_endPhaseID = value ;
		}
	}
}