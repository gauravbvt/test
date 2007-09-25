package com.mindalliance.channels.view.flowmap
{
	import com.yworks.graph.model.DefaultNode;
	import com.yworks.graph.model.DefaultPort;
	import com.yworks.graph.model.DefaultLabel;
	
	public class NodeData
	{
		
		public static const NODE_TYPE_GENERIC:String = "generic" ;
		public static const NODE_TYPE_TASK:String = "task" ;
		public static const NODE_TYPE_EVENT:String = "event" ;
		public static const NODE_TYPE_REPOSITORY:String = "repository" ;
		public static const NODE_TYPE_ROLE:String = "role" ;
		public static const NODE_TYPE_SHARING_NEED:String = "sharingneed" ;
		
		private var _node:DefaultNode ;
		private var _id:String ;
		private var _type:String = NODE_TYPE_GENERIC ;
		
		public function NodeData(node:DefaultNode, id:String, type:String) {
			_node = node ;
			_id = id ;
			_type = type ;
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
		
		public function get type():String {
			return _type ;
		}
		
	}
}