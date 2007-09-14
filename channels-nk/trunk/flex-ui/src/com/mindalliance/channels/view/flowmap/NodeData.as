package com.mindalliance.channels.view.flowmap
{
	import com.yworks.graph.model.DefaultNode;
	import com.yworks.graph.model.DefaultPort;
	import com.yworks.graph.model.DefaultLabel;
	
	public class NodeData
	{
		private var _node:DefaultNode ;
		private var _id:String ;
		
		public function NodeData(node:DefaultNode, id:String) {
			_node = node ;
			_id = id ;
		}
		
		public function set node(value:DefaultNode):void {
			_node = value ;
		}
		
		public function get node():DefaultNode {
			return _node ;
		}
		
		public function set id(value:String):void {
			_id = value ;
		}
		
		public function get id():String {
			return _id;
		}

	}
}